package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AlgorithmsLoader {

    public static List<File> loadAlgorithms() {
        List<File> algorithms = new ArrayList<>();
        FileUtils.createFolderIfNotExists(FileUtils.DEFAULT_ALGORITHM_FOLDER);
        File file = new File(FileUtils.DEFAULT_ALGORITHM_FOLDER);
        for (File listFile : file.listFiles()) {
            String[] split = listFile.getName().split("\\.");
            if (split.length == 1)
                continue;
            if (split[1].equals("bat") || split[1].equals("exe"))
                algorithms.add(listFile);
        }

        return algorithms;
    }
}
