package controller.search;

import com.jfoenix.controls.*;
import controller.interfaces.TabItem;
import controller.cell.ProductResultListCellController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import controller.GTMapSearchController;
import gui.components.TabPaneComponent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import model.openSearcher.SentinelProductParameters;
import model.openSearcher.OpenSearchResponse;
import model.products.ProductDTO;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.CopernicusService;
import services.search.OpenSearcher;
import utils.AlertFactory;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class CopernicusOpenSearchController implements TabItem, SearchController  {

    public static final String SENTINEL_1 = "Sentinel-1";
    public static final String SENTINEL_2 = "Sentinel-2";
    private final String id;
    private final FXMLLoader loader;
    private OpenSearcher searcher;
    private Parent parent;

    @FXML
    private ScrollPane rootPane;
    @FXML
    private AnchorPane spinnerPane;
    @FXML
    private JFXSpinner spinnerWait;
    @FXML
    private ChoiceBox<String> platformList;
    @FXML
    private ChoiceBox<String> productTypeList;
    @FXML
    private Pane polarisationPane;
    @FXML
    private ChoiceBox<String> polarisation;
    @FXML
    private Pane sensorPane;
    @FXML
    private ChoiceBox<String> sensorMode;
    @FXML
    private Pane cloudPane;
    @FXML
    private TextField cloudCoverage;
    @FXML
    private DatePicker dateStart;
    @FXML
    private DatePicker dateFinish;
    @FXML
    private Button search;
    @FXML
    private Pagination pagination;
    @FXML
    private ChoiceBox<Integer> rows;
    @FXML
    private Pane resultsPane;
    @FXML
    private HBox resultPaneHeader;
    @FXML
    private ListView<ProductDTO> resultProductsList;
    @FXML
    private AnchorPane mapPane;
    @FXML
    private Button showResults;

    private GTMapSearchController mapController;

    static final Logger logger = LogManager.getLogger(CopernicusOpenSearchController.class.getName());
    private boolean isPaginationSetted;
    private TabPaneComponent tabPaneComponent;

    public CopernicusOpenSearchController(String id) {
        this.id = id;
        loader = new FXMLLoader(getClass().getResource("/fxml/CopernicusOpenSearchView.fxml"));
        loader.setController(this);
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return id;
    }

    @Override
    public ObservableList<ProductDTO> getSelectedProducts() {
        return resultProductsList.getSelectionModel().getSelectedItems();
    }

    @Override
    public ObservableList<ProductDTO> getProducts() {
        return resultProductsList.getItems();
    }

    @Override
    public void setTabPaneComponent(TabPaneComponent component) {
        this.tabPaneComponent = component;
    }

    @Override
    public Parent getView() {
        return searcher == null ? null : rootPane;
    }

    @Override
    public Task<Parent> start() {
        searcher = new OpenSearcher(CopernicusService.getInstance());
        return new Task<>() {
            @Override
            protected Parent call() throws Exception {
                searcher.login();
                initViewData();
                return getView();
            }
        };
    }

    @Override
    public String getName() {

        return getClass().getSimpleName();
    }

    @Override
    public String getItemId() {
        return getClass().getSimpleName();
    }

    private void initViewData() {
        setSpinnerVisible(false);
        setPaginationVisible(false);
        initProductsPerPage();
        setSatelliteList();
        setSentinel1Data();
        setProductsList();
        initResultPaneHeader();
        onActionInPaginationFireSearchEvent();
        initGTMapController();
        onSearchButtonActionSearchEvent();
        onCloudCoverageChangeAllowOnlyNumbers();
    }

    private void onSearchButtonActionSearchEvent() {
        search.setOnMouseClicked(e->{
            searcher.setStartProductIndex(0);
            isPaginationSetted = false;
            search();
        });
    }

    private void initGTMapController() {
        mapController = new GTMapSearchController(mapPane.getPrefWidth(),mapPane.getPrefHeight(), true);
        mapController.addSelectedAreaEvent("products");

        onMouseClickInMapHighlightSelectedProductsEvent();

        mapPane.getChildren().addAll(mapController.getView());
        AnchorPane.setLeftAnchor(mapController.getView(),0.0);
        AnchorPane.setRightAnchor(mapController.getView(),0.0);
    }

    private void onMouseClickInMapHighlightSelectedProductsEvent() {
        BorderPane view = (BorderPane)mapController.getView();
        view.getCenter().addEventHandler(MouseEvent.MOUSE_CLICKED, event-> {
            //If there are not selected products in map, deselect items in listview
            if (mapController.getSelectedProduct() == null) {
                resultProductsList.getSelectionModel().clearSelection();
                return;
            }

            //Get selected product
            ProductDTO product = resultProductsList.getItems().stream()
                    .filter(p -> p.getId().equals(mapController.getSelectedProduct()))
                    .findFirst()
                    .orElse(null);

            //Clear selected items in listview if control is not pushed
            if (!event.isControlDown())
                resultProductsList.getSelectionModel().clearSelection();

            if (product != null) {
                //If product is already selected, deselected
                if (isProductSelectedInListView(product)) {
                    deselectProductInListView(product);
                } else {
                    selectProductInListView(product);
                }

            }
        });
    }

    private void selectProductInListView(ProductDTO product) {
        resultProductsList.setFocusTraversable(true);
        resultProductsList.getFocusModel().focus(resultProductsList.getItems().indexOf(product));
        resultProductsList.getSelectionModel().select(resultProductsList.getItems().indexOf(product));
        resultProductsList.scrollTo(product);
    }

    private void deselectProductInListView(ProductDTO product) {
        resultProductsList.getSelectionModel().clearSelection(resultProductsList.getItems().indexOf(product));
    }

    private boolean isProductSelectedInListView(ProductDTO product) {
        return resultProductsList.getSelectionModel().isSelected(resultProductsList.getItems().indexOf(product));
    }

    private void onCloudCoverageChangeAllowOnlyNumbers() {
        cloudCoverage.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*") || newValue.length() == 0)
                cloudCoverage.setText(newValue.replaceAll("[^\\d]", ""));
            else {
                if (Integer.parseInt(newValue) < 0 || Integer.parseInt(newValue) > 100)
                    cloudCoverage.setText(oldValue);
            }
        });
    }

    private void setPaginationVisible(boolean b) {
        pagination.setVisible(b);
        pagination.setManaged(b);
    }

    private void onActionInPaginationFireSearchEvent() {
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            searcher.setStartProductIndex(searcher.getProductsPerPage()*(newIndex.intValue()));
            isPaginationSetted = true;
            search();
        });
    }

    private void initProductsPerPage() {
        rows.setItems(FXCollections.observableArrayList(
                25,50,75,100));
        rows.setValue(25);
        rows.getSelectionModel()
            .selectedItemProperty()
            .addListener((observableValue, oldValue, newValue) -> {
                if (newValue > 0)
                    searcher.setProductPerPage(newValue);
            });
    }

    private void setSentinel1Data() {
        setSentinel1Instruments();
        setSentinel1SensorMode();
        setSentinel1Polarisation();
    }

    private void setSentinel1Polarisation() {
        polarisation.setItems(FXCollections.observableArrayList(
                "All","VV", "VH","HV","VH","HH+HV","VV+VH"));
        polarisation.setValue("All");
    }

    private void setSentinel1SensorMode() {
        sensorMode.setItems(FXCollections.observableArrayList(
                "All","SM", "IW","EW","WV"));
        sensorMode.setValue("All");
    }

    private void setProductsList() {
        resultProductsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        resultProductsList.setOnMouseClicked(highlightInMapSelectedProductInListView());
    }

    private EventHandler<MouseEvent> highlightInMapSelectedProductInListView() {
        return event -> {
            ObservableList<ProductDTO> products = resultProductsList.getSelectionModel().getSelectedItems();
            //Get the ids of the products for the features id
            mapController.showProductArea(
                    products.stream()
                            .map(ProductDTO::getId)
                            .collect(Collectors.toList())
                    , "products");
        };
    }

    private void setSatelliteList() {
        platformList.setItems(FXCollections.observableArrayList(
                SENTINEL_1, SENTINEL_2));

        platformList.getSelectionModel()
                .selectedIndexProperty()
                .addListener((observableValue, oldValue, newValue) -> {
                        if (platformList.getItems().get(newValue.intValue()).equals(SENTINEL_1)) {
                            setSentinel1Instruments();
                            setCloudCoverageDisabled();
                            setSentinel1ParametersEnabled();
                        } else if (platformList.getItems().get(newValue.intValue()).equals(SENTINEL_2)) {
                            setSentinel2Instruments();
                            setSentinel1ParametersDisabled();
                            setCloudCoverageEnabled();
                        }

                    }
                );
        platformList.setValue(SENTINEL_1);

        setCloudCoverageDisabled();
    }

    private void setSentinel1ParametersEnabled() {
        polarisationPane.setManaged(true);
        polarisationPane.setVisible(true);
        sensorPane.setManaged(true);
        sensorPane.setVisible(true);
    }

    private void setSentinel1ParametersDisabled() {
        polarisationPane.setManaged(false);
        polarisationPane.setVisible(false);
        sensorPane.setManaged(false);
        sensorPane.setVisible(false);
    }

    private void setCloudCoverageDisabled() {
        cloudPane.setManaged(false);
        cloudPane.setVisible(false);
    }

    private void setCloudCoverageEnabled() {
        cloudPane.setManaged(true);
        cloudPane.setVisible(true);
    }

    private void setSentinel2Instruments() {
        productTypeList.setItems(FXCollections.observableArrayList(
                "All","S2MSI1C", "S2MSI2A","S2MSI2Ap"));
        productTypeList.setValue("All");
    }

    private void setSentinel1Instruments() {
        productTypeList.setItems(FXCollections.observableArrayList(
                "All","GRD", "OCN","SLC"));
        productTypeList.setValue("All");
    }

    private void initResultPaneHeader() {
        resultsPane.setVisible(false);
        Button iconButton = GlyphsDude.createIconButton(FontAwesomeIcon.CLOSE,"");
        iconButton.setAccessibleText("Close results");
        iconButton.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            resultsPane.setVisible(false);
            if (resultProductsList.getItems().size() > 0)
                showShowResultsButton();
            else
                hideShowResultsButton();
        });
        resultPaneHeader.getChildren().add(iconButton);
        hideShowResultsButton();
        showResults.addEventHandler(MouseEvent.MOUSE_CLICKED, e-> resultsPane.setVisible(true));
    }

    private void hideShowResultsButton() {
        toggleShowResultsButton(false);
    }

    private void showShowResultsButton() {
        toggleShowResultsButton(true);
    }

    private void toggleShowResultsButton(boolean b) {
        showResults.setVisible(b);
        showResults.setManaged(b);
    }

    @Override
    public void search() {
        clearListAndFilter();
        addParameters();
        clearMap();
        logger.atLevel(Level.INFO).log("Petition to OpenSearch with parameters {}",searcher.getSearchParametersAsString());
        setSpinnerVisible(true);
        Task<OpenSearchResponse> response = getSearchTask();
        response.setOnSucceeded(event -> getOnSucceedSearchEvent(response));
        response.setOnFailed(this::onFailedSearch);
        new Thread(response).start();
    }

    private void onFailedSearch(WorkerStateEvent workerStateEvent) {
        AlertFactory.showErrorDialog("Error while searching","An error has ocurred while searching",
                "Error while searching. " + workerStateEvent.getSource().getMessage());
        setSpinnerVisible(false);
    }

    private void getOnSucceedSearchEvent(Task<OpenSearchResponse> response) {
        try {
            ObservableList<ProductDTO> tObservableArray =
                    FXCollections.observableArrayList(response.get().getProducts());

            resultProductsList.setItems(tObservableArray);
            resultProductsList.setCellFactory(e -> new ProductResultListCellController(tabPaneComponent));
            clearMap();
            OpenSearchResponse openSearchResponse = response.get();
            if (openSearchResponse.getProducts().size() > 0) {
                setPaginationVisible(true);
                writeProductsFootprintInMap(openSearchResponse.getProducts());
                if (!isPaginationSetted)
                    setPagination(openSearchResponse.getNumOfProducts(),openSearchResponse.getProducts().size());
            } else {
                setPaginationVisible(false);
            }
            setSpinnerVisible(false);
            resultsPane.setVisible(true);
        } catch (InterruptedException | ExecutionException e) {
            logger.atLevel(Level.ERROR).log("Error while retrieving search results: {0}",e);
            e.printStackTrace();
        }
    }

    private void clearMap() {
        mapController.clearMap("products");
    }

    private void setSpinnerVisible(boolean b) {
        if (b)
            rootPane.setCursor(Cursor.WAIT);
        else
            rootPane.setCursor(Cursor.DEFAULT);
        spinnerPane.setVisible(b);
        spinnerPane.setManaged(b);
        spinnerWait.setVisible(b);
        spinnerWait.setManaged(b);
    }

    private void setPagination(int numOfProducts, int rows) {
        if (numOfProducts > rows) {
            setPaginationVisible(true);
            double numPages = Math.ceil((numOfProducts/rows)+0.5f);
            pagination.setPageCount((int)numPages);
        } else
            setPaginationVisible(false);

    }

    private void writeProductsFootprintInMap(List<ProductDTO> products) {
        mapController.printProductsInMap(products, Color.BLACK, null);

    }

    private Task<OpenSearchResponse> getSearchTask() {
        return new Task<>() {
            @Override
            protected OpenSearchResponse call() throws Exception {
                return searcher.search();
            }
        };
    }

    private void clearListAndFilter() {
        resultProductsList.getItems().clear();
        searcher.clearSearchParameters();
    }

    private void addParameters() {
        addPlatformNameParameter();
        addWKTParameter();
        addInstrumentParameter();
        addDateRangeFilter();
        if (platformList.getValue().equals(SENTINEL_1)) {
            addPolarisationModeParameter();
            addSensorModeParameter();
        } else if (platformList.getValue().equals(SENTINEL_2)) {
            addCloudCoverageParameter();
        }
    }

    private void addCloudCoverageParameter() {
        if (!cloudCoverage.getText().isEmpty())
            searcher.addSearchParameter(SentinelProductParameters.CLOUD_COVER_PERCENTAGE,cloudCoverage.getText());
    }

    private void addSensorModeParameter() {
        if (!sensorMode.getValue().equals("All"))
            searcher.addSearchParameter(SentinelProductParameters.SENSOR_OPERATIONAL_MODE, sensorMode.getValue());
    }

    private void addPolarisationModeParameter() {
        if (!polarisation.getValue().equals("All"))
            searcher.addSearchParameter(SentinelProductParameters.POLARISATION_MODE, polarisation.getValue());
    }

    private void addInstrumentParameter() {
        if (!productTypeList.getValue().equals("All"))
            searcher.addSearchParameter(SentinelProductParameters.PRODUCT_TYPE, productTypeList.getValue());
    }

    private void addWKTParameter() {
        if (mapController.getWKT().length()>0)
            searcher.addSearchParameter(SentinelProductParameters.FOOTPRINT,"\"Intersects("+ mapController.getWKT()+")\"");
    }

    private void addDateRangeFilter() {
        if (dateStart.getValue() != null || dateFinish.getValue() != null)
            searcher.addSearchParameter(SentinelProductParameters.INGESTION_DATE,getFromToIngestionDate(dateStart.getValue(),dateFinish.getValue()));
    }

    //REFACTOR to OpenSearch

    private String getFromToIngestionDate(LocalDate start, LocalDate finish) {
        String startS = getDateString(start,"*",0,0,0,":00.001");
        String endS = getDateString(finish,"NOW",23,59,59,".999");
        return "["+ startS+ " TO " + endS + "]";
    }

    private String getDateString(LocalDate date, String dateNull, int hour, int minute, int seconds, String nano) {
        if (date == null)
            return dateNull;

        LocalDateTime localDateTimeFinish = date.atTime(hour, minute, seconds, 0);
        localDateTimeFinish.atZone(ZoneId.of("UTC"));
        return localDateTimeFinish.toString()+nano+"Z";
    }

    private void addPlatformNameParameter() {
        searcher.addSearchParameter(SentinelProductParameters.PLATFORM_NAME, platformList.getValue());
    }
}
