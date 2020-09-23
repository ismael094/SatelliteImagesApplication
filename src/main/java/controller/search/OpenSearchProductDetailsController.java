package controller.search;

import gui.GTMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import model.exception.AuthenticationException;
import model.products.Product;
import model.products.Sentinel1Product;
import model.products.Sentinel2Product;
import org.locationtech.jts.io.ParseException;
import utils.HTTPAuthManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class OpenSearchProductDetailsController implements Initializable {

    @FXML
    private Label platformDetails;
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
    private Pane container;
    @FXML
    private Pane map;
    @FXML
    private FlowPane root;
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


    private void setProductDetails() {
        System.out.println(product);
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
        platformDetails.setText("Sentinel-2 Data");
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
        platformDetails.setText("Sentinel-1 Data");
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
            e.printStackTrace();
        }

    }

    private void setImagePreviewAndMap() throws AuthenticationException, ParseException, IOException {
        setMap();
        HTTPAuthManager httpManager = HTTPAuthManager.getHttpManager("", "");
        InputStream contentFromURL = null;
        try {
            contentFromURL = httpManager.getContentFromURL(getQuicklook(product.getId()));
            image.setImage(new Image(contentFromURL,382,382,false,false));

        } catch (IOException e) {
            httpManager.closeConnection();
            image.setImage(new Image(getClass().getResource("/img/no_photo.jpg").openStream(),382,382,false,false));
        }
        image.setFitWidth(382);
        image.setFitHeight(382);

    }

    private void setMap() throws ParseException {
        GTMap gtMap = new GTMap(382, 382);
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
            System.out.println("dklsfjml");
    }
}
