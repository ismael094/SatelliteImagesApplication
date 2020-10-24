package services.processing;


import model.processing.workflow.operation.Operation;
import model.processing.workflow.Sentinel1GRDDefaultWorkflowDTO;
import model.processing.workflow.WorkflowDTO;
import model.processing.workflow.WorkflowType;
import model.processing.workflow.operation.Operator;
import model.products.ProductDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import utils.DownloadConfiguration;
import utils.FileUtils;
import utils.ProcessingConfiguration;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

public class SentinelProcessor extends Processor {

    static final Logger logger = LogManager.getLogger(SentinelProcessor.class.getName());

    protected final Map<WorkflowType, WorkflowDTO> workflowType;
    private BufferedImage colorIndexedImage;

    public SentinelProcessor() {
        //super(processingController);
        this.workflowType = new HashMap<>();
        this.workflowType.put(WorkflowType.GRD, new Sentinel1GRDDefaultWorkflowDTO());
        colorIndexedImage = null;
    }

    protected Product readProduct(String path) throws IOException {
        return ProductIO.readProduct(path);
    }

    protected void saveProduct(Product product, String path, String formatName) throws IOException {
        //Platform.runLater(()->processingController.setOperation("Saving product"));
        ProductIO.writeProduct(product,path,formatName, operationMonitor);
    }

    protected Product createProduct(Product product, Operation operation) {
        return GPF.createProduct(operation.getName().getName(),operation.getParameters(),product);
    }

    /*@Override
    public void process(ProductListDTO productList) throws Exception {
        logger.atInfo().log("====== Processing start =========");
        logger.atInfo().log("Starting to process list {}",productList.getName());

        Map<ProductDTO, List<String>> productsAreasOfWorks = productList.getProductsAreasOfWorks();

        startProductListMonitor("Product list starting", productsAreasOfWorks.size());
        updateProductListMonitor("0 of "+ productList.getProducts().size(),0);
        productsAreasOfWorks.forEach((p,footprints)-> {
            try {
                process(p, footprints, productList.getWorkflow(WorkflowType.valueOf(p.getProductType())), productList.getName(),false);
                updateProductListMonitor(
                        (productList.getProducts().indexOf(p)+1.0)+" of "+ productList.getProducts().size(),
                        1);
            } catch (Exception e) {
                Platform.runLater(()-> AlertFactory.showErrorDialog("Error","Error", "Error while processing"));
            }
        });

        listMonitor.done();

        logger.atInfo().log("====== List Processed =========");

    }*/

    @Override
    public BufferedImage process(ProductDTO product, List<String> areasOfWork, WorkflowDTO workflow, String path, boolean generateBufferedImage) throws Exception {
        if (!FileUtils.productExists(product.getTitle())) {
            logger.atError().log("File {}.zip doesn't exists",product.getTitle());
            return null;
        }

        if (areasOfWork == null || areasOfWork.size() == 0) {
            logger.atError().log("No area of work assigned to product {}", product.getTitle());
            return null;
        }

        if (workflow == null) {
            logger.atInfo().log("Loaded default Workflow for {} products",product.getProductType());
            workflow = this.workflowType.get(WorkflowType.valueOf(product.getProductType()));
        }

        logger.atInfo().log("====== Processing product {}",product.getTitle());
        try {
            BufferedImage bufferedImage = startProcess(product, areasOfWork, workflow, path, generateBufferedImage);
            logger.atInfo().log("====== Processing ended! =========");
            return bufferedImage;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private BufferedImage startProcess(ProductDTO productDTO, List<String> areasOfWork, WorkflowDTO workflow, String path, boolean generateBoolean) throws IOException, ParseException {
        List<Product> subsets = new LinkedList<>();
        Product snapProduct = null;
        startProductMonitor(productDTO.getId()+" processing...",workflow.getOperations().size());

        try {
            for (Operation op : workflow.getOperations()) {
                System.out.println(Runtime.getRuntime().freeMemory());
                logger.atInfo().log("Operation: {}", op.getName());
                if (op.getName() == Operator.READ) {
                    snapProduct = readProduct(DownloadConfiguration.getProductDownloadFolderLocation() + "\\" + productDTO.getTitle() + ".zip");
                } else if (op.getName() == Operator.WRITE) {
                    if (generateBoolean) {
                        createBufferedImage(subsets.get(0));
                    } else
                        writeOperation(productDTO, subsets, op, path);
                } else if (op.getName() == Operator.WRITE_AND_READ) {
                    snapProduct = writeAndReadOperation(snapProduct, productDTO, op);
                } else {
                    if (op.getName() == Operator.SUBSET) {
                        subsets = subsetOperation(snapProduct,areasOfWork, op);
                    } else {
                        if (subsets.isEmpty())
                            snapProduct = createProduct(snapProduct, op);
                    }
                }

                updateProductMonitor(1);
                operationMonitor.done();

            }
        } catch (java.lang.OutOfMemoryError error) {
            logger.atError().log("Error while processing. OutOfMemory Exception");
        } finally {
            operationMonitor.done();
            productMonitor.done();
            logger.atInfo().log("Empty resources...");
            snapProduct.dispose();
            snapProduct.closeIO();
            logger.atInfo().log("Empty done...");
            closeProducts(subsets);
            Runtime.getRuntime().gc();
        }

        return colorIndexedImage;
    }

    private void createBufferedImage(Product product) throws IOException {
        Band bandAt = product.getBandAt(0);
        logger.atInfo().log("Generating preview image with band {}",bandAt);
        colorIndexedImage = bandAt.createColorIndexedImage(operationMonitor);
    }

    /*@Override
    public BufferedImage preview(ProductDTO productList, List<String> areasOfWork, WorkflowDTO workflow) throws Exception {
        Operation operation = workflow.getOperation(Operator.WRITE);
        operation.setOperator(Operator.CREATE_BUFFERED_IMAGE);
        List<String> one = new ArrayList<>();
        one.add(areasOfWork.get(0));
        process(productList,one,workflow,"Tmp");
        return colorIndexedImage;
    }*/

    private List<Product> subsetOperation(Product product, List<String> areasOfWork, Operation op) throws ParseException {
        List<Product> tmp = new LinkedList<>();
        if (areasOfWork.size()>0) {
            for (String a : areasOfWork) {
                op.getParameters().put("geoRegion", new WKTReader().read(a));
                tmp.add(createProduct(product, op));
            }
            op.getParameters().remove("geoRegion");
        } else {
            tmp.add(product);
        }
        return tmp;
    }

    private Product writeAndReadOperation(Product product, ProductDTO productDTO, Operation op) throws IOException {
        saveProduct(product,ProcessingConfiguration.tmpDirectory+"\\"+ productDTO.getId()+".dim", String.valueOf(op.getParameters().get("formatName")));
        closeProduct(product);
        return readProduct(ProcessingConfiguration.tmpDirectory+"\\"+ productDTO.getId() + ".dim");
    }



    private void writeOperation(ProductDTO productDTO, List<Product> subsets, Operation op, String path) throws IOException {
        int x = 0;
        for (Product j : subsets) {
            saveProduct(j, DownloadConfiguration.getListDownloadFolderLocation() + "\\"+path+"\\" + productDTO.getTitle() + "_" + x, String.valueOf(op.getParameters().get("formatName")));
            x++;
        }
        closeProducts(subsets);
    }

    private void closeProducts(List<Product> subsets) {
        subsets.forEach(e->{
            e.dispose();
            try {
                e.closeIO();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    private void closeProduct(Product product) {
        product.dispose();
    }
}
