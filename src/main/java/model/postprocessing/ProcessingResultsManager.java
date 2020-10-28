package model.postprocessing;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;

public class ProcessingResultsManager {
    private final ObservableList<File> files;

    public ProcessingResultsManager() {
        files = FXCollections.observableArrayList();
    }

    public ObservableList<File> getFiles() {
        return files;
    }
}
