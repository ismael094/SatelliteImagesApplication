package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import model.products.Product;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class OpenSearchProductDetailsController implements Initializable {
    private FXMLLoader loader;

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

    public OpenSearchProductDetailsController(Product product) {
        loader = new FXMLLoader(getClass().getResource("/ProductDetails.fxml"));
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setProductDetails(product);
    }

    private void setProductDetails(Product product) {
        name.setText(product.getTitle());
        platName.setText(product.getPlatformName());
        productType.setText(product.getProductType());
        size.setText(product.getSize());
        //ingestionDate.setText(product.getIngestionDate().toString());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
