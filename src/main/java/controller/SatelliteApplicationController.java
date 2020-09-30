package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import gui.components.ListTreeViewComponent;
import gui.components.MenuComponent;
import gui.components.TabPaneComponent;
import controller.search.CopernicusOpenSearchController;
import controller.search.SearchController;
import gui.components.ToolBarComponent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jfxtras.styles.jmetro.JMetroStyleClass;
import model.ProductList;
import model.user.UserDTO;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private List<ProductList> userProductList;
    private Map<String, SearchController> searchControllers;

    static final Logger logger = LogManager.getLogger(SatelliteApplicationController.class.getName());
    private UserDTO user;
    private List<ProductList> productLists;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rootPane.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        productLists = new ArrayList<>();
        logger.atLevel(Level.INFO).log("Starting Satellite App...");
        initComponents();
        searchControllers = new HashMap<>();
        wait.setVisible(false);
    }

    private void initComponents() {
        initTabPaneComponent();
        initListTreeViewComponent();
        initMenuComponent();
        initToolBarComponent();
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

    public List<ProductList> getUserProductList() {
        return productLists;
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
    }

}
