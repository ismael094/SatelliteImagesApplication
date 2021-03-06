package controller.search;

import com.jfoenix.controls.*;
import controller.cell.ProductResultListCell;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import controller.GTMapController;
import gui.components.TabPaneComponent;
import gui.components.listener.ComponentEvent;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import jfxtras.styles.jmetro.JMetro;
import model.openSearcher.SentinelProductParameters;
import model.openSearcher.OpenSearchResponse;
import model.products.ProductDTO;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.io.ParseException;
import services.search.OpenSearcher;
import utils.AlertFactory;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static utils.ThemeConfiguration.getJMetroStyled;
import static utils.ThemeConfiguration.getThemeMode;

public class CopernicusOpenSearchController extends SearchController<ProductDTO>  {

    public static final String SENTINEL_1 = "Sentinel-1";
    public static final String SENTINEL_2 = "Sentinel-2";
    private final ObservableList<OpenSearchResponse> allResponses;
    private final ObservableList<Map<String,String>> parametersOfAllResponses;
    private int index;
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
    private AnchorPane polarisationPane;
    @FXML
    private ChoiceBox<String> productTypeList;
    @FXML
    private ChoiceBox<String> platformList;
    @FXML
    private ChoiceBox<String> loadSearch;
    @FXML
    private AnchorPane searchParameters;
    @FXML
    private ChoiceBox<String> polarisation;
    @FXML
    private AnchorPane sensorPane;
    @FXML
    private ChoiceBox<String> sensorMode;
    @FXML
    private AnchorPane cloudPane;
    @FXML
    private TextField cloudCoverage;
    @FXML
    private TextField cloudCoverageTo;
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
    @FXML
    private Button saveSearch;
    @FXML
    private JFXButton show;

    private GTMapController mapController;

    static final Logger logger = LogManager.getLogger(CopernicusOpenSearchController.class.getName());
    private boolean isRedoOrUndo;
    private TabPaneComponent tabPaneComponent;
    private Map<String,Control> control;

    public CopernicusOpenSearchController(String id, OpenSearcher openSearcher) {
        searcher = openSearcher;
        this.id = id;
        loader = new FXMLLoader(getClass().getResource("/fxml/CopernicusOpenSearchView.fxml"));
        loader.setController(this);
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        geotoolsController();

        setControlsMap();

        allResponses = FXCollections.observableArrayList();
        parametersOfAllResponses = FXCollections.observableArrayList();
        index = 0;
    }

