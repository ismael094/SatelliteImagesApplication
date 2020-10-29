package model.postprocessing;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ProcessingResults {
    private ObservableList<File> files;

    public ProcessingResults() {
        files = FXCollections.observableArrayList();
    }

    public void addFile(File f) {
        if (!files.contains(f))
            files.add(f);
    }

    public void addFile(List<File> f) {
        if (!files.containsAll(f))
            files.addAll(f);
    }

    public void addFile(File[] f) {
        if (!files.containsAll(Arrays.asList(f)))
            files.addAll(Arrays.asList(f));
    }

    public void removeFile(File[] f) {
        if (files.containsAll(Arrays.asList(f)))
            files.removeAll(Arrays.asList(f));
    }

    public void removeFile(List<File> f) {
        if (files.containsAll(f))
            files.removeAll(f);
    }

    public void removeFile(File f) {
        files.remove(f);
    }

    public ObservableList<File> getFiles() {
        return files;
    }

    @Override
    public String toString() {
        return "ProcessingResults{" +
                "files=" + files +
                '}';
    }
}
