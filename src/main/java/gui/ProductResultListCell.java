package gui;

import com.jfoenix.controls.JFXButton;
import controller.OpenSearchProductDetailsController;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import model.products.Product;

import java.awt.event.MouseEvent;
import java.io.IOException;

public class ProductResultListCell extends ListCell<Product> {
    private FXMLLoader loader;

    @FXML
    private GridPane root;
    @FXML
    private Label title;
    @FXML
    private ImageView image;
    @FXML
    private Label platformName;
    @FXML
    private Label instrumentName;
    @FXML
    private Label size;
    @FXML
    private JFXButton details;

    @Override
    protected void updateItem(Product product, boolean empty) {
        super.updateItem(product, empty);

        if(empty || product == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/ProductResultListCell.fxml"));
                loader.setController(this);

                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            title.setText(product.getTitle());
            platformName.setText(product.getPlatformName());
            instrumentName.setText(product.getProductType());
            size.setText(product.getSize());
            image = new ImageView(this.getClass().getResource("/img/scihub_logo.svg").toString());
            details.setOnMouseClicked(e->{
                OpenSearchProductDetailsController ospdc = new OpenSearchProductDetailsController(product);
            });
            setText(null);
            setGraphic(root);
        }

    }

}
