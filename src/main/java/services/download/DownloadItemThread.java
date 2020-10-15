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
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.System.currentTimeMillis;

public class DownloadItemThread extends Service<Boolean> {
    private final DownloadItem item;
    private DownloadEnum.DownloadCommand command;
    private DownloadEnum.DownloadStatus status;
    private AtomicInteger numOfBytesRead;
    private AtomicLong contentLength;

    static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(DownloadItemThread.class.getName());
    private long startTime;

    public DownloadItemThread(DownloadItem item) {
        this.item = item;
        startTime = 0;
        contentLength = new AtomicLong(0);
        numOfBytesRead = new AtomicInteger(0);
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
        return new Task<Boolean>() {
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
                String temporalFileLocation = item.getLocation() +"\\"+ item.getProductDTO().getId() + ".zip";
                String finalFileLocation = item.getLocation() +"\\"+ item.getProductDTO().getTitle() + ".zip";

                Thread.sleep(400);

                HttpURLConnection connection = CopernicusService.getInstance().getConnectionFromURL(item.getProductDTO().getDownloadURL());

                //Init download bytes data
                numOfBytesRead = new AtomicInteger(0);
                contentLength = new AtomicLong(getContentLength(connection.getContentLength()));

                //Open streams and init buffer
                InputStream inputStream = connection.getInputStream();
                BufferedInputStream in = new BufferedInputStream(inputStream);
                FileOutputStream fileOutputStream = new FileOutputStream(temporalFileLocation);
                byte buffer[] = new byte[1024];


                logger.atInfo().log("Downloading started!  {}", temporalFileLocation);
                MessageDigest md = MessageDigest.getInstance("MD5");
                int bytesRead;
                int tries = 5;
                startTime = currentTimeMillis();
                while (true) {

                    if (isCancelled() || command == DownloadEnum.DownloadCommand.STOP) {
                        setStoppedStatus();
                        close(fileOutputStream, inputStream, in, connection);
                        Files.deleteIfExists(Paths.get(temporalFileLocation));
                        Thread.sleep(2000);
                        return false;
                    }

                    if (command == DownloadEnum.DownloadCommand.PAUSE) {
                        if (status != DownloadEnum.DownloadStatus.PAUSED)
                            setPausedStatus();
                        Thread.sleep(1000);
                    } else {
                        if (status != DownloadEnum.DownloadStatus.DOWNLOADING)
                            setDownloadingStatus();

                        bytesRead = in.read(buffer, 0, 1024);
                        if (bytesRead == 0 && tries >0){
                            tries--;
                            System.out.println("Bytes not readed");
                            in.mark(1024);
                            Thread.sleep(5000);
                            in.reset();
                            continue;
                        } else if (tries == 0) {
                            logger.atError().log("No connection");
                            return false;
                        }
                        if (isDownloadFinish(bytesRead)) {
                            setFinishedStatus();
                            close(fileOutputStream, inputStream, in, connection);
                            boolean wasFileRename = new File(temporalFileLocation).renameTo(new File(finalFileLocation));
                            if (wasFileRename)
                                logger.atInfo().log("Download finished! {}GB downloaded in {} minutes", getContentLengthInGb(contentLength.get() / 1024.0, 1024.0, 1024.0), getContentLengthInGb(currentTimeMillis() - startTime, 1000.0, 60.0));
                            else {
                                Files.deleteIfExists(Paths.get(temporalFileLocation));
                                logger.atError().log("Not able to rename file {}",temporalFileLocation);
                                return false;
                            }
                            return checkMD5(md);
                        }
                        addBytesRead(bytesRead);
                        fileOutputStream.write(buffer, 0, bytesRead);
                        md.update(buffer, 0, bytesRead);
                        updateProgress(numOfBytesRead.get()/1024.0, contentLength.get());
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

            private boolean isDownloadFinish(int bytesRead) {
                return bytesRead == -1;
            }
        };
    }

    private double getContentLengthInGb(double v, double v2, double v3) {
        return ((v) / v2) / v3;
    }

    private synchronized void addBytesRead(int bytes) {
        numOfBytesRead.addAndGet(bytes);
    }

    private long getContentLength(long contentLength) {
        if (contentLength > 0)
            return contentLength/1024;
        else
            return (long) (item.getProductDTO().getSizeAsDouble()*1024*1024);
    }

    private void setStoppedStatus() {
        setStatus(DownloadEnum.DownloadStatus.STOPPED);
    }

    private void setPausedStatus() {
        setStatus(DownloadEnum.DownloadStatus.PAUSED);
    }

    private void setDownloadingStatus() {
        setStatus(DownloadEnum.DownloadStatus.DOWNLOADING);
    }

    private void setFinishedStatus() {
        setStatus(DownloadEnum.DownloadStatus.FINISHED);
    }

    private synchronized void setStatus(DownloadEnum.DownloadStatus status) {
        this.status = status;
    }

    public synchronized double getTimeLeft() {
        return (numOfBytesRead.get()/1024.0) == 0 ? 0 : ((contentLength.get()-(numOfBytesRead.get()/1024.0))/(numOfBytesRead.get()/1024.0))*(currentTimeMillis()-startTime)/1000 /60;

    }
}
