package controller.list;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.ProductList;
import model.products.Product;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateListController implements Initializable {

    @FXML
    private BorderPane root;
    @FXML
    private TextField name;
    @FXML
    private TextField description;
    @FXML
    private Button create;

    private ProductList productList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        create.setOnAction(e->{
            productList = new ProductList(name.getText(),description.getText());
            Stage stage = (Stage) root.getScene().getWindow();
            stage.close();
        });

        create.disableProperty().bind(
                Bindings.isEmpty(name.textProperty())
                        .or(Bindings.isEmpty(description.textProperty())));
    }

    public ProductList getProductList() {
        return productList;
    }
}
