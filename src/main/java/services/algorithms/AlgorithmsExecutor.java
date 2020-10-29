package services.algorithms;

import model.postprocessing.ProcessingResults;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class AlgorithmsExecutor {

    public ProcessBuilder execute(File algorithm, ProcessingResults images) {
        if (!algorithm.exists() || images.getFiles().isEmpty())
            return null;


        List<String> processLine = new LinkedList<>();
        processLine.add(algorithm.getAbsolutePath());
        images.getFiles().forEach(i->processLine.add(i.getAbsolutePath()));
        return new ProcessBuilder(processLine);
    }
}
