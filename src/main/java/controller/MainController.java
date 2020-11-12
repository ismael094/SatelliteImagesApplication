package controller;

import com.jfoenix.controls.JFXSpinner;
import controller.download.DownloadController;
import controller.processing.monitors.SimpleProcessingMonitorController;
import gui.EventExecuteListener;
import gui.ExecutedEvent;
import gui.components.*;
import gui.components.TabPaneComponent;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import jfxtras.styles.jmetro.JMetroStyleClass;
import model.list.ProductListDTO;
import model.preprocessing.ProcessorManager;
import model.user.UserDTO;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.database.ProductListDBDAO;
import services.download.ProductDownloader;
import services.download.Downloader;

import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    private TabPaneComponent tabPaneComponent;
    private MenuComponent menuComponent;
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
    private ProductDownloader productDownloader;
    private ProcessorManager processor;
    private UserManager userManager;
    private List<EventExecuteListener> listeners;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rootPane.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        logger.atLevel(Level.INFO).log("Starting Satellite App...");
        wait.setVisible(false);
        listeners = new ArrayList<>();
    }

    public void fireEvent(ExecutedEvent e) {
        listeners.forEach(l->l.onEventExecute(e));
        consoleComponent.println((String) e.getValue());
    }

    public void addListener(EventExecuteListener l) {
        listeners.add(l);
    }

    public void initComponents() {
        initTabPaneComponent();
        initListTreeViewComponent();
        initMenuComponent();
        initToolBarComponent();
        initConsoleComponent();
        initDownloadManager();
        initProcessorManager();
        initComponentListeners();
    }

    public void setUser(UserDTO user) {
        this.userManager = new UserManager(user);
    }

    private void initProcessorManager() {
        processor = new ProcessorManager(new SimpleBooleanProperty());
        processingController.setProcessorManager(processor);
        AnchorPane.setRightAnchor(processing,0.0);
        AnchorPane.setLeftAnchor(processing,0.0);
        AnchorPane.setTopAnchor(processing,0.0);
        AnchorPane.setBottomAnchor(processing,0.0);
    }

    private void initDownloadManager() {
        this.productDownloader = new ProductDownloader(2);
        /*copernicusDownloader.addListener(EventType.ComponentEventType.DOWNLOAD_COMPLETED, event -> {
            //if (event.getEvent().equals(EventType.DownloadEventType.COMPLETED))
                //consoleDebug.appendText("Download completed!\n");
        });*/
        new Thread(productDownloader).start();
        AnchorPane.setRightAnchor(download,0.0);
        AnchorPane.setLeftAnchor(download,0.0);
        AnchorPane.setTopAnchor(download,0.0);
        AnchorPane.setBottomAnchor(download,0.0);
        downloadController.setDownload(productDownloader);

    }

    private void initComponentListeners() {
        toolBarComponent.addComponentListener(event -> {
            listTreeViewComponent.reload();
            consoleComponent.println((String) event.getValue());
        });

        tabPaneComponent.addComponentListener(e->{
            //listTreeViewComponent.reload();
            consoleComponent.println((String) e.getValue());
        });

        listTreeViewComponent.addComponentListener(e->{
            consoleComponent.println((String) e.getValue());
        });

        productDownloader.addListener(e->{
            //AlertFactory.showInfoDialog("Downloader","Downloader", String.valueOf(e.getValue()));
            consoleComponent.println((String) e.getValue());
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
        menuComponent = new MenuComponent(this);
        menuComponent.init();
        menu.getChildren().add(0, menuComponent.getView());
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

    public void saveProductList(ProductListDTO p) {
        //ProductListEvent event = new ProductListEvent(this,"change");
        //listeners.forEach(l->l.onProductListChange(event));
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ProductListDBDAO.getInstance().save(p);
                return null;
            }
        };
        new Thread(task).start();
    }


    public TabPaneComponent getTabComponent() {
        return tabPaneComponent;
    }

    public ToolBarComponent getToolBarComponent() {
        return toolBarComponent;
    }

    public ListTreeViewComponent getListTreeViewComponent() {
        return listTreeViewComponent;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public Downloader getDownloader() {
        return productDownloader;
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

    public ProcessorManager getProductProcessor() {
        return processor;
    }

    public AnchorPane getProcessingView() {
        return processing;
    }
}
