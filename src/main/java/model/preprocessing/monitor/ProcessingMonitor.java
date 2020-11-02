package model.preprocessing.monitor;

import com.bc.ceres.core.ProgressMonitor;
import javafx.beans.property.DoubleProperty;

public abstract class ProcessingMonitor implements ProgressMonitor {
    public abstract DoubleProperty getProgress();
}
