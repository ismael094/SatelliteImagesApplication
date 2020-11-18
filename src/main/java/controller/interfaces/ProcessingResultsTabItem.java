package controller.interfaces;

import model.postprocessing.ProcessingResults;

/**
 * Interface to allow menu component to handle controllers with processing results
 */
public interface ProcessingResultsTabItem {
    /**
     * Return list of processing images
     * @return ProcessingResults with the processing images
     */
    ProcessingResults getProcessingResults();
}
