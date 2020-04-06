package controller;

import gui.dialog.CreateProductListDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Product;
import model.ProductList;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListViewController {
    @FXML
    private ListView<ProductList> productList;

    @FXML
    private ListView<Product> productInList;

    @FXML
    private Button deleteList;

    @FXML
    private Button createList;

    @FXML
    private MenuBar menuBar;

    @FXML
    private BorderPane borderPane;

    private SearcherController searcherController;
    private Stage searcherStage;
    private List<ProductList> userProductList;

    public void initialize()  {
        userProductList = new ArrayList<>();
        createList.setOnAction(e -> {
            CreateProductListDialog createProductListDialog = new CreateProductListDialog();
            createProductListDialog.setOnHidden(event -> {
                addProductListAndRefresh(createProductListDialog.getProductList());
            });
            createProductListDialog.init();
            createProductListDialog.show();
        });

        productList.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> clearAndAddProductsToProductInList(productList.getSelectionModel().getSelectedItem()));

        borderPane.setTop(new VBox(getMenu()));
    }

    private void addProductListAndRefresh(ProductList pl) {
        if (pl != null) {
            userProductList.add(pl);
            productList.getItems().add(pl);
            productList.refresh();
        }
    }

    private void clearAndAddProductsToProductInList(ProductList selectedItem) {
        productInList.getItems().clear();
        productInList.getItems().addAll(selectedItem.getProducts());
        productInList.refresh();
    }


    private MenuBar getMenu() {
        Menu file = new Menu("File");
        Menu edit = new Menu("Edit");
        Menu search = new Menu("Search");
        MenuItem searcher = new MenuItem("Open Searcher");
        searcher.setOnAction(e -> showSearcherScene());
        search.getItems().addAll(searcher);
        MenuBar menuBar = new MenuBar();

        menuBar.getMenus().addAll(file,edit,search);
        return menuBar;
    }

    private void showSearcherScene() {
        getSearcherStage();
        if (searcherStage != null)
            searcherStage.show();
    }

    public void getSearcherStage() {
        if (searcherStage == null) {
            URL location = getClass().getResource("/Sample.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            try {
                Parent root1 = (Parent) fxmlLoader.load();
                searcherController = fxmlLoader.getController();
                searcherController.setProductList(userProductList);
                searcherStage = new Stage();
                searcherStage.setScene(new Scene(root1));
                searcherStage.setOnCloseRequest(event -> {
                    reloadLists();
                });
            } catch (IOException ex) {
                Logger logger = Logger.getLogger(getClass().getName());
                logger.log(Level.SEVERE, "Failed to create new Window.", ex);
                searcherStage = null;
            }
        }
    }

    private void reloadLists() {
        productList.getItems().clear();
        productList.getItems().addAll(userProductList);
        productInList.getItems().clear();
    }


}
