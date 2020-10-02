package controller.list;

import com.jfoenix.controls.JFXListView;
import controller.GTMapSearchController;
import controller.TabItem;
import controller.cell.ProductListCell;
import gui.GTMap;
import gui.components.TabPaneComponent;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import model.ProductList;
import model.exception.AuthenticationException;
import model.exception.NotAuthenticatedException;
import model.products.Product;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.swing.control.DnDListItemsTransferable;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import services.CopernicusService;
import utils.WKTUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ListController implements TabItem, ListItem {
    private final FXMLLoader loader;
    private Parent parent;
    private ProductList productList;

    @FXML
    private ImageView image;
    @FXML
    private Label title;
    @FXML
    private Label description;
    @FXML
    private JFXListView<Product> productListView;
    @FXML
    private Label numberOfProducts;
    @FXML
    private Label size;
    @FXML
    private AnchorPane mapPane;
    @FXML
    private Button workingAreaToAll;
    private GTMapSearchController gtMapSearchController;
    private TabPaneComponent setTabPaneComponent;

    public ListController(ProductList productList) {
        this.productList = productList;
        loader = new FXMLLoader(getClass().getResource("/fxml/ListView.fxml"));
        loader.setController(this);
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setMap() {
        gtMapSearchController = new GTMapSearchController(458.0,435.0,false);
        gtMapSearchController.disableMouseClickedEvent();
        mapPane.getChildren().add(gtMapSearchController.getView());
        gtMapSearchController.addProductsWKT(productList.getProducts());
    }

    @Override
    public void setTabPaneComponent(TabPaneComponent component) {
        this.setTabPaneComponent = component;
    }

    @Override
    public Parent getView() {
        return parent;
    }

    @Override
    public Task<Parent> start() {
        return new Task<>() {
            @Override
            protected Parent call() throws Exception {
                initData();
                try {
                    setMap();
                    if (productList.count() > 0) {
                        InputStream preview = CopernicusService.getInstance().getPreview(productList.getProducts().get(0).getId());
                        image.setImage(new Image(preview));
                    } else {
                        image.setImage(new Image("/img/no_photo.jpg"));
                    }

                } catch (IOException | AuthenticationException e) {
                    e.printStackTrace();
                }
                return getView();
            }
        };
    }

    private void initData() {
        numberOfProducts.textProperty().bind(productList.countProperty().asString());
        size.textProperty().bind(Bindings.format("%.2f",productList.sizeAsDoubleProperty()).concat(" GB"));
        productListView.setItems(productList.getProducts());
        productListView.setCellFactory(e -> new ProductListCell(productList));
        productListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        title.setText(productList.getName());
        description.setText(productList.getDescription());
        productList.getProducts().addListener((ListChangeListener<Product>) c -> {
            gtMapSearchController.clearMap();
            gtMapSearchController.addProductsWKT(productList.getProducts());

        });

        addSelectionModeListener();


        workingAreaToAll.setOnAction(event -> {
            System.out.println(gtMapSearchController.getWKT());
            if (!gtMapSearchController.getWKT().isEmpty())
                productList.setDefaultAreaOfWork(gtMapSearchController.getWKT());
        });
    }



    private void addSelectionModeListener() {
        productListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            Task<InputStream> task = new Task<>() {
                @Override
                protected InputStream call() throws Exception {
                    setTabPaneComponent.getMainController().showWaitSpinner();
                    return CopernicusService.getInstance().getPreview(newValue.getId());
                }
            };
            task.setOnSucceeded(previewImageLoaded(task));
            task.setOnFailed(loadDefaultImage());
            new Thread(task).start();

        });
    }

    private EventHandler<WorkerStateEvent> loadDefaultImage() {
        return event -> {
            image.setImage(new Image("/img/no_photo.jpg"));
            setTabPaneComponent.getMainController().hideWaitSpinner();
        };
    }

    private EventHandler<WorkerStateEvent> previewImageLoaded(Task<InputStream> task) {
        return event -> {
            try {
                image.setImage(new Image(task.get()));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            setTabPaneComponent.getMainController().hideWaitSpinner();
        };
    }

    @Override
    public ProductList getProductList() {
        return productList;
    }

    @Override
    public ObservableList<Product> getSelectedProducts() {
        return productListView.getSelectionModel().getSelectedItems();
    }

    @Override
    public String getName() {
        return productList.getName();
    }

}
