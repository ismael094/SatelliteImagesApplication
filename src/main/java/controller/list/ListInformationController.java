package controller.list;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import controller.GTMapController;
import controller.cell.ProductListCell;
import controller.interfaces.ProductListTabItem;
import controller.processing.preview.PreviewController;
import controller.search.CopernicusOpenSearchController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.TabPaneComponent;
import gui.components.listener.ComponentEvent;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
import jfxtras.styles.jmetro.JMetroStyleClass;
import model.list.ProductListDTO;
import model.openSearcher.SentinelProductParameters;
import model.preprocessing.workflow.WorkflowType;
import model.products.ProductDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.ToggleSwitch;
import org.locationtech.jts.io.ParseException;
import services.database.ProductListDBDAO;
import services.download.Downloader;
import services.search.OpenSearcher;
import utils.AlertFactory;
import utils.FileUtils;
import utils.ProcessingConfiguration;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Main ListController to show all the productList information
 */
public class ListInformationController extends ProductListTabItem {
    public static final String REFERENCE_IMAGES = "referenceImages";
    public static final String AREA_OF_WORK_LAYER = "default";
    public static final String PRODUCTS_LAYER = "products";
    public static final String COPERNICUS_OPEN_SEARCH_ID = "Copernicus Open Search";
    private final FXMLLoader loader;
    private Parent parent;
    private final ProductListDTO productListDTO;
    private GTMapController mapController;
    private TabPaneComponent tabPaneComponent;
    private List<Pair<ListAction,ObservableList<?>>> actions;
    private int actionIndex = 0;
    private boolean actionActive;

    @FXML
    private Label title;
    @FXML
    private Label description;
    @FXML
    private JFXListView<ProductDTO> productListView;
    @FXML
    private JFXListView<ProductDTO> referenceImgsList;
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
    private Button showRI;
    @FXML
    private Button makePreview;
    @FXML
    private JFXSpinner imageSpinner;
    @FXML
    private HBox buttonPanel;

    private String idSelected;

    static final Logger logger = LogManager.getLogger(ListInformationController.class.getName());


    public ListInformationController(ProductListDTO productList, Downloader download) {
        super(download);
        idSelected = "";
        this.productListDTO = productList;
        this.actions = new ArrayList<>();
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
        return new Task<Parent>() {
            @Override
            protected Parent call() throws Exception {
                initData();
                if (productListDTO.count() > 0) {
                    idSelected = productListDTO.getProducts().get(0).getId();
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
        productListView.getSelectionModel().clearSelection();
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
        referenceImgsList.refresh();
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
        --actionIndex;
        actionActive(true);
        Pair<ListAction, ObservableList<?>> pair = actions.get(actionIndex);
        if (pair.getKey() == ListAction.ADD_AREA_OF_WORK) {
            deleteAreaOfWork(pair);
        } else if (pair.getKey() == ListAction.DELETE_AREA_OF_WORK) {
            addAreaOfWork(pair);
        } else if (pair.getKey() == ListAction.ADD_REFERENCE_IMAGE) {
            deleteReferenceImage(pair);
        }else if (pair.getKey() == ListAction.DELETE_REFERENCE_IMAGE) {
            addReferenceImage(pair);
        }else if (pair.getKey() == ListAction.ADD_PRODUCT) {
            productListDTO.remove((ObservableList<ProductDTO>) pair.getValue());
        }else if (pair.getKey() == ListAction.DELETE_PRODUCT) {
            productListDTO.addProduct((ObservableList<ProductDTO>) pair.getValue());
        }

        saveProductList();

        actionActive(false);
        tabPaneComponent.updateObservers(this);
    }

    private void saveProductList() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                ProductListDBDAO.getInstance().save(productListDTO);
                return null;
            }
        };
        new Thread(task).start();
    }

    private void actionActive(boolean b) {
        actionActive = b;
    }

