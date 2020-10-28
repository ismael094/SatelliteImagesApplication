package controller.results;

import controller.interfaces.TabItem;
import gui.components.TabPaneComponent;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import jfxtras.styles.jmetro.JMetro;
import model.list.ProductListDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.DownloadConfiguration;
import utils.ThemeConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.*;

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
    private ObservableList<File> files;

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
                System.out.println("BYE BYE");
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
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                System.out.println(productListDTO.getName());
                File file = new File(DownloadConfiguration.getListDownloadFolderLocation() + "\\" + productListDTO.getName());
                System.out.println(file.listFiles().length);

                try {
                    for (File f : file.listFiles()) {
                        if (f.isDirectory()) {
                            for (File listFile : f.listFiles()) {
                                loadFile(listFile);
                            }
                        }
                        System.out.println(f.getName());
                        loadFile(f);
                    }

                    WatchService watcher = FileSystems.getDefault().newWatchService();
                    Path dir = Paths.get(DownloadConfiguration.getListDownloadFolderLocation() + "\\" + productListDTO.getName());
                    WatchKey key = dir.register(watcher,
                            ENTRY_CREATE,
                            ENTRY_DELETE,
                            ENTRY_MODIFY);
                    searchFiles(watcher, dir);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        new Thread(task).start();

    }

    private void searchFiles(WatchService watcher, Path dir) {
        while (true) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                WatchEvent<Path> ev = (WatchEvent<Path>)event;
                Path filename = ev.context();
                Path child = dir.resolve(filename);
                File file = child.toFile();
                System.out.println(kind + " - " + file.getName());

                if (kind == ENTRY_DELETE) {
                    if (file.isDirectory()) {
                        delete(file.listFiles());
                    } else {
                        removeFile(file);
                    }
                } else if (kind == ENTRY_CREATE) {
                    if (file.isDirectory()) {
                        for (File listFile : file.listFiles()) {
                            loadFile(listFile);
                        }
                    }
                    loadFile(file);
                } else if (kind == ENTRY_MODIFY) {
                    if (file.isDirectory()) {
                        delete(file.listFiles());
                    } else {
                        if (!isLoadedInFlowPane(file.getName()))
                            loadFile(file);
                    }
                }

            }


            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    private void delete(File[] listFiles) {
        List<File> files = Arrays.asList(listFiles);
        Platform.runLater(()->{
            List<Node> nodes = new ArrayList<>();
            resultsPane.getChildren().forEach(c->{
                if (!isContain(files,c.getId()))
                    nodes.add(c);
            });
            resultsPane.getChildren().removeAll(nodes);
            add(listFiles);
        });
    }

    private void add(File[] listFiles) {
        List<Node> nodes = new ArrayList<>();
        Arrays.asList(listFiles).forEach(f->{
            if (!isLoadedInFlowPane(f.getName()))
                nodes.add(loadResultItemView(f));
        });
        resultsPane.getChildren().addAll(nodes);
    }

    private boolean isContain(List<File> list, String filename) {
        return list.stream()
                .filter(f->f.getName().equals(filename))
                .findAny()
                .orElse(null) != null;
    }

    private synchronized void removeFile(File file) {
        if (isLoadedInFlowPane(file.getName())) {
            Node node = getChildren(file.getName());
            if (node != null)
                Platform.runLater(()->resultsPane.getChildren().remove(node));
        }
    }

    private synchronized void loadFile(File file) {
        if (file.isDirectory())
            return;
        String[] a = file.getName().split("\\.");
        if (a.length == 1)
            return;
        String extension = a[1];
        if (!file.getName().contains("tmp") && (extension.equals("tif") || extension.equals("PNG")) && !isLoadedInFlowPane(file.getName())) {
            System.out.println("LOADED");
            Platform.runLater(()->resultsPane.getChildren().add(loadResultItemView(file)));
        }
    }

    private synchronized boolean isLoadedInFlowPane(String name) {
        return getChildren(name) != null;
    }

    private synchronized Node getChildren(String name) {
        return resultsPane.getChildren().stream()
                .filter(c->c.getId().equals(name))
                .findAny()
                .orElse(null);
    }

    private synchronized Parent loadResultItemView(File file) {
        System.out.println(file.getName());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProductListProcessingResultItemView.fxml"));
        Parent parent = null;
        try {
            parent = loader.load();
            parent.setId(file.getName());
        } catch (IOException e) {
            parent = null;
            e.printStackTrace();
        }

        JMetro jMetro = ThemeConfiguration.getJMetroStyled();
        jMetro.setScene(new Scene(parent));

        parent.setId(file.getName());

        ProductListProcessingResultItemController controller = loader.getController();
        controller.setFile(file);
        return parent;

    }
}
