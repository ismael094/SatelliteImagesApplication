package model.preprocessing;

import javafx.beans.property.BooleanProperty;
import javafx.concurrent.Task;
import model.exception.NoWorkflowFoundException;
import model.list.ProductListDTO;
import model.preprocessing.monitor.FXProgressMonitor;
import model.preprocessing.workflow.WorkflowDTO;
import model.preprocessing.workflow.WorkflowType;
import model.products.ProductDTO;
import model.products.ProductType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.processing.Processor;
import utils.DownloadConfiguration;
import utils.FileUtils;
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
    private final BooleanProperty processing;
    private boolean isProcessing;
    private boolean isCancel;
    private Task task;

    public ProcessorManager(BooleanProperty processing) {
        this.processing = processing;
        processing.set(false);
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

                processingStart();
                isCancel = false;

                FileUtils.createFolderIfNotExists(productListDTO.getName());

                logger.atInfo().log("====== Processing start =========");
                logger.atInfo().log("Starting to process list {}", productListDTO.getName());

                Map<ProductDTO, List<String>> productsAreasOfWorks = productListDTO.getProductsAreasOfWorks();

                listMonitor.beginTask("Processing list " + productListDTO.getName(), productsAreasOfWorks.size() + productListDTO.getReferenceProducts().size());

                //If process was cancel, return false
                if (processProducts(productsAreasOfWorks)) return false;


                logger.atInfo().log("====== Processing reference images =========");
                if (processReferenceImages()) return false;


                listMonitor.done();

                resetMonitors();

                logger.atInfo().log("====== List Processed =========");
                isProcessing = false;

                return true;
            }


            /**
             * process references images
             * @return true if the process was cancel
             * @throws Exception
             */
            private boolean processReferenceImages() throws Exception {
                File file = new File(DownloadConfiguration.getListDownloadFolderLocation() + "\\" + productListDTO.getName() + "\\reference_images");
                if (!file.exists())
                    file.mkdirs();
                for (ProductDTO p : productListDTO.getReferenceProducts()) {
                    if (executeProcessIfIsNotCancelled(p, productListDTO.areasOfWorkOfProduct(p.getFootprint()),
                            productListDTO.getWorkflow(WorkflowType.valueOf(p.getProductType())),
                            productListDTO.getName() + "\\reference_images",
                            false)) return true;
                }
                return false;
            }

            private boolean processProducts(Map<ProductDTO, List<String>> productsAreasOfWorks) throws Exception {
                for (Map.Entry<ProductDTO, List<String>> entry : productsAreasOfWorks.entrySet()) {
                    if (executeProcessIfIsNotCancelled(entry.getKey(), entry.getValue(),
                            productListDTO.getWorkflow(WorkflowType.valueOf(entry.getKey().getProductType())),
                            productListDTO.getName(),
                            false)) return true;
                }
                return false;
            }

            private boolean executeProcessIfIsNotCancelled(ProductDTO p, List<String> areas, WorkflowDTO workflow, String path, boolean bufferedImage) throws Exception {
                if (isCancelled()) return true;
                try {
                    processProduct(p, areas,workflow,path,bufferedImage);
                } catch (NoWorkflowFoundException e) {
                    logger.atError().log("No workflow found for");
                }
                listMonitor.internalWorked(1);
                return false;
            }
        };
        return task;

    }

    public Task<BufferedImage> process(ProductDTO product, List<String> areasOfWork, WorkflowDTO workflow,
                                       String path, boolean bufferedImage) throws Exception {

        if (this.task != null && this.task.isRunning())
            throw new Exception("Processing still running");
        this.task =  new Task<BufferedImage>() {
            @Override
            protected BufferedImage call() throws Exception {
                processingStart();
                BufferedImage bufferedImage1 = processProduct(product, areasOfWork, workflow, path, bufferedImage);
                processingStop();
                return bufferedImage1;
            }
        };
        return task;
    }

    private void processingStart() {
        processing.setValue(true);
    }

    private void processingStop() {
        processing.setValue(false);
    }

    private void resetMonitors() {
        listMonitor.getProgress().set(0);
        operationMonitor.getProgress().set(0);
        productMonitor.getProgress().set(0);
    }

    public void cancel() {
        typeProcessMap.forEach((key,value)->{
            value.stop();
        });
        if (task != null)
            task.cancel(true);
    }

    private BufferedImage processProduct(ProductDTO product, List<String> areasOfWork, WorkflowDTO workflow,
                                         String path, boolean bufferedImage) throws Exception {
        Runtime.getRuntime().gc();
        return getProcessor(product).process(product, areasOfWork, workflow, path, bufferedImage);
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

    public BooleanProperty processingProperty() {
        return processing;
    }

    public boolean isProcessing() {
        return isProcessing;
    }


}