    /**
     * Undo implementations
     */
    @Override
    public void redo() {
        actionActive(true);
        Pair<ListAction, ObservableList<?>> pair = actions.get(actionIndex++);
        if (pair.getKey() == ListAction.ADD_AREA_OF_WORK) {
            addAreaOfWork(pair);
        } else if (pair.getKey() == ListAction.DELETE_AREA_OF_WORK) {
            deleteAreaOfWork(pair);
        } else if (pair.getKey() == ListAction.ADD_REFERENCE_IMAGE) {
            addReferenceImage(pair);
        } else if (pair.getKey() == ListAction.DELETE_REFERENCE_IMAGE) {
            deleteReferenceImage(pair);
        } else if (pair.getKey() == ListAction.ADD_PRODUCT) {
            productListDTO.addProduct((ObservableList<ProductDTO>) pair.getValue());
        } else if (pair.getKey() == ListAction.DELETE_PRODUCT) {
            productListDTO.remove((ObservableList<ProductDTO>) pair.getValue());
        }

        saveProductList();

        actionActive(false);
        tabPaneComponent.updateObservers(this);
    }



    @Override
    public String getRedo() {
        try {
            return actions.get(actionIndex).getKey().getName();
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public String getUndo() {
        try {
            return actions.get(actionIndex-1).getKey().getName();
        }catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    //UNDO AND REDO OPERATIONS

    private void deleteReferenceImage(Pair<ListAction, ObservableList<?>> pair) {
        productListDTO.removeReferenceProduct((ProductDTO) pair.getValue().get(0));
        refreshLayer(REFERENCE_IMAGES, Color.GREEN, "#C3FFE9");
        saveProductList();
    }

    private void addReferenceImage(Pair<ListAction, ObservableList<?>> pair) {
        productListDTO.addReferenceProduct(Collections.singletonList((ProductDTO) pair.getValue().get(0)));
        refreshLayer(REFERENCE_IMAGES, Color.GREEN, "#C3FFE9");
        saveProductList();
    }

    private void deleteAreaOfWork(Pair<ListAction, ObservableList<?>> pair) {
        productListDTO.removeAreaOfWork((String) pair.getValue().get(0));
        drawInMapTheAreasOfWork();
        saveProductList();
    }

    private void addAreaOfWork(Pair<ListAction, ObservableList<?>> pair) {
        productListDTO.addAreaOfWork((String) pair.getValue().get(0));
        drawInMapTheAreasOfWork();
        saveProductList();
    }

    private void addAction(ListAction work, ObservableList<?> list) {
        actions.add(actionIndex,new Pair<>(work,list));
        actionIndex++;
        for (int i = actionIndex;i<actions.size();i++)
            actions.remove(i);
        tabPaneComponent.updateObservers(this);
    }


    /**
     * Init all components and events
     */
    private void initData() {

        bindProperties();

        initProductDTOListView();
        initReferenceImagesListView();

        onReferenceListCellSelectLoadPreviewImage();
        onShowReferenceImageSetVisibleReferenceImagesList();

        onProductListChangeRepaintMap();

        onProductSelectedLoadPreviewImage();

        onAreaOfWorkChangeRefreshListView();

        onActionOnSearchReferenceImageLoadOpenSearcher();

        onReferenceImagesChangeRefreshMap();

        initAddAreaOfWorkButton();

        initMapController();

        onChangeReferenceImageToggleChangeMapMode();

        onDeleteFeatureActionRemoveFeature();

        onActionInAddAreaOfWorkCreateAreaOfWork();

        onActionInPreviewGeneratePreview();

        //Controls to generate preview, add area of work, delete, etc
        initButtonPanel();

        //REDO AND UNDO
        onProductListChangeSaveChange();
        onReferenceImagesChangeSaveChange();
    }

    private void initButtonPanel() {
        buttonPanel.toFront();
        selectReferenceImage.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        setReferenceListVisible(false);
        GlyphsDude.setIcon(showRI, FontAwesomeIcon.IMAGE);
        GlyphsDude.setIcon(makePreview, FontAwesomeIcon.ROCKET);
        setTooltip("Make preview", makePreview);
        GlyphsDude.setIcon(searchGroundTruth, FontAwesomeIcon.SEARCH);
        setTooltip("Make preview", searchGroundTruth);
        GlyphsDude.setIcon(deleteFeature, MaterialDesignIcon.ERASER);
        setTooltip("Delete", deleteFeature);
    }

    private void setTooltip(String title, Button button) {
        Tooltip tooltip = new Tooltip(title);
        Tooltip.install(button, tooltip);
    }

    private void onActionInAddAreaOfWorkCreateAreaOfWork() {
        addAreaOfProduct.setOnAction(event -> {
            //If a area is marked
            if (!mapController.getWKT().isEmpty()) {
                //Add area to productList
                productListDTO.addAreaOfWork(mapController.getWKT());
                drawInMapTheAreasOfWork();
                saveProductList();
                if (!actionActive)
                    addAction(ListAction.ADD_AREA_OF_WORK,FXCollections.observableArrayList(mapController.getWKT()));

            }
        });
    }

    private void onReferenceImagesChangeSaveChange() {
        referenceImgsList.getItems().addListener((ListChangeListener<ProductDTO>) c -> {
            if (referenceImgsList.getItems().isEmpty() && referenceImgsList.isVisible()) {
                setReferenceListVisible(false);
                productListView.setVisible(true);
            }
            if (!actionActive)
                while (c.next()) {
                    if (c.wasRemoved())
                        addAction(ListAction.DELETE_REFERENCE_IMAGE, FXCollections.observableArrayList(c.getRemoved()));
                    if (c.wasAdded())
                        addAction(ListAction.ADD_REFERENCE_IMAGE, FXCollections.observableArrayList(c.getAddedSubList()));
                }
        });
    }

    private void onProductListChangeSaveChange() {
        productListView.getItems().addListener((ListChangeListener<ProductDTO>) c -> {
            if (!actionActive)
                while (c.next()) {
                    if (c.wasRemoved())
                        addAction(ListAction.DELETE_PRODUCT, FXCollections.observableArrayList(c.getRemoved()));
                    if (c.wasAdded())
                        addAction(ListAction.ADD_PRODUCT, FXCollections.observableArrayList(c.getAddedSubList()));
                }
        });
    }

    private void onActionInPreviewGeneratePreview() {
        makePreview.setOnAction(e->this.generatePreview());
    }

    private void onReferenceListCellSelectLoadPreviewImage() {
        referenceImgsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            showProductInMap(newValue.getId());
        });
    }

    private void onShowReferenceImageSetVisibleReferenceImagesList() {
        showRI.setOnAction(e->{
            setReferenceListVisible(!referenceImgsList.isVisible());
            if (referenceImgsList.isVisible())
                showRI.setText("Products");
            else
                showRI.setText("Ref. Imgs.");
        });
    }

    private void setReferenceListVisible(boolean b) {
        referenceImgsList.setVisible(b);
        referenceImgsList.setManaged(b);
    }

    private void onDeleteFeatureActionRemoveFeature() {

        deleteFeature.setOnAction(e->{
            if (selectReferenceImage.isSelected()) {
                ProductDTO productDTO = productListDTO.getReferenceProducts().stream()
                        .filter(p -> p.getId().equals(mapController.getSelectedFeatureId()))
                        .findAny()
                        .orElse(null);
                productListDTO.removeReferenceProduct(productDTO);
                if (!actionActive)
                    addAction(ListAction.DELETE_REFERENCE_IMAGE,FXCollections.observableArrayList(productDTO));

            } else {
                if (!actionActive)
                    addAction(ListAction.DELETE_AREA_OF_WORK,
                            FXCollections.observableArrayList(
                                    productListDTO.getAreasOfWork().get(
                                            Integer.parseInt(mapController.getSelectedFeatureId()))));

                productListDTO.removeAreaOfWork(
                        productListDTO.getAreasOfWork().get(Integer.parseInt(mapController.getSelectedFeatureId())));
                drawInMapTheAreasOfWork();



            }
            saveProductList();
        });
    }

    private void onChangeReferenceImageToggleChangeMapMode() {
        selectReferenceImage.setOnMouseClicked(e->{
            if (selectReferenceImage.isSelected()) {
                mapController.setLayerSelectedAreaEvent(REFERENCE_IMAGES);
                mapController.setNotSelectedFeaturesBorderColor(Color.decode("#00976C"), Color.decode("#00976C"));
                mapController.setSelectedFeaturesBorderColor(Color.GREEN, Color.GREEN);
            } else {
                mapController.setLayerSelectedAreaEvent(AREA_OF_WORK_LAYER);
                changeColorOfSelectedFeaturesInMap(Color.MAGENTA, Color.ORANGE);
            }
        });
    }

    private void onReferenceImagesChangeRefreshMap() {
        productListDTO.getReferenceProducts().addListener((ListChangeListener<ProductDTO>) c -> refreshLayer(REFERENCE_IMAGES, Color.GREEN, "#C3FFE9"));
    }

    private void refreshLayer(String groundTruth, Color green, String s) {
        mapController.drawProductsInLayer(groundTruth, productListDTO.getReferenceProducts(), green, Color.decode(s));
    }

    private void onActionOnSearchReferenceImageLoadOpenSearcher() {
        searchGroundTruth.setOnAction(e->{
            //If there are not areas of work, or there are not selected, return
            if (mapController.getSelectedFeatureId() == null || mapController.getSelectedFeatureId().isEmpty() || selectReferenceImage.isSelected()) {
                AlertFactory.showErrorDialog("No area of work selected","","Select an area of work!");
                return;
            }

            //Open new CopernicusOpenSearchController if it is not loaded
            CopernicusOpenSearchController searchController;
            if (tabPaneComponent.isLoaded(COPERNICUS_OPEN_SEARCH_ID)) {
                tabPaneComponent.select(COPERNICUS_OPEN_SEARCH_ID);
                searchController = (CopernicusOpenSearchController) tabPaneComponent.getControllerOf(COPERNICUS_OPEN_SEARCH_ID);
            } else {
                searchController = new CopernicusOpenSearchController("id", new OpenSearcher());
                tabPaneComponent.load(searchController);
            }

            //Set parameters of ground truth
            Platform.runLater(()-> searchController.setSearchParameters(getReferenceImagesParameters()));
        });
    }

    private Map<String, String> getReferenceImagesParameters() {
        Map<String, String> data = new HashMap<>();
        data.put(SentinelProductParameters.FOOTPRINT.getParameterName(), productListDTO.getAreasOfWork().get(Integer.parseInt(mapController.getSelectedFeatureId())));
        data.put(SentinelProductParameters.PLATFORM_NAME.getParameterName(), "Sentinel-2");
        data.put(SentinelProductParameters.CLOUD_COVER_PERCENTAGE.getParameterName()+"_from", "0");
        data.put(SentinelProductParameters.CLOUD_COVER_PERCENTAGE.getParameterName()+"_to", "10");
        return data;
    }

    private void initMapController() {
        mapController = new GTMapController(mapPane.getPrefWidth(),mapPane.getPrefHeight(),true);
        //Set the layer that will be selected in mouse events
        mapController.addSelectedAreaEvent(AREA_OF_WORK_LAYER);

        //Set styles of features
        changeColorOfSelectedFeaturesInMap(Color.MAGENTA, Color.ORANGE);
        mapPane.getChildren().add(mapController.getView());

        AnchorPane.setBottomAnchor(mapController.getView(),0.0);
        AnchorPane.setTopAnchor(mapController.getView(),0.0);
        AnchorPane.setLeftAnchor(mapController.getView(),0.0);
        AnchorPane.setRightAnchor(mapController.getView(),0.0);

        //Draw products in map
        if (productListDTO.getProducts().size() > 0)
            mapController.printProductsInMap(productListDTO.getProducts(),Color.BLACK, null);

        //Draw reference images
        if (!productListDTO.getReferenceProducts().isEmpty())
            refreshLayer(REFERENCE_IMAGES, Color.decode("#00976C"), "#00976C");

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

    private void initAddAreaOfWorkButton() {
        Tooltip.install(addAreaOfProduct,new Tooltip("Add area of work"));
        GlyphsDude.setIcon(addAreaOfProduct, MaterialDesignIcon.BOOKMARK_PLUS);
    }

    private void initProductDTOListView() {
        productListView.setItems(productListDTO.getProducts());
        productListView.setCellFactory(e -> new ProductListCell(tabPaneComponent.getMainController(),productListDTO, mapController,true));
        productListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void initReferenceImagesListView() {
        referenceImgsList.setItems(productListDTO.getReferenceProducts());
        referenceImgsList.setCellFactory(e -> new ProductListCell(tabPaneComponent.getMainController(),productListDTO, mapController, false));
        referenceImgsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void bindProperties() {
        numberOfProducts.textProperty().bind(productListDTO.countProperty().asString());
        size.textProperty().bind(Bindings.format("%.2f", productListDTO.sizeAsDoubleProperty()).concat(" GB"));
        title.textProperty().bind(productListDTO.nameProperty());
        description.textProperty().bind(productListDTO.descriptionProperty());
        showRI.visibleProperty().bind(Bindings.isEmpty(productListDTO.getReferenceProducts()).not());
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
            refreshListView(productListView);
            refreshListView(referenceImgsList);
        });

    }

    public void refreshListView(JFXListView<ProductDTO> list) {
        list.applyCss();
        list.refresh();
        list.setItems(list.getItems());
    }

    private void onProductSelectedLoadPreviewImage() {
        productListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            if (newValue != null)
                showProductInMap(newValue.getId());
        });
    }

