package controller;

import com.jfoenix.controls.JFXSpinner;
import controller.search.OpenSearcherController;
import gui.TabPaneManager;
import gui.dialog.ScihubCredentialsDialog;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
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
        tabPane.getTabs().add(new Tab("Information"));
        tabPane.getTabs().get(0).setClosable(true);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/SearcherView.fxml"));
            Task<Parent> response = getOpenSearchTask(credentials, fxmlLoader);
            response.exceptionProperty().addListener(exceptionWhileOpenOpenSearch());
            response.setOnSucceeded(addOpenSearchTab(response.getValue()));
            new Thread(response).start();
        } else
            hideSpinner();
    }

    private EventHandler<WorkerStateEvent> addOpenSearchTab(Parent response) {
        return event -> {
            tabPane.addTab("Copernicus Open Search", response);
            hideSpinner();
            print("Login Successful! Copernicus Open Search opened");
        };
    }

    private ChangeListener<Throwable> exceptionWhileOpenOpenSearch() {
        return (observable, oldValue, newValue) -> {
            logger.atLevel(Level.WARN).log("Exception while login in searcher: {}", newValue.getMessage());
            if (newValue instanceof AuthenticationException) {
                showErrorDialog("Login", "An error occurred during login",
                        "Incorrect username or password");

                print("Copernicus Open Search: Incorrect username or password");
            } else
                print("Error opening Copernicus Open Search");

            hideSpinner();
        };
    }

    private Task<Parent> getOpenSearchTask(Pair<String, String> credentials, FXMLLoader fxmlLoader) {
        Task<Parent> response = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                Parent root1 = fxmlLoader.load();
                openSearcherController = fxmlLoader.getController();
                openSearcherController.login(credentials.getKey(), credentials.getValue());
                return root1;
            }
        };
        return response;
    }

    private void print(String text) {
        consoleDebug.appendText(text+"\n");
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
