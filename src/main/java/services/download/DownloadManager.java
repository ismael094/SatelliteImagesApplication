package services.download;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import model.events.DownloadEvent;
import model.events.EventType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import model.exception.ProductNotAvailableException;
import model.listeners.DownloadListener;
import org.apache.http.client.HttpResponseException;
import org.apache.logging.log4j.LogManager;
import utils.AlertFactory;
import utils.FileUtils;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

import static utils.DownloadConfiguration.getDownloadModeLocation;
import static utils.DownloadConfiguration.getProductDownloadFolderLocation;

public class DownloadManager implements Runnable {

    static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(DownloadManager.class.getName());

    private volatile  List<DownloadItemThread> downloading;
    private volatile ConcurrentLinkedQueue<DownloadItem> queue;
    private volatile ObservableList<DownloadItem> historical;
    private volatile ObservableList<DownloadItem> downloadingObservable;

    private volatile int filesDownloading;
    private volatile int downloadAttempts;
    private final Map<EventType.DownloadEventType, List<DownloadListener>> listeners;
    private final int maxFilesDownloading;
    private DoubleProperty timeLeft;


    public DownloadManager(int maxFilesDownloading) {
        this.timeLeft = new SimpleDoubleProperty(0.0);
        this.maxFilesDownloading = maxFilesDownloading;
        this.queue = new ConcurrentLinkedQueue<>();
        this.downloading = new ArrayList<>();
        historical = FXCollections.observableArrayList();
        downloadingObservable = FXCollections.observableArrayList();
        filesDownloading = 0;
        downloadAttempts = 5;
        listeners = new HashMap<>();
    }

    public synchronized void add(DownloadItem item) {
        if (queue.contains(item) || historical.contains(item) || FileUtils.fileExists(item.getProductDTO().getTitle()))
            return;
        queue.add(item);
        historical.add(item);
    }

    public void addListener(EventType.DownloadEventType type, DownloadListener listener) {
        List<DownloadListener> orDefault = this.listeners.getOrDefault(type, new ArrayList<>());
        orDefault.add(listener);
        this.listeners.put(type,orDefault);
    }

    private void fireEvent(DownloadEvent<EventType.DownloadEventType> event) {
        List<DownloadListener> orDefault = this.listeners.getOrDefault(event.getEvent(), null);
        if (orDefault != null)
            orDefault.forEach(l->l.onComponentChange(event));
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
            double time = 0;
            for (DownloadItemThread t : downloading) {
                if (t.getTimeLeft() > time)
                    time = t.getTimeLeft();
            }

            double finalTime = time;
            Platform.runLater(()->{
                timeLeft.set(finalTime);
            });

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
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
        fireEvent(new DownloadEvent<>(this, EventType.DownloadEventType.COMPLETED));
        removedDownloadItemFromQueues(thread.getDownloadItem());
        removeDownloadFile();
    }

    private void onFailed(DownloadItemThread thread, WorkerStateEvent e) {
        thread.setCommand(DownloadEnum.DownloadCommand.STOP);
        thread.cancel();

        fireEvent(new DownloadEvent<>(this, EventType.DownloadEventType.ERROR));

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
            AlertFactory.showErrorDialog("Product not available", "Product not available","Product " +poll.getProductDTO().getTitle() + " not available. Request done! Try to download it in the next days!");
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
            Files.deleteIfExists(Path.of(item.getLocation() +"\\"+ item.getProductDTO().getId() + ".zip"));
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

    public synchronized void remove(DownloadItem item) {
        logger.atWarn().log("{} stopped",item.getProductDTO().getTitle());

        DownloadItemThread downloadItemThread = downloading.stream()
                .filter(t -> t.getId().equals(item.getProductDTO().getId()))
                .findAny()
                .orElse(null);

        if (downloadItemThread != null) {
            downloadItemThread.setCommand(DownloadEnum.DownloadCommand.STOP);
            Task<Void> task = new Task<>() {
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

    public synchronized void cancel() {
        logger.atError().log("Cancel all downloads!");
        queue.clear();
        Task<Void> task = new Task<>() {
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

    public synchronized void pause() {
        logger.atInfo().log("Paused downloads!");
        downloading.forEach(t-> t.setCommand(DownloadEnum.DownloadCommand.PAUSE));
    }

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

    public DoubleProperty timeLeftProperty() {
        return timeLeft;
    }
}
