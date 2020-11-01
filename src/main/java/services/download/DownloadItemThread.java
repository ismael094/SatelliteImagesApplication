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
    private InputStream inputStream;
    private BufferedInputStream in;
    private FileOutputStream fileOutputStream;
    private HttpURLConnection connection;

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

                connection = CopernicusService.getInstance().getConnectionFromURL(item.getProductDTO().getDownloadURL());

                //Init download bytes data
                numOfBytesRead = new AtomicInteger(0);
                contentLength = new AtomicLong(getContentLength(connection.getContentLength()));

                //Open streams and init buffer
                inputStream = connection.getInputStream();
                in = new BufferedInputStream(inputStream);
                fileOutputStream = new FileOutputStream(temporalFileLocation);
                byte buffer[] = new byte[1024];


                logger.atInfo().log("Downloading started!  {}", temporalFileLocation);
                MessageDigest md = MessageDigest.getInstance("MD5");
                int bytesRead;
                int tries = 5;
                startTime = currentTimeMillis();

                while (true) {

                    if (isCancelled() || command == DownloadEnum.DownloadCommand.STOP) {
                        cancel(temporalFileLocation);
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
                        if (noBytesRead(bytesRead) && tries > 0){
                            tries--;
                            //Go to the the last 1024 bytes readed
                            in.mark(1024);
                            Thread.sleep(5000);
                            in.reset();
                            continue;
                        } else if (noBytesRead(tries)) {
                            logger.atError().log("No connection with service! No bytes read from HTTP");
                            //Cancel the download
                            cancel(temporalFileLocation);
                            return false;
                        }

                        if (isDownloadFinish(bytesRead)) {
                            setFinishedStatus();
                            closeStreams();
                            if (FileUtils.renameFile(temporalFileLocation, finalFileLocation)) {
                                logger.atError().log("Error while renaming product {} to {}");
                                return false;
                            }
                            return checkfileMD5(md);
                        }

                        addBytesRead(bytesRead);
                        fileOutputStream.write(buffer, 0, bytesRead);
                        md.update(buffer, 0, bytesRead);
                        updateProgress(numOfBytesRead.get()/1024.0, contentLength.get());
                    }
                }
            }

            private boolean noBytesRead(int bytesRead) {
                return bytesRead == 0;
            }

            private boolean checkfileMD5(MessageDigest md) throws NotAuthenticatedException, IOException, AuthenticationException {
                String md5 = new BigInteger(1, md.digest()).toString(16);
                while (md5.length() < 32)
                    md5=0+md5;
                String checksum = CopernicusService.getInstance().getMD5CheckSum(item.getProductDTO().getId());
                if (!md5.toUpperCase().equals(checksum))
                    logger.atError().log("Checksum verification failed! Restarting download...");
                return md5.toUpperCase().equals(checksum);
            }

            private boolean isDownloadFinish(int bytesRead) {
                return bytesRead == -1;
            }

            private void cancel(String temporalFileLocation) throws IOException, InterruptedException {
                setStoppedStatus();
                closeStreams();
                Files.deleteIfExists(Paths.get(temporalFileLocation));
                Thread.sleep(2000);
            }
        };
    }

    public void closeStreams() throws IOException {
        if (inputStream != null)
            inputStream.close();

        if (in != null)
            in.close();

        if (fileOutputStream != null)
            fileOutputStream.close();

        if (connection != null) {
            if (connection.getInputStream() != null)
                connection.getInputStream().close();
            connection.disconnect();
        }
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
