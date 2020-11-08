package services.download;

import gui.components.listener.ComponentEvent;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import model.exception.ProductNotAvailableException;
import model.list.ProductListDTO;
import model.listeners.ComponentChangeListener;
import model.products.ProductDTO;
import org.apache.http.client.HttpResponseException;
import org.apache.logging.log4j.LogManager;
import utils.FileUtils;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

import static utils.DownloadConfiguration.getDownloadModeLocation;
import static utils.DownloadConfiguration.getProductDownloadFolderLocation;

public class CopernicusDownloader implements Downloader, Runnable {

    static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(CopernicusDownloader.class.getName());

    private volatile  List<DownloadItemThread> downloading;
    private volatile ConcurrentLinkedQueue<DownloadItem> queue;
    private volatile ObservableList<DownloadItem> historical;
    private volatile ObservableList<DownloadItem> downloadingObservable;

    private volatile int filesDownloading;
    private volatile int downloadAttempts;
    private final List<ComponentChangeListener> listeners;
    private final int maxFilesDownloading;
    private DoubleProperty timeLeft;


    public CopernicusDownloader(int maxFilesDownloading) {
        this.timeLeft = new SimpleDoubleProperty(0.0);
        this.maxFilesDownloading = maxFilesDownloading;
        this.queue = new ConcurrentLinkedQueue<>();
        this.downloading = new ArrayList<>();
        historical = FXCollections.observableArrayList();
        downloadingObservable = FXCollections.observableArrayList();
        filesDownloading = 0;
        downloadAttempts = 5;
        listeners = new ArrayList<>();
    }

    @Override
    public synchronized void download(ProductListDTO productList) {
        productList.getValidProducts().forEach(p->add(new DownloadItem(p)));
        productList.getReferenceProducts().forEach(p->add(new DownloadItem(p)));
    }

    @Override
    public synchronized void download(ProductDTO productDTO) {
        add(new DownloadItem(productDTO));
    }

    @Override
    public DoubleProperty timeLeftProperty() {
        return timeLeft;
    }

    @Override
    public boolean isDownloading() {
        return !downloadingObservable.isEmpty();
    }

    private synchronized void add(DownloadItem item) {
        if (FileUtils.productExists(item.getProductDTO().getTitle()) || queue.contains(item)  || downloadingObservable.contains(item)|| historical.contains(item) )
            return;
        queue.add(item);
        historical.add(item);
    }

    public void addListener(ComponentChangeListener l) {
        this.listeners.add(l);
    }

    private void fireEvent(ComponentEvent e) {
        listeners.forEach(l->l.onComponentChange(e));
    }

    private synchronized void addDownloadingFile() {
        filesDownloading++;
    }

    private synchronized void removeDownloadFile() {
        filesDownloading--;
    }

