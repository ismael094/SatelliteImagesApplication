package services.download;

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
