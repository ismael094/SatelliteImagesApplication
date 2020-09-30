package controller.list;

import com.jfoenix.controls.JFXListView;
import controller.TabItem;
import controller.cell.ProductListCell;
import gui.GTMap;
import gui.components.TabPaneComponent;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import model.ProductList;
import model.exception.AuthenticationException;
import model.exception.NotAuthenticatedException;
import model.products.Product;
import org.locationtech.jts.io.ParseException;
import services.CopernicusService;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ListController implements TabItem {
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
    private GTMap gtMap;
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

    private void setMap() throws ParseException {
        gtMap = new GTMap(458, 435,false);
        mapPane.getChildren().add(gtMap);
        gtMap.createFeatureFromWKT(productList.getProducts().get(0).getFootprint(),productList.getProducts().get(0).getId());
        gtMap.createAndDrawProductsLayer();
        gtMap.goToSelection();
        gtMap.scroll(-96);
        gtMap.refresh();
        mapPane.addEventHandler(ScrollEvent.SCROLL, e-> {
            gtMap.scroll(e.getDeltaY());
            e.consume();
        });
    }

    public URL getQuicklook(String id) throws MalformedURLException {
        String url = "https://scihub.copernicus.eu/dhus/odata/v1/Products('"+id+"')/Products('Quicklook')/$value";
        return new URL(url);
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
                    }

                } catch (ParseException | IOException | AuthenticationException e) {
                    e.printStackTrace();
                }
                return getView();
            }
        };
    }

    private void initData() {
        productListView.setItems(productList.getProducts());
        productListView.setCellFactory(e -> new ProductListCell());
        title.setText(productList.getName());
        description.setText(productList.getDescription());
        productListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            Platform.runLater(() -> {
                try {
                    image.setImage(new Image(CopernicusService.getInstance().getPreview(newValue.getId())));
                } catch (IOException | AuthenticationException | NotAuthenticatedException e) {
                    e.printStackTrace();
                }
            });

        });
        productListView.getItems().addListener((ListChangeListener<Product>) c -> {
            if (c.wasAdded())
                System.out.println(c.getList().size());
            /*gtMap.createFeatureFromWKT(c.getAddedSubList().get(0).getFootprint(), c.getAddedSubList().get(0).getId());
            gtMap.createAndDrawProductsLayer();
            gtMap.refresh();*/
        });
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

}
