package model.postprocessing.algorithms;

import model.postprocessing.ProcessingResults;

public interface Algorithm {
    String getName();
    boolean execute(ProcessingResults processingResults);
}
