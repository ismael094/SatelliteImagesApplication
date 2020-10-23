package model.processing;

import com.bc.ceres.core.ProgressMonitor;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class FXProgressMonitor extends ProcessingMonitor {
    private boolean cancel = false;
    private String taskName;
    private String subTaskName;
    private int totalWork;
    private DoubleProperty progress;
    private double workDone = 0;

    public FXProgressMonitor() {
        progress = new SimpleDoubleProperty(0);
    }

    @Override
    public void beginTask(String taskName, int totalWork) {
        this.totalWork = totalWork;
        System.out.println("The " + taskName +" has started. Total work is " + totalWork );
        progress.set(0);
    }

    @Override
    public void done() {
        System.out.println("Work done" );
        progress.set(0);
        workDone = 0;
    }

    @Override
    public void internalWorked(double work) {
        workDone=workDone+work;
        double percentageDone = (workDone*100.0)/totalWork;
        progress.set((workDone)/totalWork);
        if (percentageDone % 10.0 == 0)
            System.out.println("Work done " + percentageDone + "%" );

    }

    @Override
    public boolean isCanceled() {
        return cancel;
    }

    @Override
    public void setCanceled(boolean canceled) {
        this.cancel = canceled;
    }

    @Override
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public void setSubTaskName(String subTaskName) {
        this.subTaskName = subTaskName;
    }

    @Override
    public void worked(int work) {
        System.out.println("Worked : "+work);
    }

    @Override
    public DoubleProperty getProgress() {
        return progress;
    }
}
