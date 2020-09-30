package controller.search;

import controller.TabItem;
import gui.GTMap;
import gui.components.TabPaneComponent;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import model.exception.AuthenticationException;
import model.exception.NotAuthenticatedException;
import model.products.Product;
import model.products.Sentinel1Product;
import model.products.Sentinel2Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.io.ParseException;
import services.CopernicusService;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class CopernicusProductDetailsController implements Initializable, TabItem {

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
    private Product product;
    private GTMap gtMap;
    static final Logger logger = LogManager.getLogger(CopernicusProductDetailsController.class.getName());
    private TabPaneComponent tabPaneComponent;

    private void setProductDetails() {
        name.setText(product.getTitle());
        platName.setText(product.getPlatformName());
        productType.setText(product.getProductType());
        size.setText(product.getSize());
        ingestionDate.setText(product.getIngestionDate().getTime().toString());
        if (product.getPlatformName().equals("Sentinel-1"))
            setSentinel1Data();
        else if (product.getPlatformName().equals("Sentinel-2"))
            setSentinel2Data();
    }

    private void setSentinel2Data() {
        sensorMode.setText(((Sentinel2Product)product).getCloudCoverPercentage()+"");
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
        sensorMode.setText(((Sentinel1Product)product).getSensorOperationalMode());
        polarisation.setText(((Sentinel1Product)product).getPolarizationMode());
        toggleSentinel1Data(true);
        toggleSentinel2Data(false);
    }

    public void setProduct(Product p) {
        this.product = p;
        setProductDetails();
        try {
            setImagePreviewAndMap();
        } catch (AuthenticationException | ParseException | IOException e) {
            logger.atError().log("Error loading bathymetry.shp: {0}",e);
        } catch (NotAuthenticatedException e) {
            e.printStackTrace();
        }

    }

    private void setImagePreviewAndMap() throws AuthenticationException, ParseException, IOException, NotAuthenticatedException {
        setMap();
        CopernicusService httpManager = CopernicusService.getInstance();
        InputStream contentFromURL = null;
        try {
            contentFromURL = httpManager.getContentFromURL(getQuicklook(product.getId()));
            System.out.println(image.getFitHeight());
            image.setImage(new Image(contentFromURL,image.getFitWidth(),image.getFitHeight(),false,false));
            System.out.println(image.getFitHeight());
        } catch (IOException e) {
            logger.atInfo().log("No preview image found for product name {}",product.getTitle());
            //httpManager.closeConnection();
            image.setImage(new Image(getClass().getResource("/img/no_photo.jpg").openStream(),image.getFitWidth(),image.getFitHeight(),false,false));
        }


    }

    private void setMap() throws ParseException {
        gtMap = new GTMap(300, 300,false);
        map.getChildren().add(gtMap);
        gtMap.createFeatureFromWKT(product.getFootprint(),product.getId());
        gtMap.createAndDrawProductsLayer();
        gtMap.goToSelection();
        gtMap.scroll(-96);
        gtMap.refresh();
    }

    public URL getQuicklook(String id) throws MalformedURLException {
        String url = "https://scihub.copernicus.eu/dhus/odata/v1/Products('"+id+"')/Products('Quicklook')/$value";
        return new URL(url);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //this.gtMap = new GTMap(458, 500);
        if (product!=null)
            setProductDetails();
        else
            logger.atError().log("Error while retrieving product data");

    }

    @Override
    public void setTabPaneComponent(TabPaneComponent component) {
        this.tabPaneComponent = component;
    }

    @Override
    public Parent getView() {
        return null;
    }

    @Override
    public Task<Parent> start() {
        return null;
    }

    @Override
    public String getName(){
        return getClass().getSimpleName();
    }


}
