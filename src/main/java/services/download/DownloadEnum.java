package services.download;

/**
 * Download status and commands
 */
public class DownloadEnum {
    public enum DownloadCommand {
        PAUSE,STOP,START
    }
    public enum DownloadStatus {
        PAUSED,STOPPED,STARTED,DOWNLOADING,FINISHED
    }
    public enum DownloadMode {
        SINGLE,MULTIPLE
    }
}
