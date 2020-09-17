package controller;

import com.jfoenix.controls.JFXSpinner;
import gui.dialog.ScihubCredentialsDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.ProductOData;
import model.ProductList;
import model.exception.AuthenticationException;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static utils.AlertFactory.showErrorDialog;

public class ListViewController implements Initializable {
    @FXML
    private StackPane rootPane;
    @FXML
    private TabPane tabPane;
    @FXML
    private Pane testPane;
    @FXML
    private ListView<ProductList> productList;
    @FXML
    private ListView<ProductOData> productInList;
    @FXML
    private Button deleteList;
    @FXML
    private Button createList;
    @FXML
    private MenuBar menuBar;
    @FXML
    private BorderPane borderPane;

    @FXML
    private JFXSpinner wait;

    private OpenSearcherController openSearcherController;
    private Stage searcherStage;
    private List<ProductList> userProductList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        wait.setVisible(false);
        //userProductList = new ArrayList<>();
        System.out.println(rootPane);
        /*createList.setOnAction(e -> {
            CreateProductListDialog createProductListDialog = new CreateProductListDialog();
            createProductListDialog.setOnHidden(event -> {
                addProductListAndRefresh(createProductListDialog.getProductList());
            });
            createProductListDialog.init();
            createProductListDialog.show();
        });*/
        tabPane.getTabs().add(new Tab("Searcher"));
        //productList.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> clearAndAddProductsToProductInList(productList.getSelectionModel().getSelectedItem()));

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
        searcher.setOnAction(e -> setSearcherScene());
        search.getItems().addAll(searcher);
        MenuBar menuBar = new MenuBar();

        menuBar.getMenus().addAll(file,edit,search);
        return menuBar;
    }

    private void setSearcherScene() {
        getSearcherStage();
        if (searcherStage != null)
            searcherStage.show();
    }

    public void credentialsDialog() {

    }

    public void getSearcherStage() {
        ScihubCredentialsDialog dialog = new ScihubCredentialsDialog();
        Optional<Pair<String, String>> stringStringPair = dialog.showAndWait();
        if (stringStringPair.isPresent() && searcherStage == null) {
                Pair<String, String> credentials = stringStringPair.get();
                URL location = getClass().getResource("/SearcherView.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(location);
                try {
                    Parent root1 = (Parent) fxmlLoader.load();
                    openSearcherController = fxmlLoader.getController();
                    openSearcherController.login(credentials.getKey(),credentials.getValue());
                    //openSearcherController.setProductList(userProductList);
                    //testPane.getChildren().add(root1);
                    searcherStage = new Stage();
                    searcherStage.setScene(new Scene(root1));
                    /*searcherStage.setOnCloseRequest(event -> {
                        //reloadLists();
                    });*/

                } catch (IOException ex) {
                    Logger logger = Logger.getLogger(getClass().getName());
                    logger.log(Level.SEVERE, "Failed to create new Window.", ex);
                    searcherStage = null;
                } catch (AuthenticationException e) {
                    Logger logger = Logger.getLogger(getClass().getName());
                    logger.log(Level.SEVERE, "Auth failed", e);
                    showErrorDialog("Login","An error occurred during login",
                            "Incorrect username or password");
                }
        }
    }



    private void reloadLists() {
        productList.getItems().clear();
        productList.getItems().addAll(userProductList);
        productInList.getItems().clear();
    }
}
