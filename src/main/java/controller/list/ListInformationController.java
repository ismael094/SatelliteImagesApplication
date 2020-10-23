package controller.list;

import com.google.common.collect.Lists;
import com.jfoenix.controls.JFXListView;
import controller.GTMapSearchController;
import controller.cell.ProductListCell;
import controller.interfaces.ProductListTabItem;
import controller.processing.PreviewImageController;
import controller.processing.ProcessingPreviewController;
import controller.search.CopernicusOpenSearchController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.TabPaneComponent;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jfxtras.styles.jmetro.Style;
import model.list.ProductListDTO;
import model.exception.AuthenticationException;
import model.openSearcher.SentinelProductParameters;
import model.processing.workflow.WorkflowType;
import model.products.ProductDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.ToggleSwitch;
import org.locationtech.jts.io.ParseException;
import services.CopernicusService;
import services.download.Downloader;
import services.entities.Product;
import services.entities.Workflow;
import utils.AlertFactory;
import utils.ThemeConfiguration;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ListInformationController extends ProductListTabItem {
    public static final String REFERENCE_IMAGES = "groundTruth";
    public static final String DEFAULT_IMAGE = "/img/no_photo.jpg";
    public static final String AREA_OF_WORK_LAYER = "default";
    public static final String PRODUCTS_LAYER = "products";
    public static final String COPERNICUS_OPEN_SEARCH_ID = "Copernicus Open Search";
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
    private ToggleSwitch selectReferenceImage;
    @FXML
    private AnchorPane multimediaPane;
    @FXML
    private Button deleteFeature;

    @FXML
    private Button addAreaOfProduct;
    @FXML
    private Button searchGroundTruth;
    @FXML
    private Button makePreview;

    private String idSelected;

    static final Logger logger = LogManager.getLogger(ListInformationController.class.getName());


    public ListInformationController(ProductListDTO productList, Downloader download) {
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

    /**
     * Inject TabPaneComponent
     * @param component TabPaneComponent
     */
    @Override
    public void setTabPaneComponent(TabPaneComponent component) {
        this.tabPaneComponent = component;
    }

    /**
     * Get the parent view linked to controller
     * @return Parent node
     */
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

        return new Task<Parent>() {
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
                        loadImage(new Image(DEFAULT_IMAGE));
                } catch (IOException | AuthenticationException e) {
                    loadImage(new Image(DEFAULT_IMAGE));
                }
                return getView();
            }
        };
    }

    /**
     * Get the product list of the controller
     * @return product list
     */
    @Override
    public ProductListDTO getProductList() {
        return productListDTO;
    }

    /**
     * Get the selected products in the view in an observable collection
     * @return products in an observable collection
     */
    @Override
    public ObservableList<ProductDTO> getSelectedProducts() {
        return productListView.getSelectionModel().getSelectedItems();
    }

    /**
     * Select product list in the list view
     * @param products List of products to select in the view
     */
    @Override
    public void setSelectedProducts(ObservableList<ProductDTO> products) {
        productListView.setFocusTraversable(true);
        productListView.getFocusModel().focus(productListView.getItems().indexOf(products.get(0)));
        productListView.getSelectionModel().select(productListView.getItems().indexOf(products.get(0)));
        productListView.scrollTo(products.get(0));
    }

    /**
     * Refresh the products list. In this case, refresh listView
     */
    @Override
    public void refreshProducts() {
        productListView.refresh();
    }

    /**
     * Get the name of this TabItem
     * @return get the name of the TabItem
     */
    @Override
    public String getName() {
        return productListDTO.getName();
    }

    /**
     * Get the name of this TabItem
     * @return get the id of the TabItem
     */
    @Override
    public String getItemId() {
        return productListDTO.getId().toString();
    }

    /**
     * Redo implementations
     */
    @Override
    public void undo() {

    }

    /**
     * Undo implementations
     */
    @Override
    public void redo() {

    }


    private void initData() {

        bindProperties();

        initProductDTOListView();

        onProductListChangeRepaintMap();

        onProductSelectedLoadPreviewImage();

        onAreaOfWorkChangeRefreshListView();

        onActionOnSearchGroundTruthLoadOpenSearcher();

        onGroundTruthChangeRefreshMap();

        initAddWorkingAreaButton();

        initMapController();

        onChangeToggleAction();

        onDeleteFeatureActionRemoveFeature();

        addAreaOfProduct.toFront();
        selectReferenceImage.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        makePreview.setOnAction(e->this.processImage());
    }

    private void onDeleteFeatureActionRemoveFeature() {
        GlyphsDude.setIcon(deleteFeature, MaterialDesignIcon.ERASER);
        deleteFeature.toFront();
        deleteFeature.setOnAction(e->{
            if (selectReferenceImage.isSelected()) {
                ProductDTO productDTO = productListDTO.getGroundTruthProducts().stream()
                        .filter(p -> p.getId().equals(mapController.getSelectedFeatureId()))
                        .findAny()
                        .orElse(null);
                productListDTO.removeGroundTruth(productDTO);
            } else {
                productListDTO.removeAreaOfWork(productListDTO.getAreasOfWork().get(Integer.parseInt(mapController.getSelectedFeatureId())));
                drawInMapTheAreasOfWork();
            }
        });
    }

    private void onChangeToggleAction() {
        selectReferenceImage.setOnMouseClicked(e->{
            if (selectReferenceImage.isSelected()) {
                mapController.setLayerSelectedAreaEvent(REFERENCE_IMAGES);
                mapController.setNotSelectedFeaturesBorderColor(Color.decode("#00976C"), Color.decode("#00976C"));
                mapController.setSelectedFeaturesBorderColor(Color.GREEN, Color.GREEN);
            } else {
                mapController.setLayerSelectedAreaEvent(AREA_OF_WORK_LAYER);
                mapController.setSelectedFeaturesBorderColor(Color.MAGENTA, null);
                mapController.setNotSelectedFeaturesBorderColor(Color.ORANGE, null);
            }
        });
    }

    private void onGroundTruthChangeRefreshMap() {
        productListDTO.getGroundTruthProducts().addListener((ListChangeListener<ProductDTO>) c -> mapController.printProductsInLayer("groundTruth", productListDTO.getGroundTruthProducts(), Color.GREEN, Color.decode("#C3FFE9")));
    }

    private void onActionOnSearchGroundTruthLoadOpenSearcher() {
        searchGroundTruth.setOnAction(e->{
            //If there are not areas of work, or there are not selected, return
            if (mapController.getSelectedFeatureId() == null || mapController.getSelectedFeatureId().isEmpty()) {
                AlertFactory.showErrorDialog("No area of work selected","","Select an area of work!");
                return;
            }

            //Open new CopernicusOpenSearchController if it is not loaded
            CopernicusOpenSearchController searchController;
            if (tabPaneComponent.isLoaded(COPERNICUS_OPEN_SEARCH_ID)) {
                tabPaneComponent.select(COPERNICUS_OPEN_SEARCH_ID);
                searchController = (CopernicusOpenSearchController) tabPaneComponent.getControllerOf(COPERNICUS_OPEN_SEARCH_ID);
            } else {
                searchController = new CopernicusOpenSearchController("id");
                tabPaneComponent.load(searchController);
            }

            //Set parameters of ground truth
            Platform.runLater(()-> searchController.setParametersOfAllResponses(getGroundTruthParameters()));
        });
    }

    private Map<String, String> getGroundTruthParameters() {
        Map<String, String> data = new HashMap<>();
        data.put(SentinelProductParameters.FOOTPRINT.getParameterName(), productListDTO.getAreasOfWork().get(Integer.parseInt(mapController.getSelectedFeatureId())));
        data.put(SentinelProductParameters.PLATFORM_NAME.getParameterName(), "Sentinel-2");
        data.put(SentinelProductParameters.CLOUD_COVER_PERCENTAGE.getParameterName()+"_from", "0");
        data.put(SentinelProductParameters.CLOUD_COVER_PERCENTAGE.getParameterName()+"_to", "10");
        return data;
    }

    private void initMapController() {

        mapController = new GTMapSearchController(mapPane.getPrefWidth(),mapPane.getPrefHeight(),true);
        mapController.addSelectedAreaEvent(AREA_OF_WORK_LAYER);
        mapController.setSelectedFeaturesBorderColor(Color.MAGENTA, null);
        mapController.setNotSelectedFeaturesBorderColor(Color.ORANGE, null);
        mapPane.getChildren().add(mapController.getView());
        AnchorPane.setBottomAnchor(mapController.getView(),0.0);
        AnchorPane.setTopAnchor(mapController.getView(),0.0);
        AnchorPane.setLeftAnchor(mapController.getView(),0.0);
        AnchorPane.setRightAnchor(mapController.getView(),0.0);
        mapController.printProductsInMap(productListDTO.getProducts(),Color.BLACK, null);
        if (!productListDTO.getGroundTruthProducts().isEmpty())
            mapController.printProductsInLayer(REFERENCE_IMAGES,productListDTO.getGroundTruthProducts(),Color.decode("#00976C"), Color.decode("#00976C"));

        drawInMapTheAreasOfWork();
        selectReferenceImage.toFront();
        //mapController.focusOnLayer("default");
    }

    private void onProductListChangeRepaintMap() {
        productListDTO.getProducts().addListener((ListChangeListener<ProductDTO>) c -> {
            mapController.clearMap(PRODUCTS_LAYER);
            mapController.printProductsInMap(productListDTO.getProducts(), Color.BLACK, null);

        });
    }

    private void initAddWorkingAreaButton() {
        Tooltip.install(addAreaOfProduct,new Tooltip("Add area of work"));
        GlyphsDude.setIcon(addAreaOfProduct, MaterialDesignIcon.BOOKMARK_PLUS);
        addAreaOfProduct.setOnAction(event -> {
            if (!mapController.getWKT().isEmpty()) {
                productListDTO.addAreaOfWork(mapController.getWKT());
                drawInMapTheAreasOfWork();
            }
        });
    }

    private void initProductDTOListView() {
        productListView.setItems(productListDTO.getProducts());
        productListView.setCellFactory(e -> new ProductListCell(productListDTO, mapController));
        productListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void bindProperties() {
        numberOfProducts.textProperty().bind(productListDTO.countProperty().asString());
        size.textProperty().bind(Bindings.format("%.2f", productListDTO.sizeAsDoubleProperty()).concat(" GB"));
        title.textProperty().bind(productListDTO.nameProperty());
        description.textProperty().bind(productListDTO.descriptionProperty());
        image.fitWidthProperty().bind(multimediaPane.widthProperty().subtract(8));
        image.fitHeightProperty().bind(multimediaPane.heightProperty().subtract(8));
    }

    private void drawInMapTheAreasOfWork() {
        mapController.clearMap(AREA_OF_WORK_LAYER);
        productListDTO.getAreasOfWork().forEach(a->{
            try {
                mapController.addProductWKT(a, productListDTO.getAreasOfWork().indexOf(a)+"","default");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        mapController.drawFeaturesOfLayer(AREA_OF_WORK_LAYER,Color.ORANGE, null);
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
            Task<InputStream> task = new Task<InputStream>() {
                @Override
                protected InputStream call() throws Exception {
                    tabPaneComponent.getMainController().showWaitSpinner();
                    return CopernicusService.getInstance().getContentFromURL(newValue.getPreviewURL());
                }
            };
            task.setOnSucceeded(e-> {
                try {
                    loadPreviewImage(task.get());
                    task.get().close();
                } catch (Exception ioException) {
                    ioException.printStackTrace();
                }
            });

            task.setOnFailed(e->loadDefaultImage());
            new Thread(task).start();
        });
    }

    private void  loadDefaultImage() {
        loadImage(new Image(DEFAULT_IMAGE));
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

    private void processImage() {
        try {
            ProductDTO selectedItem = productListView.getSelectionModel().getSelectedItem();
            if (!selectReferenceImage.isSelected() && selectedItem != null) {
                List<String> strings = productListDTO.areasOfWorkOfProduct(selectedItem.getFootprint());

                if (mapController.getSelectedFeatureId() != null && strings.contains(productListDTO.getAreasOfWork().get(Integer.parseInt(mapController.getSelectedFeatureId())))) {
                    process(selectedItem, Lists.newArrayList(productListDTO.getAreasOfWork().get(Integer.parseInt(mapController.getSelectedFeatureId()))));
                } else {
                    AlertFactory.showErrorDialog("","","Error");
                }
            } else {
                AlertFactory.showErrorDialog("","","Error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void process(ProductDTO productDTO, List<String> areas) {
        Task<WritableImage> task = new Task<WritableImage>() {
            @Override
            protected WritableImage call() throws Exception {
                BufferedImage preview = tabPaneComponent.getMainController().getProcessor().process(productDTO, areas, productListDTO.getWorkflow(WorkflowType.valueOf(productDTO.getProductType())),productListDTO.getName(), true);
                return SwingFXUtils.toFXImage(preview, null);
            }
        };
        task.setOnSucceeded(e-> {
            try {
                loadView(task.get());
            } catch (InterruptedException | ExecutionException interruptedException) {
                interruptedException.printStackTrace();
            }
        });
        new Thread(task).start();
    }

    private void loadView(WritableImage image) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PreviewImageView.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        JMetro jMetro = ThemeConfiguration.getJMetroStyled();

        PreviewImageController controller = fxmlLoader.getController();
        controller.setImage(image);

        Stage stage = new Stage();
        stage.initOwner(tabPaneComponent.getMainController().getRoot().getScene().getWindow());
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);
        jMetro.setScene(scene);
        stage.show();
    }
}
