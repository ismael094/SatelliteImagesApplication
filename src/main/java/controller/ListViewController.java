package controller;

import gui.dialog.CreateListDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Product;
import model.ProductList;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ListViewController {
    @FXML
    public ListView<ProductList> productList;

    @FXML
    public ListView<Product> productInList;

    @FXML
    public Button deleteList;

    @FXML
    public Button createList;

    @FXML
    private MenuBar menuBar;

    @FXML
    private BorderPane borderPane;

    private SearcherController searcherController;
    private Stage searcherStage;

    public void initialize()  {

        createList.setOnAction(e -> {
            System.out.println("Esto es una prueba");
            ProductList pl = new ProductList("Nmae","descriptoin");
            productList.getItems().add(pl);
        });

        borderPane.setTop(new VBox(getMenu()));
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
                searcherController.setProductList(productList);
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
        productList.refresh();
        productInList.getItems().clear();
    }


}
