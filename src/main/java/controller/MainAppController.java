package controller;

import com.jfoenix.controls.JFXSpinner;
import controller.search.CopernicusOpenSearchController;
import controller.search.SearchController;
import gui.TabPaneManager;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetroStyleClass;
import model.ProductList;
import model.exception.AuthenticationException;
import model.user.UserDTO;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.*;

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

    private CopernicusOpenSearchController copernicusOpenSearchController;
    private Stage searcherStage;
    private List<ProductList> userProductList;
    private Map<String, SearchController> searchControllers;
    static final Logger logger = LogManager.getLogger(MainAppController.class.getName());
    private UserDTO user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        logger.atLevel(Level.INFO).log("Starting Satellite App...");
        searchControllers = new HashMap<>();
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
        search.setId("OpenSearcher");
        searcher.setOnAction(e -> showSearchView(search.getId()));
        search.getItems().addAll(searcher);
        menuBar.getMenus().addAll(file,edit,search);
    }

    private void showSearchView(String id) {
        if (!searchControllers.containsKey(id)) {
            CopernicusOpenSearchController copernicusOpenSearchController = new CopernicusOpenSearchController(id);
            initSearchStage(copernicusOpenSearchController);

        } else {
            tabPane.addTab(id,searchControllers.get(id).getView());
        }

    }

    public void initSearchStage(SearchController controller) {
        Task<Parent> task = controller.start();
        task.exceptionProperty().addListener(exceptionWhileOpenSearch(controller.getId()));
        task.setOnSucceeded(addSearchTab(controller));
        showSpinner();
        new Thread(task).start();
    }

    private EventHandler<WorkerStateEvent> addSearchTab(SearchController controller) {
        return event -> {
            searchControllers.put(controller.getId(),controller);
            tabPane.addTab(controller.getId(), controller.getView());
            hideSpinner();
        };
    }

    private ChangeListener<Throwable> exceptionWhileOpenSearch(String id) {
        return (observable, oldValue, newValue) -> {
            logger.atLevel(Level.WARN).log("Exception while opening searcher -> {}: {}", id, newValue.getMessage());
            showErrorDialog(id, "An error occurred while opening "+id,
                    newValue.getMessage());
            hideSpinner();
        };
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

    public void setUser(UserDTO userDTO) {
        this.user = userDTO;
    }
}
