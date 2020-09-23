package controller.search;

import com.jfoenix.controls.JFXButton;
import gui.TabPaneManager;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import model.products.Product;

import java.io.IOException;
import java.net.URL;

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
                loader = new FXMLLoader(getClass().getResource("/fxml/ProductResultListCell.fxml"));
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
            details.setOnMouseClicked(e->{
                detailsEvent(product);
            });
            setText(null);
            setGraphic(root);
        }

    }

    public void detailsEvent(Product product) {
        URL location = getClass().getResource("/fxml/ProductDetails.fxml");
        FXMLLoader loader = new FXMLLoader(location);
        TabPaneManager tabPaneManager = TabPaneManager.getTabPaneManager();
        Task<Parent> response = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                Parent parent = loader.load();
                OpenSearchProductDetailsController controller = loader.getController();
                controller.setProduct(product);
                return parent;
            }
        };
        response.setOnSucceeded(event -> {
            tabPaneManager.addTab(product.getTitle().substring(0,10), response.getValue());
        });
        new Thread(response).start();
    }

}
