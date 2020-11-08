package services.algorithms;

import model.postprocessing.ProcessingResults;
import model.postprocessing.algorithms.Algorithm;
import utils.AlgorithmsLoader;
import utils.FileUtils;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class AlgorithmsExecutor {

    public boolean execute(Algorithm algorithm, ProcessingResults images) {
        if (images.getFiles().isEmpty())
            return false;

        return algorithm.execute(images);
        /*List<String> processLine = new LinkedList<>();
        processLine.add(algorithm.getAbsolutePath());
        images.getFiles().forEach(i->processLine.add(i.getAbsolutePath()));

        FileUtils.createFolderIfNotExists(AlgorithmsLoader.getAlgorithmFolder()+"\\output");

        ProcessBuilder processBuilder = new ProcessBuilder(processLine);
        processBuilder.redirectOutput(new File(AlgorithmsLoader.getAlgorithmFolder()+"\\output\\"+algorithm.getName().substring(0,algorithm.getName().indexOf("."))+".txt"));*/

        //return processBuilder;
    }
}
