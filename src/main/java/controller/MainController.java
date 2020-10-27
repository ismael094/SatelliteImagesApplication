package controller;

import com.jfoenix.controls.JFXSpinner;
import controller.download.DownloadController;
import controller.interfaces.ProductListTabItem;
import controller.interfaces.TabItem;
import controller.processing.SimpleProcessingMonitorController;
import gui.components.*;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import model.processing.ProcessorManager;
import model.processing.workflow.WorkflowDTO;
import model.processing.workflow.WorkflowType;
import model.products.ProductDTO;
import model.user.UserDTO;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.database.UserDBDAO;
import services.database.WorkflowDBDAO;
import services.download.CopernicusDownloader;
import services.download.Downloader;
import services.processing.Processor;
import utils.AlertFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    private TabPaneComponent tabPaneComponent;
    private MenuComponent menuController;
    private ListTreeViewComponent listTreeViewComponent;
    private ToolBarComponent toolBarComponent;
    private ConsoleComponent consoleComponent;

    @FXML
    private AnchorPane processing;
    @FXML
    private SimpleProcessingMonitorController processingController;
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
    @FXML
    private GridPane download;
    @FXML
    private DownloadController downloadController;

    static final Logger logger = LogManager.getLogger(MainController.class.getName());
    private UserDTO user;
    private CopernicusDownloader copernicusDownloader;
    private ProcessorManager processor;

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
        initProcessors();

        initListeners();
    }

    private void initProcessors() {
        processor = new ProcessorManager();
        processingController.setProcessorManager(processor);
        AnchorPane.setRightAnchor(processing,0.0);
        AnchorPane.setLeftAnchor(processing,0.0);
        AnchorPane.setTopAnchor(processing,0.0);
        AnchorPane.setBottomAnchor(processing,0.0);
    }

    private void initDownloadManager() {
        this.copernicusDownloader = new CopernicusDownloader(2);
        copernicusDownloader.addListener(EventType.DownloadEventType.COMPLETED, event -> {
            //if (event.getEvent().equals(EventType.DownloadEventType.COMPLETED))
                //consoleDebug.appendText("Download completed!\n");
        });
        new Thread(copernicusDownloader).start();
        /*URL location = getClass().getResource("/fxml/DownloadView.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Parent parent = null;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        downloadPane.getChildren().add(parent);*/
        AnchorPane.setRightAnchor(download,0.0);
        AnchorPane.setLeftAnchor(download,0.0);
        AnchorPane.setTopAnchor(download,0.0);
        AnchorPane.setBottomAnchor(download,0.0);
        //DownloadController controller = fxmlLoader.getController();
        downloadController.setDownload(copernicusDownloader);

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

    public ToolBarComponent getToolBarComponent() {
        return toolBarComponent;
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

    public Downloader getDownload() {
        return copernicusDownloader;
    }

    public Parent getRoot() {
        return rootPane;
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
            while (c.next())
                if (c.wasAdded()) {
                    UserDBDAO instance = UserDBDAO.getInstance();
                    System.out.println(c.getAddedSubList().size());
                    c.getAddedSubList().forEach(p-> instance.addProductList(user,p));
                } else {
                    UserDBDAO instance = UserDBDAO.getInstance();
                    c.getRemoved().forEach(p->{
                        instance.removeProductList(user,p);
                    });

                }
            System.out.println("update user");
        });

        user.getSearchParameters().addListener((MapChangeListener<String, Map<String, String>>) change -> {
            UserDBDAO instance = UserDBDAO.getInstance();
            instance.save(user);
        });

        user.getWorkflows().addListener((ListChangeListener<WorkflowDTO>) c -> {
            while (c.next())
                if (c.wasAdded()) {
                    UserDBDAO instance = UserDBDAO.getInstance();
                    c.getAddedSubList().forEach(p-> instance.addNewWorkflow(user,p));
                } else {
                    UserDBDAO instance = UserDBDAO.getInstance();
                    c.getRemoved().forEach(p->{
                        instance.removeWorkflow(user,p);
                    });

            }
        });

        listTreeViewComponent.reload();
    }

    public void updateUserWorkflows(ObservableList<WorkflowDTO> workflowsDTO) {
        WorkflowDBDAO instance = WorkflowDBDAO.getInstance();
        workflowsDTO.forEach(instance::save);
    }

    public ProcessorManager getProcessor() {
        return processor;
    }

    public Processor getProcessorFor(ProductDTO productDTO) {
        return processor.getProcessor(productDTO);
    }

    public void process() {




    }

    public AnchorPane getProcessing() {
        return processing;
    }
}
