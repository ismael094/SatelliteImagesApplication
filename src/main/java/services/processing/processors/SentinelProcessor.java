package services.processing.processors;


import com.bc.ceres.core.ProgressMonitor;
import model.exception.NoWorkflowFoundException;
import model.preprocessing.workflow.*;
import model.preprocessing.workflow.defaultWorkflow.GRDDefaultWorkflowDTO;
import model.preprocessing.workflow.defaultWorkflow.SLCDefaultWorkflowDTO;
import model.preprocessing.workflow.defaultWorkflow.S2MSI1CDefaultWorkflowDTO;
import model.preprocessing.workflow.operation.Operation;
import model.preprocessing.workflow.operation.Operator;
import model.products.ProductDTO;
import model.products.sentinel.Sentinel1ProductDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.esa.s2tbx.dataio.openjpeg.OpenJpegExecRetriever;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.ImageInfo;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.esa.snap.core.image.ImageManager;
import org.esa.snap.core.util.ProductUtils;
import org.esa.snap.core.util.ResourceInstaller;
import org.esa.snap.core.util.SystemUtils;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import services.processing.Processor;
import utils.DownloadConfiguration;
import utils.FileUtils;
import utils.ProcessingConfiguration;
import utils.SatelliteHelper;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class SentinelProcessor extends Processor {

    static final Logger logger = LogManager.getLogger(SentinelProcessor.class.getName());
    public static final String RED = "red";
    public static final String GREEN = "green";
    public static final String BLUE = "blue";
    public static final String PNG = "PNG";
    public static final String GENERATE_PNG = "generatePNG";
    public static final String GEO_REGION = "geoRegion";

    protected final Map<WorkflowType, WorkflowDTO> workflowType;
    private BufferedImage colorIndexedImage;
    private Product snapProduct;

    public SentinelProcessor() {
        //super(processingController);
        this.workflowType = new HashMap<>();
        this.workflowType.put(WorkflowType.GRD, new GRDDefaultWorkflowDTO());
        this.workflowType.put(WorkflowType.S2MSI1C, new S2MSI1CDefaultWorkflowDTO());
        this.workflowType.put(WorkflowType.S2MSI2A, new S2MSI1CDefaultWorkflowDTO());
        this.workflowType.put(WorkflowType.SLC, new SLCDefaultWorkflowDTO());
        colorIndexedImage = null;
    }

    public WorkflowDTO getWorkflow(WorkflowType type) {
        try {
            Class<?> class_ =  Class.forName("model.preprocessing.workflow.defaultWorkflow."+type.name()+"DefaultWorkflowDTO");
            return (WorkflowDTO)class_.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            return null;
        }
    }

    protected Product readProduct(String path) throws IOException {
        return ProductIO.readProduct(path);
    }

    protected void saveProduct(Product product, String path, String formatName) throws IOException {
        //GPF.writeProduct(product, new File(path),formatName, false, operationMonitor);
        ProductIO.writeProduct(product,path,formatName, operationMonitor);
    }

    protected Product createProduct(Product product, Operation operation) {
        return GPF.createProduct(operation.getName().getName(),operation.getParameters(),product);
    }

    @Override
    public BufferedImage process(ProductDTO product, List<String> areasOfWork, WorkflowDTO workflow, String path, boolean generateBufferedImage) throws Exception {
        if (!FileUtils.productExists(product.getTitle())) {
            logger.atError().log("File {}.zip doesn't exists",product.getTitle());
            return null;
        }

        if (hasNotAreaOfWorkReturn(areasOfWork)) {
            logger.atError().log("No area of work assigned to product {}", product.getTitle());
            return null;
        }

        if (hasNotWorkflowLoadDefault(workflow)) {
            if (noDefaultWorkflow(product))
                throw new NoWorkflowFoundException("No workflow found for product type "+product.getProductType());

            logger.atInfo().log("Loaded default Workflow for {} products",product.getProductType());
            workflow = getWorkflow(WorkflowType.valueOf(product.getProductType()));
        }

        logger.atInfo().log("====== Processing product {}",product.getTitle());
        BufferedImage bufferedImage = startProcess(product, areasOfWork, workflow, path, generateBufferedImage);
        logger.atInfo().log("====== Processing ended! =========");
        return bufferedImage;
    }

    private boolean noDefaultWorkflow(ProductDTO product) {
        return ProcessingConfiguration.getDefaultWorkflow(product.getProductType()) == null;
    }

    private boolean hasNotWorkflowLoadDefault(WorkflowDTO workflow) {
        return workflow == null;
    }

    private boolean hasNotAreaOfWorkReturn(List<String> areasOfWork) {
        return areasOfWork == null || areasOfWork.size() == 0;
    }

    private BufferedImage startProcess(ProductDTO productDTO, List<String> areasOfWork, WorkflowDTO workflow, String path, boolean generateBufferedImage) throws IOException, ParseException {
        List<Product> subsets = new LinkedList<>();
        List<Product> historical = new LinkedList<>();
        snapProduct = null;
        startProductMonitor(productDTO.getId()+" processing...",workflow.getOperations().size()+areasOfWork.size()-1);
        String polarisations = "";

        boolean isRadar = SatelliteHelper.isRadar(productDTO.getPlatformName());
        if (isRadar) {
            polarisations = ((Sentinel1ProductDTO)productDTO).getPolarizationMode().replace(" ",",");
        }


        try {
            for (Operation operation : workflow.getOperations()) {
                if (isRadar)
                    operation.getParameters().put("selectedPolarisations",polarisations);
                //if (snapProduct != null && productDTO.getProductType().equals("GRD"))
                    //op.getParameters().put(op)
                    //op.getParameters().put("sourceBands",getBandNames(snapProduct.getBandNames()));

                showMemory();

                logger.atInfo().log("Operation: {}", operation);


                /**
                 * map<String, Object> parametros   ->
                 * snapProduct -> Esa.Product
                 * productDTO  -> MiProyecto.ProductDTO -> modelado
                 * subsets -> Lista de Esa.Product ->
                 * operation  -> Operation(Operator + Mapa de Parámetros)
                 * path   -> String contiene nombre de carpeta destino ->> Documentos/listas/PATH/nombreProductDTO
                 * areasOfWork   -> Lista de Strings con áreas de interés sobre el ProducDTO
                 */
                if (operation.getName() == Operator.READ) {
                    snapProduct = readProduct(getProductPath(productDTO.getTitle()));
                } else if (operation.getName() == Operator.WRITE) {
                    if (generateBufferedImage) {
                        createBufferedImage(subsets.get(0),operation.getParameters());
                    } else
                        writeOperation(productDTO, subsets, operation.getParameters(), path);
                } else if (operation.getName() == Operator.WRITE_AND_READ) {
                    snapProduct = writeAndReadOperation(snapProduct, productDTO, operation);
                } else if (operation.getName() == Operator.SUBSET) {
                    subsets = subsetOperation(snapProduct, areasOfWork, operation);
                    historical.addAll(subsets);
                } else if (subsets.isEmpty())
                    snapProduct = createProduct(snapProduct, operation);



                historical.add(snapProduct);

                logger.atInfo().log("Product {} with bands {}",productDTO.getTitle(),getBandNames(snapProduct.getBandNames()));
                updateProductMonitor(1);

                operationMonitor.done();

            }
        } catch (java.lang.OutOfMemoryError error) {
            error.printStackTrace();
            logger.atError().log("Error while processing. OutOfMemory Exception {}", error.getLocalizedMessage());
            throw new IOException("Error while processing product. OutOfMemory error");
        } catch (java.lang.IllegalStateException error) {
            error.printStackTrace();
            logger.atError().log("Error while processing. IllegalStateException error {}", error.getLocalizedMessage());
            throw new IOException("Error while processing product");
        } finally {
            clearResources(subsets, historical);
        }

        return colorIndexedImage;
    }

    private String getProductPath(String title) {
        return DownloadConfiguration.getProductDownloadFolderLocation() + "\\" + title + ".zip";
    }

    private void clearResources(List<Product> subsets, List<Product> products) throws IOException {
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

    private void generateRGBImage(Product product,Map<String,Object> parameters,String path) {
        JAI.create("filestore", getRGBRenderedImage(product, parameters),
                DownloadConfiguration.getListDownloadFolderLocation() + "\\"+path+"."+PNG, PNG);
    }

    //generate rgb image for Sentinel 2 products
    private RenderedImage getRGBRenderedImage(Product product, Map<String, Object> parameters) {
        javax.imageio.spi.IIORegistry.getDefaultInstance().registerApplicationClasspathSpis();
        Band red = product.getBand(String.valueOf(parameters.get(RED)));
        Band green = product.getBand(String.valueOf(parameters.get(GREEN)));
        Band blue = product.getBand(String.valueOf(parameters.get(BLUE)));
        System.out.println(Arrays.toString(new Band[]{red, green, blue}));
        ImageInfo imageInfo = ProductUtils.createImageInfo(new Band[]{red,green,blue}, true, operationMonitor);
        return ImageManager.getInstance().createColoredBandImage(new Band[]{red,green,blue}, imageInfo, 0);
    }

    public static String getBandNames(String[] bandNames) {
        return String.join(",", bandNames);
    }

    private void createBufferedImage(Product product, Map<String, Object> parameters) throws IOException {
        //If generatePng is setted, generate RGB images. Sentinel2 products
        if ((Boolean)parameters.getOrDefault(GENERATE_PNG,false)) {
            PlanarImage planarImage = (PlanarImage) getRGBRenderedImage(product,parameters);
            colorIndexedImage = planarImage.getAsBufferedImage();
        } else {
            Band bandAt = product.getBandAt(0);
            colorIndexedImage = bandAt.createColorIndexedImage(operationMonitor);
        }
    }

    private List<Product> subsetOperation(Product product, List<String> areasOfWork, Operation operation) throws ParseException {
        List<Product> tmp = new LinkedList<>();
        if (areasOfWork.size()>0) {
            //Create new product for each area of work, adding the geoRegion in the parameters
            for (String a : areasOfWork) {
                operation.getParameters().put(GEO_REGION, new WKTReader().read(a));
                tmp.add(createProduct(product, operation));
            }
            operation.getParameters().remove(GEO_REGION);
        } else {
            tmp.add(product);
        }
        return tmp;
    }


    /**
     * Generate BEAM DIM to create bands for next operations
     * @param product base product
     * @param productDTO Product information
     * @param op Operation
     * @return product
     * @throws IOException if processing generate error
     */
    private Product writeAndReadOperation(Product product, ProductDTO productDTO, Operation op) throws IOException {
        saveProduct(product,ProcessingConfiguration.tmpDirectory+"\\"+ productDTO.getId()+".dim", String.valueOf(op.getParameters().get("formatName")));
        closeProduct(product);
        return readProduct(ProcessingConfiguration.tmpDirectory+"\\"+ productDTO.getId() + ".dim");
    }

    private void writeOperation(ProductDTO productDTO, List<Product> subsets, Map<String, Object> parameters, String path) throws IOException {
        int x = 0;
        //Temporal name while saving product
        String temporalName = getTemporalName(productDTO, String.valueOf(parameters.get("formatName")), path);

        //Save each subset
        for (Product p : subsets) {
            //if sentinel2 product, save png
            if ((boolean)parameters.getOrDefault("generatePNG",false)) {
                generateRGBImage(p,parameters, path + "\\" + productDTO.getProductType() + "_" + getDate() + "_" + x);
            } else {
                saveProduct(p, temporalName + x, String.valueOf(parameters.get("formatName")));
            }
            x++;
        }

        if (!parameters.get("formatName").equals("PolSARPro"))
            deleteTemporalFiles(productDTO, subsets, path);

        closeProducts(subsets);
    }

    private void deleteTemporalFiles(ProductDTO productDTO, List<Product> subsets, String path) {
        for (int i = 0; i< subsets.size(); i++) {
            new File(DownloadConfiguration.getListDownloadFolderLocation() + "\\" + path + "\\" + productDTO.getId() + "_tmp_" + i + ".tif")
                    .renameTo(new File(DownloadConfiguration.getListDownloadFolderLocation() + "\\" + path + "\\" + productDTO.getProductType()+"_" +getBandNames(subsets.get(i).getBandNames()) + "_"+ getDate() + "_"+  + i + ".tif"));
        }
    }

    private String getTemporalName(ProductDTO productDTO, String formatName, String path) {
        String tmpName = DownloadConfiguration.getListDownloadFolderLocation() + "\\"+ path +"\\" + productDTO.getId() + "_tmp_";
        if (formatName.equals("PolSARPro"))
            tmpName = DownloadConfiguration.getListDownloadFolderLocation() + "\\"+ path +"\\" + productDTO.getTitle() + "_";
        return tmpName;
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

    @Override
    public void stop() {
        if (snapProduct!= null) {
            try {
                snapProduct.closeIO();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
