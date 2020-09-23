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
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import model.exception.AuthenticationException;
import model.openSearcher.OpenSearchQueryParameter;
import model.openSearcher.OpenSearchResponse;
import model.products.Product;
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
    private Label searcherTitle;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private AnchorPane spinnerPane;
    @FXML
    private JFXSpinner spinnerWait;
    @FXML
    private ChoiceBox<String> satelliteList;
    @FXML
    private ChoiceBox<String> instrumentList;
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
    private Pane mapPane;
    @FXML
    private JFXButton showResults;

    private GTMapSearchController GTMapSearchController;

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
            search();
        });
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
        cloudCoverage.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*"))
                cloudCoverage.setText(newValue.replaceAll("[^\\d]", ""));
            else {
                if (Integer.parseInt(newValue) < 0 || Integer.parseInt(newValue) > 100)
                    cloudCoverage.setText(oldValue);
            }
        });
        mapPane.getChildren().addAll(GTMapSearchController);
        //list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> textArea.setText(newValue.getInfo()));
    }

    private void setPaginationVisible(boolean b) {
        pagination.setVisible(b);
        pagination.setManaged(b);
    }

    private void setSpinnerVisible(boolean b) {
        spinnerPane.setVisible(b);
        spinnerPane.setManaged(b);
        spinnerWait.setVisible(b);
        spinnerWait.setManaged(b);
    }

    private void initPaginationEvent() {
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            searcher.setStartProductIndex(searcher.getProductsPerPage()*(newIndex.intValue()));
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
        satelliteList.setItems(FXCollections.observableArrayList(
                SENTINEL_1, SENTINEL_2));

        satelliteList.getSelectionModel()
                .selectedIndexProperty()
                .addListener((observableValue, oldValue, newValue) -> {
                        if (satelliteList.getItems().get(newValue.intValue()).equals(SENTINEL_1)) {
                            setSentinel1Instruments();
                            setCloudCoverageDisabled();
                            setSentinel1ParametersEnabled();
                        } else if (satelliteList.getItems().get(newValue.intValue()).equals(SENTINEL_2)) {
                            setSentinel2Instruments();
                            setSentinel1ParametersDisabled();
                            setCloudCoverageEnabled();
                        }

                    }
                );
        satelliteList.setValue(SENTINEL_1);

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

    private void setSentinel2Instruments() {
        instrumentList.setItems(FXCollections.observableArrayList(
                "All","S2MSI1C", "S2MSI2A","S2MSI2Ap"));
        instrumentList.setValue("All");
    }

    private void setSentinel1Instruments() {
        instrumentList.setItems(FXCollections.observableArrayList(
                "All","GRD", "OCN","SLC"));
        instrumentList.setValue("All");
    }

    @Override
    public void search() {
        clearListAndFilter();
        addParameters();
        clearMap();
        setSpinnerVisible(true);
        rootPane.setDisable(true);
        Task<OpenSearchResponse> response = getSearchTask();
        response.setOnSucceeded(event -> getOnSucceedSearchEvent(response));
        new Thread(response).start();
    }

    private void clearMap() {
        GTMapSearchController.clearMap();
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
                setPagination(openSearchResponse.getNumOfProducts(),openSearchResponse.getProducts().size());
            }

            spinnerWait.setVisible(false);
            spinnerWait.setManaged(false);
            resultsPane.setVisible(true);
            rootPane.setDisable(false);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void setPagination(int numOfProducts, int rows) {
        if (numOfProducts > rows) {
            pagination.setManaged(true);
            pagination.setVisible(true);
            pagination.setPageCount((int)Math.ceil(numOfProducts/rows));
        } else {
            pagination.setManaged(false);
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
        addPlatformNameParameter(satelliteList.getValue());
        addWKTParameter(GTMapSearchController.getWKT());
        addInstrumentParameter(instrumentList.getValue());
        addDateRangeFilter(dateStart.getValue(),dateFinish.getValue());
        if (satelliteList.getValue().equals(SENTINEL_1)) {
            addPolarisationModeParameter(polarisation.getValue());
            addSensorModeParameter(sensorMode.getValue());
        } else if (satelliteList.getValue().equals(SENTINEL_2)) {
            addCloudCoverageParameter(cloudCoverage.getText());
        }
    }

    private void addCloudCoverageParameter(String cloudCoverage) {
        if (!cloudCoverage.isEmpty())
            searcher.addSearchParameter(OpenSearchQueryParameter.CLOUD_COVER_PERCENTAGE,cloudCoverage);
    }

    private void addSensorModeParameter(String sensorMode) {
        if (!sensorMode.equals("All"))
            searcher.addSearchParameter(OpenSearchQueryParameter.SENSOR_OPERATIONAL_MODE, sensorMode);
    }

    private void addPolarisationModeParameter(String polarisation) {
        if (!polarisation.equals("All"))
            searcher.addSearchParameter(OpenSearchQueryParameter.POLARISATION_MODE, polarisation);
    }

    private void addInstrumentParameter(String instrument) {
        if (!instrument.equals("All"))
            searcher.addSearchParameter(OpenSearchQueryParameter.PRODUCT_TYPE,instrument);
    }

    private void addWKTParameter(String wkt) {
        if (wkt.length()>0)
            searcher.addSearchParameter(OpenSearchQueryParameter.FOOTPRINT,"\"Intersects("+wkt+")\"");
    }

    private void addDateRangeFilter(LocalDate start, LocalDate finish) {
        if (start != null || finish != null)
            searcher.addSearchParameter(OpenSearchQueryParameter.INGESTION_DATE,getFromToIngestionDate(start,finish));
    }

    private String getFromToIngestionDate(LocalDate start, LocalDate finish) {
        String startS = getDateString(start,"*",0,0,0,"001");
        String endS = getDateString(finish,"NOW",23,59,59,"999");
        return "["+ startS+ " TO " + endS + "]";
    }

    private String getDateString(LocalDate finish, String dateNull, int hour, int minute, int seconds, String nano) {
        if (finish == null)
            return dateNull;

        LocalDateTime localDateTimeFinish = finish.atTime(hour, minute, seconds, 0);
        localDateTimeFinish.atZone(ZoneId.of("UTC"));
        return localDateTimeFinish.toString()+"."+nano+"Z";
    }

    private void addPlatformNameParameter(String value) {
        searcher.addSearchParameter(OpenSearchQueryParameter.PLATFORM_NAME,value);
    }
}
