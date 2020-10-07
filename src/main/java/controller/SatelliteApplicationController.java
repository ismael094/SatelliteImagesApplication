package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import gui.components.*;
import controller.search.CopernicusOpenSearchController;
import controller.search.SearchController;
import gui.components.listener.ComponentEventType;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jfxtras.styles.jmetro.JMetroStyleClass;
import model.list.ProductListDTO;
import model.user.UserDTO;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.database.ProductListDBDAO;
import services.database.UserDBDAO;

import java.net.URL;
import java.util.*;

public class SatelliteApplicationController implements Initializable {
    private TabPaneComponent tabPaneComponent;
    private MenuComponent menuController;
    private ListTreeViewComponent listTreeViewComponent;
    private ToolBarComponent toolBarComponent;

    //private final ListTreeViewManager listTreeViewManager;
    //private final ConsoleManager consoleManager;
    //private MainAppController mainController;
    @FXML
    private VBox menu;
    @FXML
    private JFXButton selected;
    @FXML
    private TreeView<Object> listTree;
    @FXML
    private JFXButton test;
    @FXML
    private TextArea consoleDebug;
    @FXML
    private GridPane gridPane;
    @FXML
    private Pane spinnerWait;
    @FXML
    private ScrollPane rootPane;

    @FXML
    private JFXSpinner wait;

    private CopernicusOpenSearchController copernicusOpenSearchController;
    private List<Component> components;

    static final Logger logger = LogManager.getLogger(SatelliteApplicationController.class.getName());
    private UserDTO user;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rootPane.getStyleClass().add(JMetroStyleClass.BACKGROUND);

        logger.atLevel(Level.INFO).log("Starting Satellite App...");

        components = new ArrayList<>();
        components.add(toolBarComponent);
        components.add(tabPaneComponent);
        components.add(listTreeViewComponent);
        components.add(menuController);
        wait.setVisible(false);
        initComponents();
    }

    private void initComponents() {
        initTabPaneComponent();
        initListTreeViewComponent();
        initMenuComponent();
        initToolBarComponent();

        initListeners();
    }

    private void initListeners() {
        toolBarComponent.addComponentListener(ComponentEventType.LIST_CREATED,event -> {
            listTreeViewComponent.reload();

        });
        toolBarComponent.addComponentListener(ComponentEventType.LIST_DELETED,event -> {
            listTreeViewComponent.reload();

        });
        toolBarComponent.addComponentListener(ComponentEventType.LIST_UPDATED,event -> {
            listTreeViewComponent.reload();

        });
    }

    private void saveProductList() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                UserDBDAO.getInstance().save(user);
                return null;
            }
        };
        new Thread(task).start();
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

    public TabPaneComponent getTabController() {
        return tabPaneComponent;
    }

    public ListTreeViewComponent getListTreeViewController() {
        return listTreeViewComponent;
    }

    public ObservableList<ProductListDTO> getUserProductList() {
        return user.getProductListsDTO();
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
        listTreeViewComponent.reload();
    }

}
