package controller.list;

import com.jfoenix.controls.JFXListView;
import controller.GTMapSearchController;
import controller.cell.ProductListCell;
import controller.interfaces.ProductTabItem;
import controller.search.CopernicusOpenSearchController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gui.components.TabPaneComponent;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import model.list.ProductListDTO;
import model.exception.AuthenticationException;
import model.openSearcher.SentinelProductParameters;
import model.products.ProductDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.ToggleSwitch;
import org.locationtech.jts.io.ParseException;
import services.CopernicusService;
import services.download.DownloadManager;
import utils.AlertFactory;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

public class ListInformationController extends ProductTabItem {
    private final FXMLLoader loader;
    private Parent parent;
    private final ProductListDTO productListDTO;
    private GTMapSearchController mapController;
    private TabPaneComponent tabPaneComponent;

    @FXML
    private ImageView image;
    @FXML
    private Label title;
    @FXML
    private Label description;
    @FXML
    private JFXListView<ProductDTO> productListView;
    @FXML
    private Label numberOfProducts;
    @FXML
    private Label size;
    @FXML
    private AnchorPane mapPane;
    @FXML
    private Button addAreaOfProduct;
    @FXML
    private Button deleteSelectedArea;
    @FXML
    private Button searchGroundTruth;
    @FXML
    private Button deleteGroundTruth;
    @FXML
    private ToggleSwitch selectGroundTruth;

    private String idSelected;

    static final Logger logger = LogManager.getLogger(ListInformationController.class.getName());


