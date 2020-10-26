package controller.processing;

import controller.interfaces.TabItem;
import gui.components.TabPaneComponent;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;
import jfxtras.styles.jmetro.JMetro;
import model.list.ProductListDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.DownloadConfiguration;
import utils.ThemeConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ProductListProcessingResultsController implements TabItem {
    static final Logger logger = LogManager.getLogger(ProductListProcessingResultsController.class.getName());

    @FXML
    private ScrollPane rootPane;
    @FXML
    private FlowPane resultsPane;

    private final FXMLLoader loader;
    private Parent parent;
    private TabPaneComponent tabPaneComponent;
    private ProductListDTO productListDTO;

    public ProductListProcessingResultsController(ProductListDTO productListDTO) {
        this.productListDTO = productListDTO;
        loader = new FXMLLoader(getClass().getResource("/fxml/ProductListProcessingResultsView.fxml"));
        loader.setController(this);
        try {
            parent = loader.load();
        } catch (IOException e) {
            parent = null;
            e.printStackTrace();
        }
    }

    @Override
    public void setTabPaneComponent(TabPaneComponent component) {
        this.tabPaneComponent = component;
    }

    @Override
    public Parent getView() {
        return parent;
    }

    @Override
    public Task<Parent> start() {
        return new Task<Parent>() {
            @Override
            protected Parent call() throws Exception {
                initSearch();
                return parent;
            }
        };
    }

    @Override
    public String getName() {
        return "Processing Results - "+productListDTO.getName();
    }

    @Override
    public String getItemId() {
        return "PR:"+productListDTO.getName();
    }

    private void initSearch() {
        //logger.atInfo().log("Init map refresh thread");
        ScheduledService<Boolean> svc = new ScheduledService<Boolean>() {
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    protected Boolean call() {
                        Platform.runLater(ProductListProcessingResultsController.this::searchFiles);
                        return true;
                    }
                };
            }
        };
        svc.setPeriod(Duration.millis(5000));
        svc.start();
    }

    private void searchFiles() {
        File file = new File(DownloadConfiguration.getListDownloadFolderLocation() + "\\" + productListDTO.getName()+"\\");
        Arrays.stream(file.listFiles())
                .filter(f->!f.getName().contains(".json")&&!f.getName().contains("tmp"))
                .forEach(this::loadThumbnail);
    }

    private void loadThumbnail(File file) {
        if (!isLoadedInFlowPane(file.getName()))
            loadResultItemView(file);
    }

    private boolean isLoadedInFlowPane(String name) {
        return resultsPane.getChildren().stream()
                .filter(c->c.getId().equals(name))
                .findAny()
                .orElse(null) != null;
    }

    private void loadResultItemView(File file) {
        System.out.println("New file");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProductListProcessingResultItemView.fxml"));
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            parent = null;
            e.printStackTrace();
        }

        parent.setId(file.getName());

        JMetro jMetro = ThemeConfiguration.getJMetroStyled();
        jMetro.setScene(new Scene(parent));

        ProductListProcessingResultItemController controller = loader.getController();
        Parent p = parent;
        Platform.runLater(()->{
            controller.setFile(file);
            resultsPane.getChildren().add(p);
        });

    }
}
