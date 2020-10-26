package model.processing;

import controller.processing.SimpleProcessingMonitorController;
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
import utils.ProcessingConfiguration;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public class ProcessorManager {
    private final Map<ProductType, Processor> typeProcessMap;
    private final SimpleProcessingMonitorController monitorController;
    static final Logger logger = LogManager.getLogger(ProcessorManager.class.getName());
    private final FXProgressMonitor operationMonitor;
    private final FXProgressMonitor productMonitor;
    private final FXProgressMonitor listMonitor;
    private boolean isProcessing;

    public ProcessorManager(SimpleProcessingMonitorController monitorController) {
        this.operationMonitor = new FXProgressMonitor();
        this.productMonitor = new FXProgressMonitor();
        this.listMonitor = new FXProgressMonitor();
        this.isProcessing = false;
        this.monitorController = monitorController;
        this.typeProcessMap = ProcessingConfiguration.getProcessor();

        typeProcessMap.forEach((key,value)->{
            value.setOperationMonitor(operationMonitor);
            value.setProductMonitor(productMonitor);
        });

        monitorController.setOperationProcessingProgressBar(operationMonitor.getProgress());
        monitorController.setProductProgressBar(productMonitor.getProgress());
        monitorController.setProductListProgressBar(listMonitor.getProgress());
    }

    public void process(ProductListDTO productListDTO) throws Exception {
        if (isProcessing)
            throw new Exception("There is a processing open");

        isProcessing = true;

        logger.atInfo().log("====== Processing start =========");
        logger.atInfo().log("Starting to process list {}",productListDTO.getName());
        Map<ProductDTO, List<String>> productsAreasOfWorks = productListDTO.getProductsAreasOfWorks();

        logger.atInfo().log("====== Processing products =========");
        listMonitor.beginTask("Processing list "+productListDTO.getName(),productsAreasOfWorks.size()+productListDTO.getGroundTruthProducts().size());

        for (Map.Entry<ProductDTO, List<String>> entry : productsAreasOfWorks.entrySet()) {
            process(entry.getKey(),entry.getValue(),
                    productListDTO.getWorkflow(WorkflowType.valueOf(entry.getKey().getProductType())),
                    productListDTO.getName(),
                    false);
            listMonitor.internalWorked(1);
        }


        logger.atInfo().log("====== Processing reference images =========");
        for (ProductDTO p : productListDTO.getGroundTruthProducts()) {
            process(p,productListDTO.areasOfWorkOfProduct(p.getFootprint()),
                    new Sentinel2MSILDefaultWorkflowDTO(),
                    productListDTO.getName()+"\\reference_images",
                    false);
            listMonitor.internalWorked(1);
        }


        listMonitor.done();


        logger.atInfo().log("====== List Processed =========");
        isProcessing = false;
    }

    public BufferedImage process(ProductDTO product, List<String> areasOfWork, WorkflowDTO workflow, String path, boolean bufferedImage) throws Exception {
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
}
