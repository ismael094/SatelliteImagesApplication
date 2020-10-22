package services.processing;

import controller.workflow.ProcessingController;
import javafx.application.Platform;
import model.list.ProductListDTO;
import model.processing.FXProgressMonitor;
import model.processing.ProcessingMonitor;
import model.processing.WorkflowDTO;
import model.products.ProductDTO;

import java.util.List;

public abstract class Processing {
    protected final ProcessingController processingController;
    protected FXProgressMonitor operationMonitor;
    protected FXProgressMonitor productMonitor;
    protected FXProgressMonitor listMonitor;

    public Processing(ProcessingController processingController) {
        this.processingController = processingController;
        this.operationMonitor = new FXProgressMonitor();
        this.productMonitor = new FXProgressMonitor();
        this.listMonitor = new FXProgressMonitor();
        bindMonitors();
    }

    public abstract void process(ProductListDTO productList);
    public abstract void process(ProductDTO productList, List<String> areasOfWork, WorkflowDTO workflow, String path) throws Exception;

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
