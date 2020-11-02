package controller.postprocessing;

import controller.interfaces.ProcessingResultsTabItem;
import controller.interfaces.TabItem;
import gui.components.TabPaneComponent;
import javafx.application.Platform;
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
import model.postprocessing.ProcessingResults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.DownloadConfiguration;
import utils.FileUtils;
import utils.ThemeConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static java.nio.file.StandardWatchEventKinds.*;

public class ProductListProcessingResultsController implements TabItem, ProcessingResultsTabItem {
    static final Logger logger = LogManager.getLogger(ProductListProcessingResultsController.class.getName());

    @FXML
    private ScrollPane rootPane;
    @FXML
    private FlowPane resultsPane;

    private final FXMLLoader loader;
    private Parent parent;
    private TabPaneComponent tabPaneComponent;
    private ProductListDTO productListDTO;
    private ProcessingResults processingResults;
    private Map<String,File> files;
    private WatchService watcher;
    private Path dir;

    public ProductListProcessingResultsController(ProductListDTO productListDTO) {
        processingResults = new ProcessingResults();
        files = new HashMap<>();
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
                onListFolderChangeReloadSearch();
                initSearch();
                return parent;
            }
        };
    }

    private void onListFolderChangeReloadSearch() {
        DownloadConfiguration.getDownloadPreferences().addPreferenceChangeListener(preferenceChangeEvent -> {
            try {
                String key = preferenceChangeEvent.getKey();
                System.out.println(preferenceChangeEvent.getKey().equals("listFolder"));
                if (preferenceChangeEvent.getKey().equals("listFolder")) {
                    watcher.close();
                    Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            resultsPane.getChildren().clear();
                            return null;
                        }
                    };

                    Platform.runLater(task);
                    task.get();
                    initSearch();
                }

            } catch (IOException | InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
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
                File file = new File(DownloadConfiguration.getListDownloadFolderLocation() + "\\" + productListDTO.getName());
                FileUtils.createFolderIfNotExists(file.getAbsolutePath());
                try {
                    for (File f : file.listFiles()) {
                        entryCreated(f);
                    }

                    watcher = FileSystems.getDefault().newWatchService();
                    dir = Paths.get(DownloadConfiguration.getListDownloadFolderLocation() + "\\" + productListDTO.getName());
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

    public boolean haveBeenProcessed() {
        if (productListDTO == null)
            return false;
        File file = new File(DownloadConfiguration.getListDownloadFolderLocation() + "\\" + productListDTO.getName());

        return file.exists() && file.listFiles() != null && file.listFiles().length > 2;
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

                File file = dir.resolve(ev.context()).toFile();

                if (kind == ENTRY_DELETE) {
                    entryDelete(file);
                } else if (kind == ENTRY_CREATE) {
                    entryCreated(file);
                } else if (kind == ENTRY_MODIFY) {
                    entryModified(file);
                }

                System.out.println(processingResults.toString());
            }

            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    private void entryModified(File file) {
        if (file.isDirectory()) {
            reload(file.listFiles());
        } else {
            if (!isLoadedInFlowPane(file.getName()))
                loadFile(file);
        }
    }

    private void entryCreated(File file) {
        if (file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                loadFile(listFile);
            }
        }
        loadFile(file);
    }

    private void entryDelete(File file) {
        if (file.isDirectory()) {
            reload(file.listFiles());
        } else {
            removeFile(file);
        }
    }

    private void reload(File[] listFiles) {
        List<File> files = Arrays.asList(listFiles);
        Platform.runLater(()->{
            List<Node> nodes = new ArrayList<>();
            resultsPane.getChildren().forEach(c->{
                if (files.contains(this.files.get(c.getId()))) {
                    nodes.add(c);
                    processingResults.removeFile(this.files.get(c.getId()));
                    this.files.remove(c.getId());
                }
            });
            resultsPane.getChildren().removeAll(nodes);
            addFilesToFlownPane(listFiles);
        });
    }

    private void addFilesToFlownPane(File[] listFiles) {
        List<Node> nodes = new ArrayList<>();
        Arrays.asList(listFiles).forEach(f->{
            if (validFile(f)) {
                this.files.put(f.getName(),f);
                nodes.add(loadResultItemView(f));
                processingResults.addFile(f);
            }
        });
        resultsPane.getChildren().addAll(nodes);
    }

    private boolean validFile(File file) {
        if (file.isDirectory())
            return false;
        String[] a = file.getName().split("\\.");
        if (a.length == 1)
            return false;
        String extension = a[1];
        return !file.getName().contains("tmp") && (extension.equals("tif") || extension.equals("PNG")) && !isLoadedInFlowPane(file.getName());

    }

    private synchronized void removeFile(File file) {
        if (isLoadedInFlowPane(file.getName())) {
            Node node = getChildren(file.getName());
            this.files.remove(file.getName());
            processingResults.removeFile(file);
            if (node != null)
                Platform.runLater(()->resultsPane.getChildren().remove(node));
        }
    }

    private synchronized void loadFile(File file) {
        if (validFile(file)) {
            this.files.put(file.getName(),file);
            processingResults.addFile(file);
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

    @Override
    public ProcessingResults getProcessingResults() {
        return processingResults;
    }
}