    @Override
    public void run() {
        while (true) {
            if ((filesDownloading < getNumMaxOfFileDownloading() && queue.size() > 0) && downloadAttempts>0) {
                addDownloadingFile();
                processItem();
            }

            if (filesDownloading == 0 && downloadAttempts == 0) {
                logger.atError().log("An error has occurred. Not able to download from Copernicus API. Try later");
                queue.clear();
                Platform.runLater(()->historical.clear());
                restoreAttempts();
            }

            //Set the time left
            //Must be called in a JavaFX Thread
            Platform.runLater(()->{
                timeLeft.set(calculateMaxTimeLeft());
            });

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private double calculateMaxTimeLeft() {
        double time = 0;
        for (DownloadItemThread t : downloading) {
            if (t.getTimeLeft() > time)
                time = t.getTimeLeft();
        }
        return time;
    }

    private synchronized void processItem() {
        DownloadItem poll = queue.poll();
        poll.setLocation(getProductDownloadFolderLocation());

        DownloadItemThread downloadItemThread = new DownloadItemThread(poll);
        downloading.add(downloadItemThread);

        Task<Boolean> task = downloadItemThread.createTask();

        task.setOnSucceeded(e-> {
            try {
                if (task.get()) {
                    onSucceed(downloadItemThread);
                } else {
                    onFailed(downloadItemThread,e);
                }
            } catch (InterruptedException | ExecutionException interruptedException) {
                interruptedException.printStackTrace();
            }
        });

        task.setOnFailed(e-> onFailed(downloadItemThread,e));

        initThread(poll, task);

        Platform.runLater(() -> {
            historical.remove(poll);
            poll.setProgressProperty(task.progressProperty());
            downloadingObservable.add(poll);
        });

    }

    private void initThread(DownloadItem poll, Task<Boolean> task) {
        Thread thread = new Thread(task);
        thread.setName(poll.getProductDTO().getId());
        thread.start();
    }

    private void onSucceed(DownloadItemThread thread) {
        logger.atInfo().log("Download completed! {}.zip", thread.getDownloadItem().getProductDTO().getTitle());

        restoreAttempts();
        fireEvent(new ComponentEvent(this, thread.getDownloadItem().getProductDTO().getTitle() + " downloaded!"));
        removedDownloadItemFromQueues(thread.getDownloadItem());
        removeDownloadFile();
    }

    private void onFailed(DownloadItemThread thread, WorkerStateEvent e) {
        thread.setCommand(DownloadEnum.DownloadCommand.STOP);
        thread.cancel();
        try {
            thread.closeStreams();
        } catch (IOException ioException) {
            logger.atError().log("Error while closing download item thread {}",e);
            ioException.printStackTrace();
        }

        handleError(thread.getDownloadItem(), e);

        removedDownloadItemFromQueues(thread.getDownloadItem());

        if (e.getSource().getException() instanceof HttpResponseException) {
            if (downloadAttempts>0) {
                attempt();
                logger.atError().log("Attempt to download! {} left...",downloadAttempts);
                queue.add(thread.getDownloadItem());
                Platform.runLater(()->historical.add(thread.getDownloadItem()));
            } else {
                logger.atError().log("5 attempts to download products, all with error. Try later!");
            }
        }
        removeDownloadFile();

    }

    private void handleError(DownloadItem poll, WorkerStateEvent e) {
        if (e.getSource().getException() == null) {
            return;
        }

        if (e.getSource().getException() instanceof ProductNotAvailableException) {
            logger.atWarn().log("Product not available for download! Request made. Try to download it later! {}", poll.getProductDTO().getTitle());
            fireEvent(new ComponentEvent(this,"Product " + poll.getProductDTO().getTitle() + " not available to download!"));
        }
        else if (e.getSource().getException() instanceof FileAlreadyExistsException)
            logger.atWarn().log("Product already exits {}", poll.getProductDTO().getTitle());
        else {
            logger.atError().log("Exception '{}' while downloading! {}.zip", e.getSource().getException().getLocalizedMessage(), poll.getProductDTO().getTitle());
            deleteFile(poll);
        }
    }

    private void deleteFile(DownloadItem item) {
        try {
            Files.deleteIfExists(Paths.get(item.getLocation() +"\\"+ item.getProductDTO().getId() + ".zip"));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private int getNumMaxOfFileDownloading() {
        return getDownloadModeLocation() == DownloadEnum.DownloadMode.MULTIPLE ? maxFilesDownloading : 1;
    }

    private synchronized void attempt() {
        downloadAttempts--;
    }

    private void restoreAttempts() {
        downloadAttempts = 5;
    }

    private synchronized void removedDownloadItemFromQueues(DownloadItem poll) {
        DownloadItemThread itemThread = downloading.stream()
                .filter(t -> t.getId().equals(poll.getProductDTO().getId()))
                .findAny()
                .orElse(null);

        downloading.remove(itemThread);
        downloadingObservable.remove(poll);
    }


    private void stop() {
        downloading.forEach(t-> t.setCommand(DownloadEnum.DownloadCommand.STOP));
    }

    @Override
    public synchronized void remove(ProductDTO productDTO) {
        logger.atWarn().log("{} stopped",productDTO.getTitle());

        DownloadItemThread downloadItemThread = downloading.stream()
                .filter(t -> t.getId().equals(productDTO.getId()))
                .findAny()
                .orElse(null);

        if (downloadItemThread != null) {
            downloadItemThread.setCommand(DownloadEnum.DownloadCommand.STOP);
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while (downloadItemThread.isRunning()) {
                        downloadItemThread.cancel();
                    }
                    return null;
                }
            };
            new Thread(task).start();
        }

    }

    @Override
    public synchronized void cancel() {
        logger.atError().log("Cancel all downloads!");
        queue.clear();
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                downloading.forEach(t -> {
                    t.setCommand(DownloadEnum.DownloadCommand.STOP);
                    while (t.isRunning()) {
                        t.cancel();
                    }
                });
                logger.atInfo().log("All downloads stopped");
                return null;
            }
        };
        new Thread(task).start();
    }

    @Override
    public synchronized void pause() {
        logger.atInfo().log("Paused downloads!");
        downloading.forEach(t-> t.setCommand(DownloadEnum.DownloadCommand.PAUSE));
    }

    @Override
    public synchronized void resume() {
        logger.atInfo().log("Resume downloads!");
        downloading.forEach(t-> t.setCommand(DownloadEnum.DownloadCommand.START));
    }

    public ObservableList<DownloadItem> getHistorical() {
        return historical;
    }

    public ObservableList<DownloadItem> getDownloading() {
        return downloadingObservable;
    }

    public int getQueueSize() {
        return queue.size();
    }

    public DownloadItem getDownload() {
        return queue.poll();
    }

    public synchronized void clearQueue() {
        queue.clear();
        historical.clear();
    }

}
