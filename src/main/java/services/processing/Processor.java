package services.processing;

import model.preprocessing.monitor.FXProcessingMonitor;
import model.preprocessing.monitor.ProcessingMonitor;
import model.preprocessing.workflow.WorkflowDTO;
import model.products.ProductDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class Processor {
    protected ProcessingMonitor operationMonitor;
    protected ProcessingMonitor productMonitor;
    static final Logger logger = LogManager.getLogger(Processor.class.getName());

    /**
     * Process product with workaflows and areas of work
     * @param productDTO product
     * @param areasOfWork areasOfWork
     * @param workflow workflow
     * @param path path to save the results
     * @param generateBufferedImage true for generate buffered image
     * @return buffered image
     * @throws Exception Error while processing product
     */
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

    public ProcessingMonitor getOperationMonitor() {
        return operationMonitor;
    }

    public ProcessingMonitor getProductMonitor() {
        return productMonitor;
    }

    public void setOperationMonitor(FXProcessingMonitor operationMonitor) {
        this.operationMonitor = operationMonitor;
    }

    public void setProductMonitor(FXProcessingMonitor productMonitor) {
        this.productMonitor = productMonitor;
    }

    public abstract void stop();

    protected void showMemory() {
        long using = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        logger.atWarn().log("==================MEMORY==================");
        logger.atWarn().log("Memory: {}/{}",using,Runtime.getRuntime().totalMemory());
        logger.atWarn().log("==================MEMORY==================");
    }

    protected String getDate() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }
}
