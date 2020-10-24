package services.processing;

import controller.processing.SimpleProcessingMonitorController;
import javafx.application.Platform;
import model.list.ProductListDTO;
import model.processing.FXProgressMonitor;
import model.processing.ProcessingMonitor;
import model.processing.workflow.WorkflowDTO;
import model.products.ProductDTO;

import java.awt.image.BufferedImage;
import java.util.List;

public abstract class Processor {
    protected final SimpleProcessingMonitorController processingController;
    protected FXProgressMonitor operationMonitor;
    protected FXProgressMonitor productMonitor;
    protected FXProgressMonitor listMonitor;

    public Processor(SimpleProcessingMonitorController processingController) {
        this.processingController = processingController;
        this.operationMonitor = new FXProgressMonitor();
        this.productMonitor = new FXProgressMonitor();
        this.listMonitor = new FXProgressMonitor();
        bindMonitors();
    }

    public abstract void process(ProductListDTO productList) throws Exception;
    public abstract BufferedImage process(ProductDTO productDTO, List<String> areasOfWork, WorkflowDTO workflow, String path, boolean generateBufferedImage) throws Exception;
    //public abstract BufferedImage preview(ProductDTO productList, List<String> areasOfWork, WorkflowDTO workflow) throws Exception;

    private void bindMonitors() {
        processingController.setProductListProgressBar(listMonitor.getProgress());
        processingController.setProductProgressBar(productMonitor.getProgress());
        processingController.setOperationProcessingProgressBar(operationMonitor.getProgress());
    }

    protected void startProductListMonitor(String task, int total) {
        startMonitor(listMonitor, task,total);
    }

    protected void updateProductListMonitor(String text, double update) {
        listMonitor.internalWorked(update);
        Platform.runLater(()->processingController.setProductListText(text));
    }

    protected void startProductMonitor(String task, int total) {
        startMonitor(productMonitor, task,total);
    }

    protected void updateProductMonitor(String text, double update) {
        productMonitor.internalWorked(update);
        Platform.runLater(()->processingController.setProductOperation(text));
    }

    protected void startOperationMonitor(String task, int total) {
        startMonitor(operationMonitor, task,total);
    }

    private void startMonitor(ProcessingMonitor monitor, String task, int total) {
        monitor.beginTask(task,total);
    }
}
