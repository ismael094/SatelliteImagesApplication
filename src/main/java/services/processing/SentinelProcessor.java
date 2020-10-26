package services.processing;


import com.bc.ceres.core.ProgressMonitor;
import model.processing.workflow.Sentinel2MSILDefaultWorkflowDTO;
import model.processing.workflow.operation.Operation;
import model.processing.workflow.Sentinel1GRDDefaultWorkflowDTO;
import model.processing.workflow.WorkflowDTO;
import model.processing.workflow.WorkflowType;
import model.processing.workflow.operation.Operator;
import model.products.ProductDTO;
import model.products.Sentinel1ProductDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.ImageInfo;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.core.util.ProductUtils;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import utils.DownloadConfiguration;
import utils.FileUtils;
import utils.ProcessingConfiguration;

import javax.media.jai.JAI;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
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
        this.workflowType.put(WorkflowType.S2MSI1C, new Sentinel2MSILDefaultWorkflowDTO());
        this.workflowType.put(WorkflowType.S2MSI2A, new Sentinel2MSILDefaultWorkflowDTO());
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
        List<Product> products = new LinkedList<>();
        Product snapProduct = null;
        startProductMonitor(productDTO.getId()+" processing...",workflow.getOperations().size()+areasOfWork.size()-1);
        String polarisations = "";
        if (productDTO instanceof Sentinel1ProductDTO) {
            polarisations = ((Sentinel1ProductDTO)productDTO).getPolarizationMode().replace(" ",",");
        }
        try {
            System.out.println(workflow.getOperations().size());
            for (Operation op : workflow.getOperations()) {
                op.getParameters().put("selectedPolarisations",polarisations);
                if (snapProduct != null)
                    op.getParameters().put("sourceBands",getBandNames(snapProduct.getBandNames()));
                showMemory();
                logger.atInfo().log("Operation: {}", op);
                if (op.getName() == Operator.READ) {
                    snapProduct = readProduct(DownloadConfiguration.getProductDownloadFolderLocation() + "\\" + productDTO.getTitle() + ".zip");
                    products.add(snapProduct);
                } else if (op.getName() == Operator.WRITE) {
                    if (generateBoolean) {
                        createBufferedImage(subsets.get(0));
                    }
                    writeOperation(productDTO, subsets, op, path);
                } else if (op.getName() == Operator.WRITE_AND_READ) {
                    snapProduct = writeAndReadOperation(snapProduct, productDTO, op);
                    products.add(snapProduct);
                } else {
                    if (op.getName() == Operator.SUBSET) {
                        products.addAll(subsets);
                        subsets = subsetOperation(snapProduct,areasOfWork, op);
                    } else {
                        if (subsets.isEmpty())
                            products.add(snapProduct);
                            snapProduct = createProduct(snapProduct, op);
                    }
                }

                updateProductMonitor(1);
                operationMonitor.done();

            }
        } catch (java.lang.OutOfMemoryError error) {
            logger.atError().log("Error while processing. OutOfMemory Exception");
        } catch (java.lang.IllegalStateException error) {
            logger.atError().log("Error while processing. Product error {0}", error);
        } finally {
            closeProducts(products);
            operationMonitor.done();
            productMonitor.done();
            logger.atInfo().log("Empty resources...");
            if (snapProduct != null) {
                snapProduct.dispose();
                snapProduct.closeIO();
            }
            logger.atInfo().log("Empty done...");
            showMemory();
            closeProducts(subsets);
            Runtime.getRuntime().gc();
        }

        return colorIndexedImage;
    }

    private void generateRGBImage(Product product,Operation operation,String path) {
        File file = new File(DownloadConfiguration.getListDownloadFolderLocation() + "\\" + path);
        if (!file.exists())
            file.mkdirs();

        Band red = product.getBand(String.valueOf(operation.getParameters().get("red")));
        Band green = product.getBand(String.valueOf(operation.getParameters().get("green")));
        Band blue = product.getBand(String.valueOf(operation.getParameters().get("blue")));
        Band[] bands = new Band[3];
        bands[0] = red;
        bands[1] = green;
        bands[2] = blue;
        ImageInfo imageInfo = ProductUtils.createImageInfo(bands, true, operationMonitor);
        RenderedImage coloredBandImage = ImageManager.getInstance().createColoredBandImage(bands, imageInfo, 0);
        JAI.create("filestore", coloredBandImage,
                DownloadConfiguration.getListDownloadFolderLocation() + "\\"+path+".PNG", "PNG");
    }

    public static String getBandNames(String[] bandNames) {
        return String.join(",", bandNames);
    }

    private void showMemory() {
        long using = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
        logger.atWarn().log("==================MEMORY==================");
        logger.atWarn().log("Memory: {}/{}",using,Runtime.getRuntime().totalMemory());
        logger.atWarn().log("==================MEMORY==================");
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
            saveProduct(j, DownloadConfiguration.getListDownloadFolderLocation() + "\\"+path+"\\" + productDTO.getId() + "_tmp_" + x, String.valueOf(op.getParameters().get("formatName")));
            if ((boolean)op.getParameters().getOrDefault("generatePNG",false)) {
                generateRGBImage(j,op,path+"\\" + productDTO.getId() + "_tmp_" + x);
            }
            x++;
        }
        closeProducts(subsets);
        for (int i = 0; i<subsets.size();i++)
            new File(DownloadConfiguration.getListDownloadFolderLocation() + "\\"+path+"\\" + productDTO.getId() + "_tmp_" + i+".tif")
                    .renameTo(new File(DownloadConfiguration.getListDownloadFolderLocation() + "\\"+path+"\\" + productDTO.getTitle() + "_" + i+".tif"));
    }

    private void closeProducts(List<Product> subsets) {
        subsets.forEach(e->{
            if (e == null)
                return;
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
