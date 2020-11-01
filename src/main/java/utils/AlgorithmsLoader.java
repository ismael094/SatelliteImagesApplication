package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class AlgorithmsLoader {
    public static String DEFAULT_ALGORITHM_FOLDER = System.getProperty("user.home")+"\\Documents\\SatInf\\Algorithm";
    private static final Preferences algorithmPreferences = Preferences.userRoot().node("algorithmPreferences");

    public static List<File> loadAlgorithms() {
        List<File> algorithms = new ArrayList<>();
        FileUtils.createFolderIfNotExists(getAlgorithmFolder());
        File file = new File(getAlgorithmFolder());
        for (File listFile : file.listFiles()) {
            String[] split = listFile.getName().split("\\.");
            if (split.length == 1)
                continue;
            if (split[1].equals("bat") || split[1].equals("exe"))
                algorithms.add(listFile);
        }

        return algorithms;
    }

    public static String getAlgorithmFolder() {
        return DEFAULT_ALGORITHM_FOLDER;
    }

    public static String getListDownloadFolderLocation() {
        return algorithmPreferences.get("algorithmFolder", DEFAULT_ALGORITHM_FOLDER);
    }

    public static void setProductDownloadFolderLocation(String location) {
        algorithmPreferences.put("productFolder",location);
    }
}