    private void showProductInMap(String id) {
        if (id ==null)
            return;

        changeColorOfSelectedFeaturesInMap(Color.BLUE, Color.BLACK);
        mapController.showProductArea(Collections.singletonList(id),"products");
        changeColorOfSelectedFeaturesInMap(Color.MAGENTA, Color.ORANGE);
    }

    private void changeColorOfSelectedFeaturesInMap(Color blue, Color black) {
        mapController.setSelectedFeaturesBorderColor(blue, null);
        mapController.setNotSelectedFeaturesBorderColor(black, null);
    }
    private void generatePreview() {
        try {
            ProductDTO product = productListView.getSelectionModel().getSelectedItem();
            if (product == null)
                return;

            if (productNotDownloaded(product)) return;

            if (productHasAreasOfWork()) {
                List<String> areasOfWorkOfProduct = productListDTO.areasOfWorkOfProduct(product.getFootprint());

                if (areasOfWorkOfProduct.contains(getSelectedAreaOfWork())) {
                    if (noDefaultWorkflow(product)) return;

                    if (ifPreviewIsAlreadyLoaded(product)) {
                        tabPaneComponent.select("Preview-"+product.getId());
                        PreviewController previewController = (PreviewController) tabPaneComponent.getControllerOf("Preview-"+product.getId());
                        previewController.setArea(getSelectedAreaOfWork());
                        previewController.setWorkflowDTO(productListDTO.getWorkflow(WorkflowType.valueOf(product.getProductType())));
                        previewController.initData();
                    } else {
                        tabPaneComponent.fireEvent(
                                new ComponentEvent(this, "Opening preview for product "+product.getTitle()));
                        tabPaneComponent.load(
                                new PreviewController(product,getSelectedAreaOfWork(),
                                        productListDTO.getWorkflow(WorkflowType.valueOf(product.getProductType())),
                                        productListDTO.getName()));
                    }

                } else {
                    AlertFactory.showErrorDialog("Product error",
                            "Product error",
                            "Product does not contain the selected area of work");
                }
            } else {
                AlertFactory.showErrorDialog("Error",
                        "Select a product and an area of work",
                        "You must select a product and an area of work to generate the preview");
            }
        } catch (Exception e) {
            AlertFactory.showErrorDialog("Error in preview","Error","Error creating the preview");
        }
    }

    private boolean ifPreviewIsAlreadyLoaded(ProductDTO product) {
        return tabPaneComponent.isLoaded("Preview-"+product.getId());
    }

    private boolean productHasAreasOfWork() {
        return getSelectedAreaOfWork() != null;
    }

    private boolean noDefaultWorkflow(ProductDTO selectedItem) {
        if (ProcessingConfiguration.getDefaultWorkflow(selectedItem.getProductType()) == null){
            AlertFactory.showErrorDialog("Product not supported","Product not supported", "This product is not supported for the" +
                    "application");
            return true;
        }
        return false;
    }

    private boolean productNotDownloaded(ProductDTO selectedItem) {
        if (!FileUtils.productExists(selectedItem.getTitle())){
            AlertFactory.showErrorDialog("Product not downloaded","Product not downloaded","The selected product is not downloaded!");
            return true;
        }
        return false;
    }

    private String getSelectedAreaOfWork() {
        try {
            return productListDTO.getAreasOfWork().get(Integer.parseInt(mapController.getSelectedFeatureId()));
        } catch (Exception e){
            return null;
        }
    }
}
