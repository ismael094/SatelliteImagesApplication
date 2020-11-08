package model.postprocessing.algorithms;

import com.google.common.io.Files;
import model.postprocessing.ProcessingResults;

import java.io.File;
import java.io.IOException;

public class MedianFilterAlgorithm implements Algorithm {
    @Override
    public String getName() {
        return "MedianFilter Algorithm";
    }

    @Override
    public boolean execute(ProcessingResults processingResults) {
        for (File file : processingResults.getFiles()) {
            try {
                Process start = new ProcessBuilder("/algorithm/mediam.exe", file.getAbsolutePath(), "5000").start();
                int i = start.waitFor();
                if (i == 0) {
                    Files.move(new File(file.getParent()),new File(file.getParent()+"\\example.PNG"));
                }
            } catch (IOException | InterruptedException e) {
                return false;
            }
        }
        return false;
    }
}
