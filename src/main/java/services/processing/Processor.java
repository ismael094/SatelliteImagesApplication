package services.processing;

import model.processing.monitor.FXProgressMonitor;
import model.processing.monitor.ProcessingMonitor;
import model.processing.workflow.WorkflowDTO;
import model.products.ProductDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import services.processing.processors.SentinelProcessor;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class Processor {
    protected FXProgressMonitor operationMonitor;
    protected FXProgressMonitor productMonitor;
    static final Logger logger = LogManager.getLogger(Processor.class.getName());

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
