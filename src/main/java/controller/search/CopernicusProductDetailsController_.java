package controller.search;

import controller.interfaces.TabItem;
import gui.GTMap;
import gui.components.TabPaneComponent;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import model.exception.AuthenticationException;
import model.exception.NotAuthenticatedException;
import model.products.ProductDTO;
import model.products.Sentinel1ProductDTO;
import model.products.Sentinel2ProductDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.io.ParseException;
import services.CopernicusService;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class CopernicusProductDetailsController_ implements TabItem {

    private final FXMLLoader loader;
    private Parent parent;
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
    private ProductDTO product;
    private GTMap gtMap;
    static final Logger logger = LogManager.getLogger(CopernicusProductDetailsController_.class.getName());
    private TabPaneComponent tabPaneComponent;

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
        return new Task<>() {
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
            logger.atError().log("Error while retrieving product data");
    }

    private void setProductDetails() throws NotAuthenticatedException, IOException, AuthenticationException, ParseException {
        name.textProperty().bind(product.titleProperty());
        platName.textProperty().bind(product.platformNameProperty());
        productType.textProperty().bind(product.productTypeProperty());
        size.textProperty().bind(product.sizeProperty());
        ingestionDate.textProperty().bind(product.ingestionDateProperty().asString());

        if (product.getPlatformName().equals("Sentinel-1"))
            setSentinel1Data();
        else if (product.getPlatformName().equals("Sentinel-2"))
            setSentinel2Data();

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
        try (InputStream contentFromURL = service.getPreview(product.getId())) {
            image.setImage(new Image(contentFromURL, image.getFitWidth(), image.getFitHeight(), false, false));
        } catch (IOException e) {
            logger.atInfo().log("No preview image found for product name {}", product.getTitle());
            image.setImage(new Image(getClass().getResource("/img/no_photo.jpg").openStream(), image.getFitWidth(), image.getFitHeight(), false, false));
        }
    }

    private void setMap() throws ParseException {
        gtMap = new GTMap(300, 300,false);
        map.getChildren().add(gtMap);
        gtMap.createFeatureFromWKT(product.getFootprint(),product.getId(),"products");
        gtMap.createAndDrawLayer("products", Color.BLACK, null);
        gtMap.goToSelection();
        gtMap.scroll(-96);
        gtMap.refresh();
    }
}
