package controller.search;

import controller.interfaces.TabItem;
import gui.GTMap;
import gui.components.TabPaneComponent;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import model.exception.AuthenticationException;
import model.exception.NotAuthenticatedException;
import model.products.ProductDTO;
import model.products.sentinel.Sentinel1ProductDTO;
import model.products.sentinel.Sentinel2ProductDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.io.ParseException;
import services.CopernicusService;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class CopernicusProductDetailsController_ implements TabItem {

    private final FXMLLoader loader;
    private Parent parent;
    @FXML
    private Label id;
    @FXML
    private Label sensorMode;
    @FXML
    private Label polarisation;
    @FXML
    private Label cloud;
    @FXML
    private Pane cloudCoveragePane;
    @FXML
    private Pane polarisationPane;
    @FXML
    private Pane sensorModePane;
    @FXML
    private AnchorPane mapContainer;
    @FXML
    private Pane map;
    @FXML
    private Label name;
    @FXML
    private Label platName;
    @FXML
    private Label productType;
    @FXML
    private Label size;
    @FXML
    private Label ingestionDate;
    @FXML
    private ImageView image;
    @FXML
    private AnchorPane imageContainer;


    private final ProductDTO product;
    private TabPaneComponent tabPaneComponent;
    private final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    static final Logger logger = LogManager.getLogger(CopernicusProductDetailsController_.class.getName());

    public CopernicusProductDetailsController_(ProductDTO product) {
        this.product = product;
        loader = new FXMLLoader(getClass().getResource("/fxml/ProductDetails.fxml"));
        loader.setController(this);
        try {
            parent = loader.load();
        } catch (IOException e) {
            parent = null;
            e.printStackTrace();
        }
    }

    @Override
    public void setTabPaneComponent(TabPaneComponent component) {
        this.tabPaneComponent = component;
    }

    @Override
    public Parent getView() {
        return parent;
    }

    @Override
    public Task<Parent> start() {
        return new Task<Parent>() {
            @Override
            protected Parent call() throws Exception {
                initViewData();
                return getView();
            }
        };
    }

    @Override
    public String getName(){
        return product.getTitle().substring(0,15);
    }

    @Override
    public String getItemId() {
        return product.getId();
    }

    public void initViewData() throws NotAuthenticatedException, IOException, AuthenticationException, ParseException {
        if (product!=null)
            setProductDetails();
        else
            logger.atError().log("Error while retrieving product data! Null reference of product!");
    }

    private void setProductDetails() throws NotAuthenticatedException, IOException, AuthenticationException, ParseException {
        name.textProperty().bind(product.titleProperty());
        Tooltip tp = new Tooltip(product.getTitle());
        //tp.setShowDelay(new Duration(300));
        name.setTooltip(tp);
        id.setText(product.getId());
        platName.textProperty().bind(product.platformNameProperty());
        productType.textProperty().bind(product.productTypeProperty());
        size.textProperty().bind(product.sizeProperty());
        ingestionDate.setText(sdf.format(product.getIngestionDate().getTime()));

        if (product.getPlatformName().equals("Sentinel-1"))
            setSentinel1Data();
        else if (product.getPlatformName().equals("Sentinel-2"))
            setSentinel2Data();

        //image.fitHeightProperty().bind(imageContainer.heightProperty());
        //image.fitWidthProperty().bind(imageContainer.widthProperty());

        setMap();
        setImagePreview();
    }

    private void setSentinel2Data() {
        cloud.textProperty().bind(((Sentinel2ProductDTO)product).cloudCoverPercentageProperty().asString());
        toggleSentinel1Data(false);
        toggleSentinel2Data(true);
    }

    private void toggleSentinel2Data(boolean b) {
        cloudCoveragePane.setManaged(b);
        cloudCoveragePane.setVisible(b);
    }

    private void toggleSentinel1Data(boolean b) {
        sensorModePane.setVisible(b);
        sensorModePane.setManaged(b);
        polarisationPane.setVisible(b);
        polarisationPane.setManaged(b);
    }

    private void setSentinel1Data() {
        sensorMode.textProperty().bind(((Sentinel1ProductDTO)product).sensorOperationalModeProperty());
        polarisation.textProperty().bind(((Sentinel1ProductDTO)product).polarizationModeProperty());
        toggleSentinel1Data(true);
        toggleSentinel2Data(false);
    }

    private void setImagePreview() throws AuthenticationException, IOException, NotAuthenticatedException {
        CopernicusService service = CopernicusService.getInstance();
        try (InputStream contentFromURL = service.getContentFromURL(product.getPreviewURL())) {
            image.setImage(new Image(contentFromURL));
        } catch (IOException e) {
            logger.atInfo().log("No preview image found for product name {}", product.getTitle());
            image.setImage(new Image(getClass().getResource("/img/no_photo.jpg").openStream()));
        }
    }

    private void setMap() throws ParseException {
        Platform.runLater(()->{
            try {
                System.out.println("Pref: " + map.getPrefWidth() + " - " + map.getPrefHeight());
                System.out.println("Actual: " + map.getWidth() + " - " + map.getWidth());
                System.out.println("Actual: " + map.getMaxWidth() + " - " + map.getMaxHeight());

                System.out.println("C. Pref: " + mapContainer.getPrefWidth() + " - " + mapContainer.getPrefHeight());
                System.out.println("C. Actual: " + mapContainer.getWidth() + " - " + mapContainer.getWidth());
                GTMap gtMap = new GTMap((int)map.getPrefWidth(), (int)map.getPrefHeight(), false);
                map.getChildren().add(gtMap);

                gtMap.createFeatureFromWKT(product.getFootprint(),product.getId(),"products");

                gtMap.createAndDrawLayer("products", Color.BLACK, null);
                gtMap.focusOnLayer("products");
                gtMap.scroll(-96);
                gtMap.refresh();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }
}
