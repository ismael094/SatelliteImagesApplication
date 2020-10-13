package services.download;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.exception.AuthenticationException;
import model.exception.NotAuthenticatedException;
import model.exception.ProductNotAvailableException;
import org.apache.logging.log4j.LogManager;
import services.CopernicusService;
import utils.FileUtils;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.lang.System.currentTimeMillis;

public class DownloadItemThread extends Service<Boolean> {
    private final DownloadItem item;
    private volatile DownloadEnum.DownloadCommand command;
    private volatile DownloadEnum.DownloadStatus status;
    private volatile int numOfBytesRead;
    private volatile long contentLength;

    static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(DownloadItemThread.class.getName());
    private long startTime;

    public DownloadItemThread(DownloadItem item) {
        this.item = item;
        startTime = 0;
    }

    public synchronized void setCommand(DownloadEnum.DownloadCommand command) {
        this.command = command;
    }

    public synchronized DownloadEnum.DownloadStatus getStatus() {
        return status;
    }

    public String getId() {
        return item.getProductDTO().getId();
    }

    public DownloadItem getDownloadItem() {
        return item;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                setStatus(DownloadEnum.DownloadStatus.DOWNLOADING);

                if (FileUtils.fileExists(item.getLocation()+"\\"+item.getProductDTO().getTitle()+".zip"))
                    throw new FileAlreadyExistsException("Product already exits");

                if (!CopernicusService.getInstance().isProductOnline(item.getProductDTO().getId())) {
                    throw new ProductNotAvailableException("Product not available!");
                }

                FileUtils.createFolderIfNotExists(item.getLocation());

                return initDownload();
            }

            private boolean initDownload() throws IOException, InterruptedException, NoSuchAlgorithmException, NotAuthenticatedException, AuthenticationException {
                Thread.sleep(400);
                HttpURLConnection connection = CopernicusService.getInstance().getConnectionFromURL(item.getProductDTO().getDownloadURL());
                String location = item.getLocation() +"\\"+ item.getProductDTO().getId() + ".zip";
                InputStream inputStream = connection.getInputStream();
                BufferedInputStream in = new BufferedInputStream(inputStream);
                FileOutputStream fileOutputStream = new FileOutputStream(location);

                byte buffer[] = new byte[1024];
                numOfBytesRead = 0;
                contentLength = getContentLength(connection.getContentLength());
                int bytesRead;

                logger.atInfo().log("Downloading started!  {}", location);
                MessageDigest md = MessageDigest.getInstance("MD5");
                startTime = currentTimeMillis();
                while (true) {
                    if (isCancelled() || command == DownloadEnum.DownloadCommand.STOP) {
                        stoppedStatus();
                        close(fileOutputStream, inputStream, in, connection);
                        Files.deleteIfExists(Path.of(item.getLocation() +"\\"+ item.getProductDTO().getId() + ".zip"));
                        Thread.sleep(2000);
                        return false;
                    }

                    if (command != DownloadEnum.DownloadCommand.PAUSE) {
                        downloadingStatus();
                        bytesRead = in.read(buffer, 0, 1024);
                        if (downloadFinish(bytesRead)) {
                            finishedStatus();
                            close(fileOutputStream, inputStream, in, connection);
                            new File(item.getLocation() + "\\" + item.getProductDTO().getId() + ".zip").renameTo(new File(item.getLocation() + "\\"+ item.getProductDTO().getTitle() + ".zip"));
                            logger.atInfo().log("Download finished! {}GB downloaded in {} minutes", ((((contentLength / 1024.0) / 1024.0) / 1024.0)), ((currentTimeMillis() - startTime) / 1000.0) / 60.0);
                            return checkMD5(md);
                        }
                        addBytesRead(bytesRead);
                        fileOutputStream.write(buffer, 0, bytesRead);
                        md.update(buffer, 0, bytesRead);
                        updateProgress(numOfBytesRead/1024.0, contentLength);
                    } else {
                        pausedStatus();
                        Thread.sleep(1000);
                    }
                }
            }

            private boolean checkMD5(MessageDigest md) throws NotAuthenticatedException, IOException, AuthenticationException {
                String md5 = new BigInteger(1, md.digest()).toString(16);
                while (md5.length() < 32)
                    md5=0+md5;
                String checksum = CopernicusService.getInstance().getMD5CheckSum(item.getProductDTO().getId());
                if (!md5.toUpperCase().equals(checksum))
                    logger.atError().log("Checksum verification failed! Restarting download...");
                return md5.toUpperCase().equals(checksum);
            }

            private void close(FileOutputStream fileOutputStream, InputStream inputStream, BufferedInputStream in, HttpURLConnection connection) throws IOException {
                inputStream.close();
                in.close();
                fileOutputStream.close();
                if (connection.getInputStream() != null)
                    connection.getInputStream().close();
                connection.disconnect();
            }

            private boolean downloadFinish(int bytesRead) {
                return bytesRead == -1;
            }
        };
    }

    private synchronized void addBytesRead(int bytes) {
        numOfBytesRead+=bytes;
    }

    private long getContentLength(long contentLength) {
        if (contentLength > 0)
            return contentLength/1024;
        else
            return (long) (item.getProductDTO().getSizeAsDouble()*1024*1024);
    }

    private void stoppedStatus() {
        setStatus(DownloadEnum.DownloadStatus.STOPPED);
    }

    private void pausedStatus() {
        setStatus(DownloadEnum.DownloadStatus.PAUSED);
    }

    private void downloadingStatus() {
        setStatus(DownloadEnum.DownloadStatus.DOWNLOADING);
    }

    private void finishedStatus() {
        setStatus(DownloadEnum.DownloadStatus.FINISHED);
    }

    private synchronized void setStatus(DownloadEnum.DownloadStatus status) {
        this.status = status;
    }

    public synchronized double getTimeLeft() {
        return (numOfBytesRead/1024) == 0 ? 0 : (double)(((contentLength-(numOfBytesRead/1024))/(numOfBytesRead/1024))*(currentTimeMillis()-startTime)/1000)/60;

    }
}
