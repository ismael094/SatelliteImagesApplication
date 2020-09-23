package controller;

import com.jfoenix.controls.JFXSpinner;
import controller.search.OpenSearcherController;
import gui.TabPaneManager;
import gui.dialog.ScihubCredentialsDialog;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Pair;
import jfxtras.styles.jmetro.JMetroStyleClass;
import model.ProductOData;
import model.ProductList;
import model.exception.AuthenticationException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static utils.AlertFactory.showErrorDialog;

public class MainAppController implements Initializable {
    @FXML
    private TextArea consoleDebug;
    @FXML
    private GridPane gridPane;
    @FXML
    private Pane tabPaneContainer;
    @FXML
    private Pane spinnerWait;
    @FXML
    private ScrollPane rootPane;
    @FXML
    private TabPaneManager tabPane;
    @FXML
    private MenuBar menuBar;

    @FXML
    private JFXSpinner wait;

    private OpenSearcherController openSearcherController;
    private Stage searcherStage;
    private List<ProductList> userProductList;


    static final Logger logger = LogManager.getLogger(MainAppController.class.getName());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.atLevel(Level.INFO).log("Starting Satellite App...");

        wait.setVisible(false);
        rootPane.getStyleClass().add(JMetroStyleClass.BACKGROUND);

        tabPane = TabPaneManager.getTabPaneManager();
        tabPane.getStyleClass().add("myTab");
        gridPane.add(tabPane,1,0);
        tabPane.getTabs().add(new Tab("Searcher"));
        tabPane.getTabs().get(0).setClosable(true);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        //productList.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> clearAndAddProductsToProductInList(productList.getSelectionModel().getSelectedItem()));
        getMenu();
    }

    private void getMenu() {

        Menu file = new Menu("File");
        MenuItem close = new MenuItem("Exit");
        close.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });
        file.getItems().addAll(close);
        Menu edit = new Menu("Edit");
        Menu search = new Menu("Searchers");
        MenuItem searcher = new MenuItem("Copernicus Open Search");
        searcher.setOnAction(e -> createSearcherScene());
        search.getItems().addAll(searcher);
        menuBar.getMenus().addAll(file,edit,search);
    }

    private void createSearcherScene() {
        getSearcherStage();
        if (searcherStage != null)
            searcherStage.show();
    }

    public void getSearcherStage() {
        ScihubCredentialsDialog dialog = new ScihubCredentialsDialog();
        Optional<Pair<String, String>> stringStringPair = dialog.showAndWait();
        showSpinner();
        if (stringStringPair.isPresent() && searcherStage == null) {
            Pair<String, String> credentials = stringStringPair.get();
            URL location = getClass().getResource("/fxml/SearcherView.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Task<Parent> response = new Task<>() {
                @Override
                protected Parent call() throws Exception {
                    Parent root1 = fxmlLoader.load();
                    openSearcherController = fxmlLoader.getController();
                    openSearcherController.login(credentials.getKey(),credentials.getValue());
                    return root1;
                }
            };
            response.exceptionProperty().addListener((observable, oldValue, newValue) ->  {
                logger.atLevel(Level.WARN).log("Exception while login in searcher: {0}",newValue);
                if(newValue != null) {
                    if (newValue instanceof AuthenticationException) {
                        showErrorDialog("Login","An error occurred during login",
                                "Incorrect username or password");

                        consoleDebug.appendText("Copernicus Open Search: Incorrect username or password\n");
                    }
                    hideSpinner();
                }
            });
            response.setOnSucceeded(event -> {
                tabPane.addTab("Copernicus Open Search", response.getValue());
                hideSpinner();
                consoleDebug.appendText("Login Successful! Copernicus Open Search opened\n");
            });
            new Thread(response).start();
        } else
            hideSpinner();
    }

    private void showSpinner() {
        toggleSpinner(true);
    }

    private void hideSpinner() {
        toggleSpinner(false);
    }

    private void toggleSpinner(boolean b) {
        wait.setManaged(b);
        wait.setVisible(b);
        spinnerWait.setVisible(b);
        spinnerWait.setManaged(b);}

}
