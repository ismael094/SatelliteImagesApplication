package utils;

import services.download.DownloadEnum;

import java.util.prefs.Preferences;

public class Configuration {

    private static final Preferences downloadPreferences = Preferences.userRoot().node("downloadPreferences");

    public static String getProductDownloadFolderLocation() {
        return downloadPreferences.get("productFolder", FileUtils.DEFAULT_DOWNLOAD_FOLDER);
    }

    public static String getListDownloadFolderLocation() {
        return downloadPreferences.get("listFolder", FileUtils.DEFAULT_DOWNLOAD_FOLDER);
    }

    public static DownloadEnum.DownloadMode getDownloadModeLocation() {
        return downloadPreferences.get("mode", "multiple").equals("multiple") ? DownloadEnum.DownloadMode.MULTIPLE : DownloadEnum.DownloadMode.SINGLE;
    }

    public static void setDownloadMode(String location) {
        setDownloadPreference("mode",location);
    }

    public static void setListDownloadFolderLocation(String location) {
        setDownloadPreference("listFolder",location);
    }

    public static void setProductDownloadFolderLocation(String location) {
        setDownloadPreference("productFolder",location);
    }

    private static void setDownloadPreference(String key, String value) {
        downloadPreferences.put(key, value);
    }
}
