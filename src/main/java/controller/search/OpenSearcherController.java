package controller.search;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gui.GTMapSearchController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import model.exception.AuthenticationException;
import model.openSearcher.OpenSearchQueryParameter;
import model.openSearcher.OpenSearchResponse;
import model.products.Product;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.OpenSearcher;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class OpenSearcherController implements Initializable, SearchController {

    public static final String SENTINEL_1 = "Sentinel-1";
    public static final String SENTINEL_2 = "Sentinel-2";

    private OpenSearcher searcher;

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
    private JFXTextField cloudCoverage;
    @FXML
    private JFXDatePicker dateStart;
    @FXML
    private JFXDatePicker dateFinish;
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
    private JFXListView<Product> resultProductsList;
    @FXML
    private AnchorPane mapPane;
    @FXML
    private JFXButton showResults;

    private GTMapSearchController GTMapSearchController;

    static final Logger logger = LogManager.getLogger(OpenSearcherController.class.getName());
    private boolean paginationSetted;

    public void login(String username, String password) throws AuthenticationException {
        searcher = OpenSearcher.getOpenSearcher(username,password);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resultsPane.setVisible(false);
        setSpinnerVisible(false);
        setPaginationVisible(false);
        initRows();
        setSatelliteList();
        setSentinel1Data();
        setProductsList();
        initResultPaneHeader();
        initPaginationEvent();
        search.setOnMouseClicked(e->{
            searcher.setStartProductIndex(0);
            paginationSetted = false;
            search();
        });
        initGTMapController();
        cloudCoverageEvent();
        mapPane.getChildren().addAll(GTMapSearchController);
        //list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> textArea.setText(newValue.getInfo()));
    }

    private void initGTMapController() {
        GTMapSearchController = new GTMapSearchController(mapPane.getPrefWidth(),mapPane.getPrefHeight());
        GTMapSearchController.setOnMouseClicked(event-> {
            Product product = resultProductsList.getItems().stream()
                    .filter(p -> p.getId().equals(GTMapSearchController.getSelectedProduct()))
                    .findFirst()
                    .orElse(null);
            if (product != null) {
                resultProductsList.setFocusTraversable(true);
                resultProductsList.getFocusModel().focus(resultProductsList.getItems().indexOf(product));
                resultProductsList.getSelectionModel().select(resultProductsList.getItems().indexOf(product));
                resultProductsList.scrollTo(product);
            }

        });
    }

    private void cloudCoverageEvent() {
        cloudCoverage.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*"))
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

    private void initPaginationEvent() {
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            searcher.setStartProductIndex(searcher.getProductsPerPage()*(newIndex.intValue()));
            paginationSetted = true;
            search();
        });
    }

    private void initRows() {
        rows.setItems(FXCollections.observableArrayList(
                25,50,75,100));
        rows.setValue(100);
        rows.getSelectionModel()
                .selectedItemProperty()
                .addListener((observableValue, oldValue, newValue) -> {
                    System.out.println(newValue);
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
        resultProductsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        resultProductsList.setOnMouseClicked(event -> {
            Product product = resultProductsList.getSelectionModel().getSelectedItem();
            if (product != null)
                GTMapSearchController.showProductArea(product.getId());
        });
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
        //response.setOnFailed(this::onFailedSearch);
        new Thread(response).start();
    }

    private void getOnSucceedSearchEvent(Task<OpenSearchResponse> response) {
        try {
            ObservableList<Product> tObservableArray =
                    FXCollections.observableArrayList(response.get().getProducts());

            resultProductsList.setItems(tObservableArray);
            resultProductsList.setCellFactory(e -> new ProductResultListCell());
            clearMap();
            OpenSearchResponse openSearchResponse = response.get();
            if (openSearchResponse.getProducts().size() > 0) {
                writeProductsFootprintInMap(openSearchResponse.getProducts());
                if (!paginationSetted)
                    setPagination(openSearchResponse.getNumOfProducts(),openSearchResponse.getProducts().size());
            }

            setSpinnerVisible(false);
            resultsPane.setVisible(true);
            //rootPane.setDisable(false);
        } catch (InterruptedException | ExecutionException e) {
            logger.atLevel(Level.ERROR).log("Error while retrieving search results: {0}",e);
            e.printStackTrace();
        }
    }

    private void clearMap() {
        GTMapSearchController.clearMap();
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
            pagination.setManaged(true);
            pagination.setVisible(true);
            double numPages = Math.ceil((numOfProducts/rows)+0.5f);
            System.out.println(numOfProducts);
            pagination.setPageCount((int)numPages);
        } else {
            //pagination.setManaged(false);
            pagination.setVisible(false);
        }
    }

    private void writeProductsFootprintInMap(List<Product> products) {
        GTMapSearchController.addProductsWKT(products);
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
            searcher.addSearchParameter(OpenSearchQueryParameter.CLOUD_COVER_PERCENTAGE,cloudCoverage.getText());
    }

    private void addSensorModeParameter() {
        if (!sensorMode.getValue().equals("All"))
            searcher.addSearchParameter(OpenSearchQueryParameter.SENSOR_OPERATIONAL_MODE, sensorMode.getValue());
    }

    private void addPolarisationModeParameter() {
        if (!polarisation.getValue().equals("All"))
            searcher.addSearchParameter(OpenSearchQueryParameter.POLARISATION_MODE, polarisation.getValue());
    }

    private void addInstrumentParameter() {
        if (!productTypeList.getValue().equals("All"))
            searcher.addSearchParameter(OpenSearchQueryParameter.PRODUCT_TYPE, productTypeList.getValue());
    }

    private void addWKTParameter() {
        if (GTMapSearchController.getWKT().length()>0)
            searcher.addSearchParameter(OpenSearchQueryParameter.FOOTPRINT,"\"Intersects("+GTMapSearchController.getWKT()+")\"");
    }

    private void addDateRangeFilter() {
        if (dateStart.getValue() != null || dateFinish.getValue() != null)
            searcher.addSearchParameter(OpenSearchQueryParameter.INGESTION_DATE,getFromToIngestionDate(dateStart.getValue(),dateFinish.getValue()));
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
        searcher.addSearchParameter(OpenSearchQueryParameter.PLATFORM_NAME, platformList.getValue());
    }
}
