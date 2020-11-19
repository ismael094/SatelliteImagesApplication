package utils;

import services.download.DownloadEnum;

import java.util.prefs.Preferences;

public class DownloadConfiguration {

    private static final Preferences downloadPreferences = Preferences.userRoot().node("downloadPreferences");

    /**
     * Get download preferences
     * @return download preferences
     */
    public static Preferences getDownloadPreferences() {
        return downloadPreferences;
    }

    /**
     * get download folder
     * @return Product download folder
     */
    public static String getProductDownloadFolderLocation() {
        return downloadPreferences.get("productFolder", FileUtils.DEFAULT_DOWNLOAD_FOLDER);
    }

    /**
     * Get list folder
     * @return list folder
     */
    public static String getListDownloadFolderLocation() {
        return downloadPreferences.get("listFolder", FileUtils.DEFAULT_DOWNLOAD_FOLDER);
    }

    /**
     * Get download mode, sequential or multiple
     * @return Download mode
     */
    public static DownloadEnum.DownloadMode getDownloadModeLocation() {
        return downloadPreferences.get("mode", "multiple")
                .equals("multiple") ? DownloadEnum.DownloadMode.MULTIPLE : DownloadEnum.DownloadMode.SINGLE;
    }

    /**
     * Get if autodownload is active
     * @return true if active; false otherwise
     */
    public static boolean getAutodownload() {
        return downloadPreferences.get("autodownload", "false").equals("true");
    }

    /**
     * Set autodownload mode
     * @param mode mode
     */
    public static void setAutodownload(String mode) {
        downloadPreferences.put("autodownload", mode);
    }

    /**
     * Set download mode
     * @param location mode
     */
    public static void setDownloadMode(String location) {
        setDownloadPreference("mode",location);
    }

    /**
     * Set list folder
     * @param location list folder
     */
    public static void setListDownloadFolderLocation(String location) {
        setDownloadPreference("listFolder",location);
    }

    /**
     * Set product download folder
     * @param location Product download folder
     */
    public static void setProductDownloadFolderLocation(String location) {
        setDownloadPreference("productFolder",location);
    }

    private static void setDownloadPreference(String key, String value) {
        downloadPreferences.put(key, value);
    }
}
