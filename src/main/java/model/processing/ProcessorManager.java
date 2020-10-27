package model.processing;

import controller.processing.SimpleProcessingMonitorController;
import javafx.concurrent.Task;
import model.list.ProductListDTO;
import model.processing.monitor.FXProgressMonitor;
import model.processing.workflow.Sentinel2MSILDefaultWorkflowDTO;
import model.processing.workflow.WorkflowDTO;
import model.processing.workflow.WorkflowType;
import model.products.ProductDTO;
import model.products.ProductType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.entities.Product;
import services.processing.Processor;
import services.processing.SentinelProcessor;
import utils.AlertFactory;
import utils.DownloadConfiguration;
import utils.ProcessingConfiguration;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;

public class ProcessorManager {
    private final Map<ProductType, Processor> typeProcessMap;
    static final Logger logger = LogManager.getLogger(ProcessorManager.class.getName());
    private final FXProgressMonitor operationMonitor;
    private final FXProgressMonitor productMonitor;
    private final FXProgressMonitor listMonitor;
    private boolean isProcessing;
    private boolean isCancel;
    private Task task;

    public ProcessorManager() {
        this.operationMonitor = new FXProgressMonitor();
        this.productMonitor = new FXProgressMonitor();
        this.listMonitor = new FXProgressMonitor();
        this.isProcessing = false;
        this.isProcessing = false;
        this.typeProcessMap = ProcessingConfiguration.getProcessor();

        typeProcessMap.forEach((key,value)->{
            value.setOperationMonitor(operationMonitor);
            value.setProductMonitor(productMonitor);
        });


    }

    public Task<Boolean> process(ProductListDTO productListDTO) throws Exception {
        if (this.task != null && this.task.isRunning())
            throw new Exception("Processing still running");

        this.task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                if (isProcessing)
                    throw new Exception("There is a processing open");

                isProcessing = true;
                isCancel = false;

                logger.atInfo().log("====== Processing start =========");
                logger.atInfo().log("Starting to process list {}", productListDTO.getName());
                Map<ProductDTO, List<String>> productsAreasOfWorks = productListDTO.getProductsAreasOfWorks();

                logger.atInfo().log("====== Processing products =========");
                listMonitor.beginTask("Processing list " + productListDTO.getName(), productsAreasOfWorks.size() + productListDTO.getGroundTruthProducts().size());

                for (Map.Entry<ProductDTO, List<String>> entry : productsAreasOfWorks.entrySet()) {
                    if (isCancelled())
                        return false;
                    processProduct(entry.getKey(), entry.getValue(),
                            productListDTO.getWorkflow(WorkflowType.valueOf(entry.getKey().getProductType())),
                            productListDTO.getName(),
                            false);
                    listMonitor.internalWorked(1);
                }


                logger.atInfo().log("====== Processing reference images =========");
                File file = new File(DownloadConfiguration.getListDownloadFolderLocation() + "\\" + productListDTO.getName() + "\\reference_images");
                if (!file.exists())
                    file.mkdirs();
                for (ProductDTO p : productListDTO.getGroundTruthProducts()) {
                    if (isCancelled())
                        return false;
                    processProduct(p, productListDTO.areasOfWorkOfProduct(p.getFootprint()),
                            new Sentinel2MSILDefaultWorkflowDTO(),
                            productListDTO.getName() + "\\reference_images",
                            false);
                    listMonitor.internalWorked(1);
                }

                listMonitor.done();


                logger.atInfo().log("====== List Processed =========");
                isProcessing = false;
                return true;
            }
        };

        task.setOnSucceeded(e->{
            task = null;
        });

        task.setOnFailed(e->{
            AlertFactory.showErrorDialog("","","");
        });

        return task;

    }

    public void cancel() {
        typeProcessMap.forEach((key,value)->{
            value.stop();
        });
        if (task != null)
            task.cancel(true);
    }

    public Task<BufferedImage> process(ProductDTO product, List<String> areasOfWork, WorkflowDTO workflow, String path, boolean bufferedImage) throws Exception {
        if (this.task != null && this.task.isRunning())
            throw new Exception("Processing still running");
        this.task =  new Task<BufferedImage>() {
            @Override
            protected BufferedImage call() throws Exception {
                return processProduct(product, areasOfWork, workflow, path, bufferedImage);
            }
        };
        return task;
    }

    private BufferedImage processProduct(ProductDTO product, List<String> areasOfWork, WorkflowDTO workflow, String path, boolean bufferedImage) throws Exception {
        System.out.println("LOL");
        System.out.println(Runtime.getRuntime().freeMemory());
        Runtime.getRuntime().gc();
        BufferedImage process = getProcessor(product).process(product, areasOfWork, workflow, path, bufferedImage);
        System.out.println(Runtime.getRuntime().freeMemory());
        return process;
    }

    public Processor getProcessor(ProductDTO productDTO) {
        return typeProcessMap.getOrDefault(getProductType(productDTO),null);
    }

    private ProductType getProductType(ProductDTO productDTO) {
        if (productDTO.getPlatformName().contains("Sentinel"))
            return ProductType.SENTINEL;
        return null;
    }

    public FXProgressMonitor getOperationMonitor() {
        return operationMonitor;
    }

    public FXProgressMonitor getProductMonitor() {
        return productMonitor;
    }

    public FXProgressMonitor getListMonitor() {
        return listMonitor;
    }
}
