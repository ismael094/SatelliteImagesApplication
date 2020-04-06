package gui.dialog;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Product;
import model.ProductList;

import java.util.List;

public class AddProductToProductListDialog extends Stage {

    private final VBox group;
    private final List<ProductList> userProductList;
    private final ListView<ProductList> productList;
    private int selectedItem;

    public AddProductToProductListDialog(List<ProductList> productList) {
        this.group = new VBox();
        this.productList = new ListView<>();
        this.userProductList = productList;
        this.selectedItem = -1;
        addUserListToView();
    }

    private void addUserListToView() {
        productList.getItems().addAll(userProductList);
    }

    public void init() {
        initStyle(StageStyle.UTILITY);
        productList.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
                if(mouseEvent.getClickCount() == 2){
                    System.out.println(productList.getSelectionModel().getSelectedIndex());
                    this.selectedItem = productList.getSelectionModel().getSelectedIndex();
                    close();
                }
            }
        });
        Label label = new Label("Select the list to add the product");
        label.setStyle("-fx-font: 24 arial;");

        group.getChildren().addAll(label, productList);
        Button btn = new Button("Close");
        btn.setOnAction(e -> {
            close();
        });

        Button btn2 = new Button("Create new list");
        btn2.setOnAction(e -> {
            CreateProductListDialog prodDialog = new CreateProductListDialog();
            prodDialog.init();
            prodDialog.setOnHidden(event -> {
                addProductListToList(prodDialog.getProductList());
            });
            prodDialog.show();

        });

        group.getChildren().addAll(btn, btn2);
        Scene scene = new Scene(group);
        setSize();
        setScene(scene);
    }

    private void addProductListToList(ProductList pl) {
        if (pl == null)
            return;
        productList.getItems().add(pl);
        userProductList.add(pl);
        productList.refresh();
    }

    private void setSize() {
        setWidth(600);
        setHeight(400);
    }

    public int getSelectedItem() {
        return selectedItem;
    }
}
