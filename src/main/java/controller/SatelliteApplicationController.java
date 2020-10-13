package controller;

import com.jfoenix.controls.JFXSpinner;
import controller.download.DownloadController;
import gui.components.*;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jfxtras.styles.jmetro.JMetroStyleClass;
import model.events.EventType;
import model.list.ProductListDTO;
import model.user.UserDTO;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.database.UserDBDAO;
import services.download.DownloadManager;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class SatelliteApplicationController implements Initializable {
    private TabPaneComponent tabPaneComponent;
    private MenuComponent menuController;
    private ListTreeViewComponent listTreeViewComponent;
    private ToolBarComponent toolBarComponent;
    private ConsoleComponent consoleComponent;

    @FXML
    private VBox menu;
    @FXML
    private GridPane gridPane;
    @FXML
    private Pane spinnerWait;
    @FXML
    private ScrollPane rootPane;
    @FXML
    private AnchorPane downloadPane;
    @FXML
    private AnchorPane console;
    @FXML
    private JFXSpinner wait;

    static final Logger logger = LogManager.getLogger(SatelliteApplicationController.class.getName());
    private UserDTO user;
    private DownloadManager downloadManager;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rootPane.getStyleClass().add(JMetroStyleClass.BACKGROUND);

        logger.atLevel(Level.INFO).log("Starting Satellite App...");
        wait.setVisible(false);
        initComponents();
    }

    private void initComponents() {
        initTabPaneComponent();
        initListTreeViewComponent();
        initMenuComponent();
        initToolBarComponent();
        initConsoleComponent();
        initDownloadManager();

        initListeners();
    }

    private void initDownloadManager() {
        this.downloadManager = new DownloadManager(2);
        downloadManager.addListener(EventType.DownloadEventType.COMPLETED, event -> {
            //if (event.getEvent().equals(EventType.DownloadEventType.COMPLETED))
                //consoleDebug.appendText("Download completed!\n");
        });
        new Thread(downloadManager).start();
        URL location = getClass().getResource("/fxml/DownloadView.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent parent = null;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        downloadPane.getChildren().add(parent);
        AnchorPane.setRightAnchor(parent,0.0);
        AnchorPane.setLeftAnchor(parent,0.0);
        AnchorPane.setTopAnchor(parent,0.0);
        AnchorPane.setBottomAnchor(parent,0.0);
        DownloadController controller = fxmlLoader.getController();
        controller.setDownload(downloadManager);

    }

    private void initListeners() {
        toolBarComponent.addComponentListener(EventType.ComponentEventType.LIST_CREATED, event -> {
            listTreeViewComponent.reload();
            consoleComponent.println((String) event.getValue());
        });
        toolBarComponent.addComponentListener(EventType.ComponentEventType.LIST_DELETED,event -> {
            listTreeViewComponent.reload();
            consoleComponent.println((String) event.getValue());
        });
        toolBarComponent.addComponentListener(EventType.ComponentEventType.LIST_UPDATED,event -> {
            listTreeViewComponent.reload();
            consoleComponent.println((String) event.getValue());
        });
    }

    private void initToolBarComponent() {
        logger.atInfo().log("Init ToolBar...");
        toolBarComponent = new ToolBarComponent(this);
        toolBarComponent.init();
        menu.getChildren().add(toolBarComponent);
        logger.atInfo().log("ToolBar loaded");
    }

    private void initTabPaneComponent() {
        logger.atInfo().log("Init TabPaneComponent...");
        tabPaneComponent = new TabPaneComponent(this);
        tabPaneComponent.init();
        gridPane.add(tabPaneComponent,2,0);
        logger.atInfo().log("TabPaneComponent loaded");
        tabPaneComponent.load(new InformationController());
    }

    private void initListTreeViewComponent() {
        logger.atInfo().log("Init ListTreeViewComponent...");
        listTreeViewComponent = new ListTreeViewComponent(this);
        listTreeViewComponent.init();
        gridPane.add(listTreeViewComponent,0,0);
        logger.atInfo().log("ListTreeViewComponent loaded");
    }

    private void initMenuComponent() {
        logger.atInfo().log("Init MenuComponent...");
        menuController = new MenuComponent(this);
        menuController.init();
        menu.getChildren().add(0,menuController.getView());
        logger.atInfo().log("MenuComponent loaded");
    }

    private void initConsoleComponent() {
        logger.atInfo().log("Init ConsoleComponent...");
        consoleComponent = new ConsoleComponent(this);
        consoleComponent.init();
        console.getChildren().add(consoleComponent);
        AnchorPane.setRightAnchor(consoleComponent,0.0);
        AnchorPane.setLeftAnchor(consoleComponent,0.0);
        AnchorPane.setTopAnchor(consoleComponent,0.0);
        AnchorPane.setBottomAnchor(consoleComponent,0.0);
        logger.atInfo().log("ConsoleComponent loaded");
    }

    public TabPaneComponent getTabController() {
        return tabPaneComponent;
    }

    public ListTreeViewComponent getListTreeViewController() {
        return listTreeViewComponent;
    }

    public ObservableList<ProductListDTO> getUserProductList() {
        return user.getProductListsDTO();
    }

    public UserDTO getUser() {
        return user;
    }

    public DownloadManager getDownload() {
        return downloadManager;
    }

    public void showWaitSpinner() {
        toggleSpinner(true);
    }

    public void hideWaitSpinner() {
        toggleSpinner(false);
    }

    private void toggleSpinner(boolean b) {
        wait.setManaged(b);
        wait.setVisible(b);
        spinnerWait.setVisible(b);
        spinnerWait.setManaged(b);}

    public void setUser(UserDTO userDTO) {
        this.user = userDTO;
        user.getProductListsDTO().addListener((ListChangeListener<ProductListDTO>) c -> {
            UserDBDAO instance = UserDBDAO.getInstance();
            instance.updateProductList(user);
            System.out.println("update user");
        });

        user.getSearchParameters().addListener((MapChangeListener<String, Map<String, String>>) change -> {
            UserDBDAO instance = UserDBDAO.getInstance();
            instance.save(user);
        });
        listTreeViewComponent.reload();
    }

}