    private void setControlsMap() {
        control = new HashMap<>();
        control.put(SentinelProductParameters.PLATFORM_NAME.getParameterName(),platformList);
        control.put(SentinelProductParameters.PRODUCT_TYPE.getParameterName(),productTypeList);
        control.put(SentinelProductParameters.POLARISATION_MODE.getParameterName(),polarisation);
        control.put(SentinelProductParameters.SENSOR_OPERATIONAL_MODE.getParameterName(),sensorMode);
        control.put(SentinelProductParameters.INGESTION_DATE.getParameterName()+"_from",dateStart);
        control.put(SentinelProductParameters.INGESTION_DATE.getParameterName()+"_to",dateFinish);
        control.put(SentinelProductParameters.CLOUD_COVER_PERCENTAGE.getParameterName()+"_from",cloudCoverage);
        control.put(SentinelProductParameters.CLOUD_COVER_PERCENTAGE.getParameterName()+"_to",cloudCoverageTo);
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
        return new Task<Parent>() {
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
        return "Copernicus Open Search";
    }

    @Override
    public String getItemId() {
        return "Copernicus Open Search";
    }

    public void initViewData() {

        loadSearch.setVisible(false);
        loadSearch.setManaged(false);

        setSpinnerVisible(false);
        setPaginationVisible(false);
        productsPerPage();
        initSatelliteValues();
        onSatelliteChangesModifyParameters();
        setSentinel1ParametersAsDefault();
        initProductListView();
        setProductsResponseView();
        onActionInPaginationFireSearchEvent();
        onActionInSearchButtonFireSearchEvent();
        onCloudCoverageChangeAllowOnlyNumbers();

        Tooltip tp = new Tooltip("Example: [5.3 TO 25.8], or single number");
        cloudCoverage.setTooltip(tp);
        Tooltip.install(cloudCoverage,tp);

        saveSearch.setVisible(false);
        saveSearch.setOnAction(e->{
            /*String s = dialogForSearchName();
            if (s == null)
                return;
            Map<String, String> parameters = getParameters();
            tabPaneComponent.getMainController().getUser().saveSearch(s,parameters);*/
        });
    }

    private void onActionInSearchButtonFireSearchEvent() {
        search.setOnMouseClicked(e->{
            searcher.setStartProductIndex(0);
            isRedoOrUndo = false;
            search();
        });
    }

    private void geotoolsController() {
        mapController = new GTMapController(1000,600, true);
        mapController.addSelectedAreaEvent("products");

        onMouseClickInMapHighlightSelectedProductsEvent();
        mapPane.getChildren().addAll(mapController.getView());
        AnchorPane.setLeftAnchor(mapController.getView(),0.0);
        AnchorPane.setRightAnchor(mapController.getView(),0.0);
        AnchorPane.setTopAnchor(mapController.getView(),0.0);
        AnchorPane.setBottomAnchor(mapController.getView(),0.0);
    }

    private void onMouseClickInMapHighlightSelectedProductsEvent() {
        mapController.getMap().addEventHandler(MouseEvent.MOUSE_CLICKED, event-> {
            //If there are not selected products in map, deselect items in listview
            if (mapController.getSelectedFeatureId() == null) {
                resultProductsList.getSelectionModel().clearSelection();
                return;
            }

            //Get selected product
            ProductDTO product = resultProductsList.getItems().stream()
                    .filter(p -> p.getId().equals(mapController.getSelectedFeatureId()))
                    .findFirst()
                    .orElse(null);

            //Clear selected items in listview if control is not pressed
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
        setCloudCoverageListener(cloudCoverage);
        setCloudCoverageListener(cloudCoverageTo);
    }

    private void setCloudCoverageListener(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*"))
                if (getThemeMode().equals("light"))
                    field.setStyle("-fx-text-fill: black");
                else
                    field.setStyle("-fx-text-fill: white");
            else {
                field.setStyle("-fx-text-fill: RED");
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
            if (!isRedoOrUndo)
                search();
            redoOrUndoOperation(false);
        });
    }

    private void productsPerPage() {
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

    private void setSentinel1ParametersAsDefault() {
        setSentinel1Instruments();
        setSentinel1SensorMode();
        setSentinel1Polarisation();
    }

    private void setSentinel1Polarisation() {
        polarisation.setItems(FXCollections.observableArrayList(
                "All","VV", "VH","HV","VH","HH+HV","VV+VH"));

        if (polarisation.getValue() == null)
            polarisation.setValue("All");
    }

    private void setSentinel1SensorMode() {
        sensorMode.setItems(FXCollections.observableArrayList(
                "All","SM", "IW","EW","WV"));
        if (sensorMode.getValue() == null)
            sensorMode.setValue("All");
    }

    private void initProductListView() {
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

    private void initSatelliteValues() {
        platformList.setItems(FXCollections.observableArrayList(
                SENTINEL_1, SENTINEL_2));
    }

    private void onSatelliteChangesModifyParameters() {
        platformList.getSelectionModel()
                .selectedItemProperty()
                .addListener((observableValue, oldValue, newValue) -> {
                        if (newValue.equals(SENTINEL_1)) {
                            sentinel1();
                        } else if (newValue.equals(SENTINEL_2)) {
                            sentinel2();
                        }
                    }
                );

        if (platformList.getValue() == null) {
            platformList.setValue(SENTINEL_1);
            setCloudCoverageDisabled();
        }
    }

    private void sentinel2() {
        setSentinel2Instruments();
        setSentinel1ParametersDisabled();
        setCloudCoverageEnabled();
    }

    private void sentinel1() {
        setSentinel1Instruments();
        setCloudCoverageDisabled();
        setSentinel1ParametersEnabled();
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
        System.out.println(productTypeList.getValue());
        if (productTypeList.getValue() == null)
            productTypeList.setValue("All");
    }

    private void setProductsResponseView() {
        resultsPane.setVisible(false);
        Button closeProductResultsButton = GlyphsDude.createIconButton(FontAwesomeIcon.ARROW_LEFT,"");

        closeProductResultsButton.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            resultsPane.setVisible(false);
            searchParameters.setVisible(true);
            if (resultProductsList.getItems().size() > 0)
                showShowResultsButton();
            else
                hideShowResultsButton();
        });

        resultPaneHeader.getChildren().add(closeProductResultsButton);
        hideShowResultsButton();

        show.setVisible(false);
        show.setText("");
        GlyphsDude.setIcon(show,FontAwesomeIcon.ARROW_RIGHT);
        show.setOnAction(e->{
            resultsPane.setVisible(true);
            searchParameters.setVisible(false);
        });
    }

    private void hideShowResultsButton() {
        toggleShowResultsButton(false);
    }

    private void showShowResultsButton() {
        toggleShowResultsButton(true);
    }

    private void toggleShowResultsButton(boolean b) {
        show.setVisible(b);
        show.setManaged(b);
    }

    @Override
    public void search() {
        clearListAndFilter();
        addParameters();
        clearMap();
        logger.atLevel(Level.INFO).log("Petition to OpenSearch with parameters {}",searcher.getSearchParametersAsString());
        tabPaneComponent.fireEvent(new ComponentEvent(this,"Petition to OpenSearch with parameters " + searcher.getSearchParametersAsString()));
        setSpinnerVisible(true);
        search.setDisable(true);
        Task<OpenSearchResponse> response = getSearchTask();
        response.setOnSucceeded(event -> onSucceedSearch(response));
        response.setOnFailed(this::onFailedSearch);
        new Thread(response).start();
        Platform.runLater(()->{
            AlertFactory.showSuccessDialog("Search","Search petition made","Waiting to retrieve products. This can last several minutes!");
        });
    }

    @Override
    public void setSearchParameters(Map<String, String> parametersOfAllResponses) {
        //init binds also
        if (!parametersOfAllResponses.getOrDefault("platformname","").isEmpty())
            if (parametersOfAllResponses.get("platformname").equals(SENTINEL_1))
                sentinel1();
            else
                sentinel2();

        parametersOfAllResponses.forEach((key, value)->{
            Control control = this.control.get(key);
            if (control instanceof ChoiceBox)
                ((ChoiceBox<String>)control).setValue(value);
            else if (control instanceof TextField)
                ((TextField)control).setText(value);
            else if (control instanceof DatePicker){
                ((DatePicker)control).setValue(LocalDate.parse(value));
            } else {
                try {
                    mapController.clearMap("searchArea");
                    mapController.addProductWKT(value,"myId","searchArea");
                    mapController.drawFeaturesOfLayer("searchArea", Color.RED,null);
                    mapController.setSearchArea(value);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public HashMap<String, String> getSearchParameters() {
        HashMap<String,String> res = new HashMap<>();
        res.put(SentinelProductParameters.PLATFORM_NAME.getParameterName(), platformList.getValue());
        res.put(SentinelProductParameters.PRODUCT_TYPE.getParameterName(), productTypeList.getValue());

        if (mapController.getWKT() != null && !mapController.getWKT().isEmpty())
            res.put(SentinelProductParameters.FOOTPRINT.getParameterName(), mapController.getWKT());
        if (platformList.getValue().equals(SENTINEL_1)) {
            res.put(SentinelProductParameters.POLARISATION_MODE.getParameterName(), polarisation.getValue());
            res.put(SentinelProductParameters.SENSOR_OPERATIONAL_MODE.getParameterName(), sensorMode.getValue());
        } else {
            res.put(SentinelProductParameters.CLOUD_COVER_PERCENTAGE.getParameterName()+"_to", cloudCoverage.getText());
            res.put(SentinelProductParameters.CLOUD_COVER_PERCENTAGE.getParameterName()+"_from", cloudCoverageTo.getText());
        }
        if (dateStart.getValue() != null)
            res.put(SentinelProductParameters.INGESTION_DATE.getParameterName()+"_from", dateStart.getValue().toString());
        if (dateFinish.getValue() != null)
            res.put(SentinelProductParameters.INGESTION_DATE.getParameterName()+"_to", dateFinish.getValue().toString());
        return res;
    }

    private void onFailedSearch(WorkerStateEvent workerStateEvent) {
        search.setDisable(false);
        AlertFactory.showErrorDialog("Error while searching","An error has occurred while searching",
                "Error while searching. " + workerStateEvent.getSource().getMessage());
        setSpinnerVisible(false);

    }

    private void onSucceedSearch(Task<OpenSearchResponse> response) {

        search.setDisable(false);
        try {
            tabPaneComponent.updateObservers(this);
            setResponse(response.get());
            saveSearch(response);
        } catch (ExecutionException | InterruptedException e) {
            logger.atLevel(Level.ERROR).log("Error while retrieving search results: {0}",e);
            e.printStackTrace();
        }
    }

    @Override
    public String getRedo() {
        if (index < 0 || index >= allResponses.size())
            return null;
        return "Last search";
    }

    @Override
    public String getUndo() {
        try {
            allResponses.get(index - 2);
            return "Preview search";
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public void redo() {
        if (index < 0 || index >= allResponses.size())
            return;
        setResponse(allResponses.get(index));
        setSearchParameters(parametersOfAllResponses.get(index));
        index++;
        redoOrUndoOperation(true);
        tabPaneComponent.updateObservers(this);
        mapController.focusOnLayer("products");
    }

    @Override
    public void undo() {
        if (index <= 1)
            return;
        setResponse(allResponses.get(index - 2));
        setSearchParameters(parametersOfAllResponses.get(index - 2));
        index--;
        redoOrUndoOperation(true);
        tabPaneComponent.updateObservers(this);
        mapController.focusOnLayer("products");
    }

    private void redoOrUndoOperation(boolean b) {
        isRedoOrUndo = b;
    }

    private void saveSearch(Task<OpenSearchResponse> response) throws InterruptedException, ExecutionException {
        allResponses.add(index,response.get());
        parametersOfAllResponses.add(index, getSearchParameters());
        index++;
    }

    private void setResponse(OpenSearchResponse response) {
        ObservableList<ProductDTO> tObservableArray =
                FXCollections.observableArrayList(response.getProducts());

        resultProductsList.setItems(tObservableArray);
        resultProductsList.setCellFactory(e -> new ProductResultListCell(tabPaneComponent));
        clearMap();
        if (response.getProducts().size() > 0) {
            setPaginationVisible(true);
            writeProductsFootprintInMap(response.getProducts());
            setPagination(response);
        } else {
            setPaginationVisible(false);
        }
        setSpinnerVisible(false);
        resultsPane.setVisible(true);
        searchParameters.setVisible(false);
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

    private void setPagination(OpenSearchResponse response) {
        if (response.getNumOfProducts() > response.getProducts().size()) {
            setPaginationVisible(true);
            double numPages = getNumberOfPages(response.getNumOfProducts(),(double)response.getRows());
            System.out.println(numPages);
            pagination.setPageCount((int)numPages);
            //pagination.setCurrentPageIndex(response.getStartIndex());
        } else
            setPaginationVisible(false);

    }

    private double getNumberOfPages(int numOfProducts, double rows) {
        if (numOfProducts%rows == 0)
            return Math.ceil((numOfProducts/rows));
        else
            return Math.ceil((numOfProducts/rows)+0.5f);
    }

    private void writeProductsFootprintInMap(List<ProductDTO> products) {
        mapController.printProductsInMap(products, Color.BLACK, null);

    }

    private Task<OpenSearchResponse> getSearchTask() {
        return new Task<OpenSearchResponse>() {
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
        if (isCloudCoverageValid(cloudCoverage) && isCloudCoverageValid(cloudCoverageTo)) {
            searcher.addJoinRangeParameter(SentinelProductParameters.CLOUD_COVER_PERCENTAGE,
                    cloudCoverage.getText(),cloudCoverageTo.getText());
        } else {
            if (isCloudCoverageValid(cloudCoverage))
                addCloudCoverageParsed(cloudCoverage.getText());
            else if (isCloudCoverageValid(cloudCoverageTo))
                addCloudCoverageParsed(cloudCoverageTo.getText());
        }
    }

    private boolean isCloudCoverageValid(TextField cloudCoverage) {
        return !cloudCoverage.getText().isEmpty() && cloudCoverage.getText().matches("\\d*");
    }

    private void addCloudCoverageParsed(String cloudCoverage) {
        searcher.addSearchParameter(SentinelProductParameters.CLOUD_COVER_PERCENTAGE,cloudCoverage);
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
        if (mapController.getWKT() !=null && mapController.getWKT().length()>0)
            searcher.addSearchParameter(SentinelProductParameters.FOOTPRINT,"\"Intersects("+ mapController.getWKT()+")\"");
    }

    private void addDateRangeFilter() {
        if (dateStart.getValue() != null || dateFinish.getValue() != null)
            searcher.addDateParameter(SentinelProductParameters.INGESTION_DATE,dateStart.getValue(),dateFinish.getValue());
    }

    private void addPlatformNameParameter() {
        searcher.addSearchParameter(SentinelProductParameters.PLATFORM_NAME, platformList.getValue());
    }

    private String dialogForSearchName() {
        TextField field = new TextField();
        JFXAlert alert = new JFXAlert(platformList.getScene().getWindow());

        alert.initModality(Modality.WINDOW_MODAL);
        alert.setOverlayClose(true);
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label("Enter a search name"));
        layout.setBody(field);
        JFXButton closeButton = new JFXButton("Accept");
        closeButton.getStyleClass().add("dialog-accept");

        closeButton.setOnAction(e -> alert.hideWithAnimation());

        layout.setActions(closeButton);

        alert.setContent(layout);
        JMetro jMetro = getJMetroStyled();

        //jMetro.setScene(alert.getDialogPane().getScene());
        alert.showAndWait();
        return field.getText().isEmpty() ? null : field.getText();
    }
}
