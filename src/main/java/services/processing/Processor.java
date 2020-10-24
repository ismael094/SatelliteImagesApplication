package services.processing;

import controller.processing.SimpleProcessingMonitorController;
import javafx.application.Platform;
import model.list.ProductListDTO;
import model.processing.monitor.FXProgressMonitor;
import model.processing.monitor.ProcessingMonitor;
import model.processing.workflow.WorkflowDTO;
import model.products.ProductDTO;

import java.awt.image.BufferedImage;
import java.util.List;

public abstract class Processor {
    protected FXProgressMonitor operationMonitor;
    protected FXProgressMonitor productMonitor;

    public abstract BufferedImage process(ProductDTO productDTO, List<String> areasOfWork, WorkflowDTO workflow, String path, boolean generateBufferedImage) throws Exception;

    protected void startProductMonitor(String task, int total) {
        startMonitor(productMonitor, task,total);
    }

    protected void updateProductMonitor(double update) {
        productMonitor.internalWorked(update);
        //Platform.runLater(()->processingController.setProductOperation(text));
    }

    protected void startOperationMonitor(String task, int total) {
        startMonitor(operationMonitor, task,total);
    }

    private void startMonitor(ProcessingMonitor monitor, String task, int total) {
        monitor.beginTask(task,total);
    }

    public FXProgressMonitor getOperationMonitor() {
        return operationMonitor;
    }

    public FXProgressMonitor getProductMonitor() {
        return productMonitor;
    }

    public void setOperationMonitor(FXProgressMonitor operationMonitor) {
        this.operationMonitor = operationMonitor;
    }

    public void setProductMonitor(FXProgressMonitor productMonitor) {
        this.productMonitor = productMonitor;
    }
}
