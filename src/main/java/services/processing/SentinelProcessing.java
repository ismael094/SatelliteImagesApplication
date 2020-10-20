package services.processing;


import com.bc.ceres.core.PrintWriterConciseProgressMonitor;
import com.bc.ceres.core.ProgressMonitor;
import com.bc.ceres.core.ProgressMonitorWrapper;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
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
import org.esa.snap.core.datamodel.Band;
import org.esa.snap.core.datamodel.Product;
import org.esa.snap.core.datamodel.ProgressListener;
import org.esa.snap.core.gpf.GPF;
import org.jfree.fx.FXGraphics2D;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import utils.DownloadConfiguration;
import utils.FileUtils;
import utils.ProcessingConfiguration;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static utils.ThemeConfiguration.getJMetroStyled;

public class SentinelProcessing implements Processing {

    static final Logger logger = LogManager.getLogger(SentinelProcessing.class.getName());

    protected final Map<WorkflowType, Workflow> workflowType;

    public SentinelProcessing() {
        this.workflowType = new HashMap<>();
        this.workflowType.put(WorkflowType.GRD, new Sentinel1GRDDefaultWorkflow());
    }

    protected Product readProduct(String path) throws IOException {
        return ProductIO.readProduct(path);
    }

    protected void saveProduct(Product product, String path, String formatName) throws IOException {
        ProductIO.writeProduct(product,path,formatName);
    }

    protected Product createProduct(Product product, Operation operation) {
        return GPF.createProduct(operation.getName().getName(),operation.getParameters(),product);
    }

    @Override
    public void process(ProductListDTO productList) {
        logger.atInfo().log("====== Processing start =========");
        logger.atInfo().log("Starting to process list {}",productList.getName());
        Map<String, List<String>> productsAreasOfWorks = productList.getProductsAreasOfWorks();
        productList.getProducts().forEach(p-> {
            process(p, productsAreasOfWorks.get(p.getId()), workflowType.get(WorkflowType.GRD), productList.getName());
        });

        logger.atInfo().log("====== List Processed =========");

    }

    @Override
    public void process(ProductDTO product, List<String> areasOfWork, Workflow workflow, String path) {
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

    private void startProcess(Product snapProduct, ProductDTO productDTO, List<String> areasOfWork, Workflow workflow, String path) throws IOException, ParseException {
        List<Product> subsets = new LinkedList<>();
        subsets.add(snapProduct);
        for (Operation op : workflow.getOperations()) {
            logger.atInfo().log("Operation: {}",op.getName());
            if (op.getName() == Operator.READ) {
                readProduct(DownloadConfiguration.getProductDownloadFolderLocation()+"\\"+path+"\\"+ productDTO.getTitle()+".zip");
            } else if (op.getName() == Operator.WRITE) {
                int x = 0;
                for (Product j : subsets) {
                    saveProduct(j, DownloadConfiguration.getListDownloadFolderLocation() + "\\Sentinel 1\\" + productDTO.getTitle() + "_" + x, String.valueOf(op.getParameters().get("formatName")));
                    //j.dispose();
                    x++;
                }
                closeProducts(subsets);
            } else if (op.getName() == Operator.WRITE_AND_READ) {
                List<Product> tmp = new LinkedList<>();
                System.out.println(subsets.size());
                for (Product j : subsets) {
                    System.out.println("==========");
                    Band band = j.getBand("Sigma0_VV");
                    System.out.println("dhsiaof");
                    example(band.createColorIndexedImage(null));
                    saveProduct(j,ProcessingConfiguration.tmpDirectory+"\\"+ productDTO.getId()+".dim", String.valueOf(op.getParameters().get("formatName")));

                    tmp.add(readProduct(ProcessingConfiguration.tmpDirectory+"\\"+ productDTO.getId() + ".dim"));
                }
                closeProducts(subsets);
                subsets = tmp;
            } else {
                List<Product> tmp = new LinkedList<>();
                if (op.getName() == Operator.SUBSET) {
                    if (areasOfWork.size()>0) {
                        for (Product j : subsets) {
                            for (String a : areasOfWork) {
                                op.getParameters().put("geoRegion", new WKTReader().read(a));
                                tmp.add(createProduct(j, op));
                            }
                            //j.dispose();
                        }
                    }
                } else {
                    for (Product j : subsets) {
                        tmp.add(createProduct(j, op));
                        //j.dispose();
                    }
                }
                subsets = tmp;
            }

        }

        closeProducts(subsets);
        snapProduct.closeIO();
        snapProduct.dispose();
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
