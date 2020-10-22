package services.processing;


import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import controller.workflow.ProcessingController;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Modality;
import jfxtras.styles.jmetro.JMetro;
import model.list.ProductListDTO;
import model.processing.*;
import model.products.ProductDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.esa.snap.core.dataio.ProductIO;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.gpf.GPF;
import org.jetbrains.annotations.NotNull;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import utils.DownloadConfiguration;
import utils.FileUtils;
import utils.ProcessingConfiguration;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static utils.ThemeConfiguration.getJMetroStyled;

public class SentinelProcessing extends Processing {

    static final Logger logger = LogManager.getLogger(SentinelProcessing.class.getName());

    protected final Map<WorkflowType, WorkflowDTO> workflowType;

    public SentinelProcessing(ProcessingController processingController) {
        super(processingController);
        this.workflowType = new HashMap<>();
        this.workflowType.put(WorkflowType.GRD, new Sentinel1GRDDefaultWorkflowDTO());
    }

    protected Product readProduct(String path) throws IOException {
        return ProductIO.readProduct(path);
    }

    protected void saveProduct(Product product, String path, String formatName) throws IOException {
        Platform.runLater(()->processingController.setOperation("Saving product"));
        ProductIO.writeProduct(product,path,formatName, operationMonitor);
    }

    protected Product createProduct(Product product, Operation operation) {
        return GPF.createProduct(operation.getName().getName(),operation.getParameters(),product);
    }

    @Override
    public void process(ProductListDTO productList) {
        logger.atInfo().log("====== Processing start =========");
        logger.atInfo().log("Starting to process list {}",productList.getName());

        Map<String, List<String>> productsAreasOfWorks = productList.getProductsAreasOfWorks();

        startProductListMonitor("Product list starting", productList.getProducts().size());

        productList.getProducts().forEach(p-> {
            process(p, productsAreasOfWorks.get(p.getId()), workflowType.get(WorkflowType.GRD), productList.getName());
            updateProductListMonitor(
                    (productList.getProducts().indexOf(p)+1.0)+" of "+ productList.getProducts().size(),
                    (productList.getProducts().indexOf(p)+1.0));
        });

        listMonitor.done();

        logger.atInfo().log("====== List Processed =========");

    }

    @Override
    public void process(ProductDTO product, List<String> areasOfWork, WorkflowDTO workflow, String path) {
        if (!FileUtils.productExists(product.getTitle())) {
            logger.atError().log("File {}.zip doesn't exists",product.getTitle());
            return;
        }

        if (workflow == null) {
            logger.atInfo().log("Loaded default Workflow for GRD products");
            workflow = this.workflowType.get(WorkflowType.valueOf(product.getProductType()));
        }

        logger.atInfo().log("====== Processing product {}",product.getTitle());
        try {
            Product read = readProduct(DownloadConfiguration.getProductDownloadFolderLocation()+"\\"+ product.getTitle()+".zip");
            startProcess(read, product, areasOfWork, workflow,path);
            logger.atInfo().log("====== Processing ended! =========");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void startProcess(Product snapProduct, ProductDTO productDTO, List<String> areasOfWork, WorkflowDTO workflow, String path) throws IOException, ParseException {
        List<Product> subsets = new LinkedList<>();
        subsets.add(snapProduct);

        startProductMonitor(productDTO.getId()+" processing...",workflow.getOperations().size());

        double i = 0;
        for (Operation op : workflow.getOperations()) {
            i++;
            logger.atInfo().log("Operation: {}",op.getName());
            if (op.getName() == Operator.READ) {
                readProduct(DownloadConfiguration.getProductDownloadFolderLocation()+"\\"+path+"\\"+ productDTO.getTitle()+".zip");
            } else if (op.getName() == Operator.WRITE) {
                writeOperation(productDTO, subsets, op);
            } else if (op.getName() == Operator.WRITE_AND_READ) {
                subsets = writeAndReadOperation(productDTO, subsets, op);
            } else {
                List<Product> tmp = new LinkedList<>();
                if (op.getName() == Operator.SUBSET) {
                    subsetOperation(areasOfWork, subsets, op, tmp);
                } else {
                    for (Product j : subsets) {
                        tmp.add(createProduct(j, op));
                        //j.dispose();
                    }
                }
                subsets = tmp;
            }

            updateProductMonitor(i+" of " + workflow.getOperations().size(),i);

        }
        productMonitor.done();

        closeProducts(subsets);
        snapProduct.closeIO();
        snapProduct.dispose();
    }

    private void subsetOperation(List<String> areasOfWork, List<Product> subsets, Operation op, List<Product> tmp) throws ParseException {
        if (areasOfWork.size()>0) {
            for (Product j : subsets) {
                for (String a : areasOfWork) {
                    op.getParameters().put("geoRegion", new WKTReader().read(a));
                    tmp.add(createProduct(j, op));
                }
                //j.dispose();
            }
        }
    }

    @NotNull
    private List<Product> writeAndReadOperation(ProductDTO productDTO, List<Product> subsets, Operation op) throws IOException {
        List<Product> tmp = new LinkedList<>();
        System.out.println(subsets.size());
        for (Product j : subsets) {
            saveProduct(j,ProcessingConfiguration.tmpDirectory+"\\"+ productDTO.getId()+".dim", String.valueOf(op.getParameters().get("formatName")));
            tmp.add(readProduct(ProcessingConfiguration.tmpDirectory+"\\"+ productDTO.getId() + ".dim"));
        }
        closeProducts(subsets);
        subsets = tmp;
        return subsets;
    }

    private void writeOperation(ProductDTO productDTO, List<Product> subsets, Operation op) throws IOException {
        int x = 0;
        for (Product j : subsets) {
            saveProduct(j, DownloadConfiguration.getListDownloadFolderLocation() + "\\Sentinel 1\\" + productDTO.getTitle() + "_" + x, String.valueOf(op.getParameters().get("formatName")));
            x++;
        }
        closeProducts(subsets);
    }

    private void closeProducts(List<Product> subsets) {
        subsets.forEach(Product::dispose);
    }

    private void example(BufferedImage image) {
        System.out.println(image.getNumXTiles());
        JFXAlert alert = new JFXAlert();
        Canvas canvas = new Canvas();
        GraphicsContext graphicsContext2D = canvas.getGraphicsContext2D();
        WritableImage writableImage = SwingFXUtils.toFXImage(image, null);
        ImageView view = new ImageView(writableImage);
        view.setFitHeight(200);
        view.setFitWidth(200);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setOverlayClose(true);
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label("test"));
        layout.setBody(view);
        JFXButton closeButton = new JFXButton("Accept");
        closeButton.getStyleClass().add("dialog-accept");
        closeButton.setOnAction(e -> alert.hideWithAnimation());
        layout.setActions(closeButton);
        alert.setContent(layout);
        JMetro jMetro = getJMetroStyled();

        jMetro.setScene(alert.getDialogPane().getScene());
        Platform.runLater(alert::showAndWait);
    }
}