    public ListInformationController(ProductListDTO productList, DownloadManager download) {
        super(download);
        idSelected = "";
        this.productListDTO = productList;
        this.loader = new FXMLLoader(getClass().getResource("/fxml/ListView.fxml"));
        this.loader.setController(this);
        try {
            parent = this.loader.load();
        } catch (IOException e) {
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

    /**
     * Start controller. Init data, if user not logged in copernicus, login. Load preview of first image
     * @return new Task with view node
     */
    @Override
    public Task<Parent> start() {
        CopernicusService service = CopernicusService.getInstance();

        return new Task<>() {
            @Override
            protected Parent call() throws Exception {
                service.login();
                initData();
                try {
                    if (productListDTO.count() > 0) {
                        idSelected = productListDTO.getProducts().get(0).getId();
                        InputStream preview = CopernicusService.getInstance().getContentFromURL(productListDTO.getProducts().get(0).getPreviewURL());
                        loadImage(new Image(preview));
                    } else
                        loadImage(new Image("/img/no_photo.jpg"));
                } catch (IOException | AuthenticationException e) {
                    loadImage(new Image("/img/no_photo.jpg"));
                }
                return getView();
            }
        };
    }

    @Override
    public ProductListDTO getProductList() {
        return productListDTO;
    }

    @Override
    public ObservableList<ProductDTO> getSelectedProducts() {
        return productListView.getSelectionModel().getSelectedItems();
    }

    @Override
    public void setSelectedProducts(ObservableList<ProductDTO> products) {
        productListView.getSelectionModel().clearSelection();
        productListView.getSelectionModel().select(products.get(0));
        productListView.getFocusModel().focus(productListView.getItems().indexOf(products.get(0)));
    }

    @Override
    public void refreshProducts() {
        productListView.refresh();
    }

    @Override
    public String getName() {
        return productListDTO.getName();
    }

    @Override
    public String getItemId() {
        return productListDTO.getId().toString();
    }

    private void initData() {
        bindProperties();

        initListView();

        onProductListChangeRepaintMap();

        onProductSelectedLoadPreviewImage();

        onAreaOfWorkChangeRefreshListView();

        onActionOnSearchGroundTruthLoadOpenSearcher();

        onGroundTruthChangeRefreshMap();

        initAddWorkingAreaButton();

        initDeleteSelectedAreaOfWorkButton();

        initMapController();

        onDeleteGroundTruthButtonAction();

        onChangeToggleAction();
    }

    private void onChangeToggleAction() {
        selectGroundTruth.setOnMouseClicked(e->{
            if (selectGroundTruth.isSelected()) {
                mapController.setLayerSelectedAreaEvent("groundTruth");
                mapController.setSelectedFeaturesBorderColor(Color.decode("#00976C"), Color.decode("#ACDACD"));
                mapController.setNotSelectedFeaturesBorderColor(Color.GREEN, null);
            } else {
                mapController.setLayerSelectedAreaEvent("default");
                mapController.setSelectedFeaturesBorderColor(Color.MAGENTA, null);
                mapController.setNotSelectedFeaturesBorderColor(Color.ORANGE, null);
            }
        });
    }

    private void onDeleteGroundTruthButtonAction() {
        GlyphsDude.setIcon(deleteGroundTruth, FontAwesomeIcon.ERASER);
        deleteGroundTruth.setOnAction(e->{
            ProductDTO productDTO = productListDTO.getGroundTruthProducts().stream()
                    .filter(p -> p.getId().equals(mapController.getSelectedProduct()))
                    .findAny()
                    .orElse(null);
            productListDTO.removeGroundTruth(productDTO);
         });
    }

    private void onGroundTruthChangeRefreshMap() {
        productListDTO.getGroundTruthProducts().addListener((ListChangeListener<ProductDTO>) c -> {
            mapController.printProductsInLayer("groundTruth", productListDTO.getGroundTruthProducts(), Color.GREEN, Color.decode("#C3FFE9"));
        });
    }

    private void onActionOnSearchGroundTruthLoadOpenSearcher() {
        searchGroundTruth.setOnAction(e->{
            if (mapController.getSelectedProduct() == null || mapController.getSelectedProduct().isEmpty()) {
                AlertFactory.showErrorDialog("No area of work selected","","Select an area of work!");
                return;
            }

            CopernicusOpenSearchController oSearch;
            if (tabPaneComponent.isLoaded("Copernicus Open Search")) {
                tabPaneComponent.select("Copernicus Open Search");
                oSearch = (CopernicusOpenSearchController) tabPaneComponent.getControllerOf("Copernicus Open Search");
            } else {
                oSearch = new CopernicusOpenSearchController("id");
                tabPaneComponent.load(oSearch);
            }

            System.out.println(mapController.getSelectedProduct());
            Map<String, String> data = new HashMap<>();
            data.put(SentinelProductParameters.FOOTPRINT.getParameterName(), productListDTO.getAreasOfWork().get(Integer.parseInt(mapController.getSelectedProduct())));
            data.put(SentinelProductParameters.PLATFORM_NAME.getParameterName(), "Sentinel-2");
            data.put(SentinelProductParameters.CLOUD_COVER_PERCENTAGE.getParameterName()+"_from", "0");
            data.put(SentinelProductParameters.CLOUD_COVER_PERCENTAGE.getParameterName()+"_to", "10");

            Platform.runLater(()->{
                oSearch.setParameters(data);
            });
        });
    }

    private void initMapController() {
        mapController = new GTMapSearchController(458.0,435.0,true);
        mapController.addSelectedAreaEvent("default");
        mapController.setSelectedFeaturesBorderColor(Color.MAGENTA, null);
        mapController.setNotSelectedFeaturesBorderColor(Color.ORANGE, null);
        mapPane.getChildren().add(mapController.getView());
        mapController.printProductsInMap(productListDTO.getProducts(),Color.BLACK, null);
        if (productListDTO.getGroundTruthProducts().size() > 0)
            mapController.printProductsInLayer("groundTruth",productListDTO.getGroundTruthProducts(),Color.GREEN, Color.GREEN);

        printAreasOfWork();
        //mapController.focusOnLayer("default");
    }

    private void onProductListChangeRepaintMap() {
        productListDTO.getProducts().addListener((ListChangeListener<ProductDTO>) c -> {
            mapController.clearMap("products");
            mapController.printProductsInMap(productListDTO.getProducts(), Color.BLACK, null);

        });
    }

    private void initDeleteSelectedAreaOfWorkButton() {
        GlyphsDude.setIcon(deleteSelectedArea, FontAwesomeIcon.ERASER);
        deleteSelectedArea.setOnAction(e->{
            productListDTO.removeAreaOfWork(productListDTO.getAreasOfWork().get(Integer.parseInt(mapController.getSelectedProduct())));
            printAreasOfWork();
        });
    }

    private void initAddWorkingAreaButton() {
        GlyphsDude.setIcon(addAreaOfProduct, FontAwesomeIcon.LOCATION_ARROW);
        addAreaOfProduct.setOnAction(event -> {
            if (!mapController.getWKT().isEmpty()) {
                productListDTO.addAreaOfWork(mapController.getWKT());
                printAreasOfWork();
            }
        });
    }

    private void initListView() {
        productListView.setItems(productListDTO.getProducts());
        productListView.setCellFactory(e -> new ProductListCell(productListDTO, mapController));
        productListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void bindProperties() {
        numberOfProducts.textProperty().bind(productListDTO.countProperty().asString());
        size.textProperty().bind(Bindings.format("%.2f", productListDTO.sizeAsDoubleProperty()).concat(" GB"));
        title.textProperty().bind(productListDTO.nameProperty());
        description.textProperty().bind(productListDTO.descriptionProperty());
        searchGroundTruth.disableProperty().bind(Bindings.isEmpty(productListDTO.getAreasOfWork()));
        deleteGroundTruth.disableProperty().bind(selectGroundTruth.selectedProperty().not());
    }

    private void printAreasOfWork() {
        mapController.clearMap("default");
        productListDTO.getAreasOfWork().forEach(a->{
            try {
                mapController.addProductWKT(a, productListDTO.getAreasOfWork().indexOf(a)+"","default");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        mapController.drawFeaturesOfLayer("default",Color.ORANGE, null);
    }

    private void onAreaOfWorkChangeRefreshListView() {
        productListDTO.getAreasOfWork().addListener((ListChangeListener<String>) c -> {
            productListView.applyCss();
            productListView.refresh();
            productListView.applyCss();
            productListView.refresh();
        });

    }

    private void onProductSelectedLoadPreviewImage() {
        productListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            if (newValue==null)
                return;

            if (idSelected.equals(newValue.getId()))
                return;
            idSelected = newValue.getId();
            Task<InputStream> task = new Task<>() {
                @Override
                protected InputStream call() throws Exception {
                    tabPaneComponent.getMainController().showWaitSpinner();
                    return CopernicusService.getInstance().getContentFromURL(newValue.getPreviewURL());
                }
            };
            task.setOnSucceeded(e-> {
                try {
                    loadPreviewImage(task.get());
                } catch (Exception ioException) {
                    ioException.printStackTrace();
                }
            });


            task.setOnFailed(e->loadDefaultImage());
            new Thread(task).start();
        });
    }

    private void  loadDefaultImage() {
        loadImage(new Image("/img/no_photo.jpg"));
    }

    private void loadPreviewImage(InputStream inputStream) throws IOException {
        loadImage(new Image(inputStream));
        inputStream.close();
    }

    private void loadImage(Image imageResource) {
        image.setImage(imageResource);
        if (tabPaneComponent != null)
            tabPaneComponent.getMainController().hideWaitSpinner();
    }
}
