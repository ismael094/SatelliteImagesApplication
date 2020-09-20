package controller;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gui.GTMapSearchController;
import gui.ProductResultListCell;
import gui.dialog.AddProductToProductListDialog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import model.ProductList;
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

public class OpenSearcherController implements Initializable {

    public static final String SENTINEL_1 = "Sentinel-1";
    public static final String SENTINEL_2 = "Sentinel-2";
    public static final String SENTINEL_3 = "Sentinel-3";


    private OpenSearcher searcher;
    private ContextMenu productContextMenu;
    private List<ProductList> productList;
    private AddProductToProductListDialog addProductToProductListDialog;

    @FXML
    private AnchorPane rootPane;
    @FXML
    private Pane searchParameters;
    @FXML
    private ImageView image;
    @FXML
    private JFXSpinner spinnerWait;

    @FXML
    private TextArea textArea;
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
    private Pane dateStartPane;
    @FXML
    private JFXDatePicker dateFinish;
    @FXML
    private URL location;
    @FXML
    private ResourceBundle resources;

    @FXML
    private Pane resultsPane;
    @FXML
    private HBox resultPaneHeader;
    @FXML
    private JFXListView<Product> resultProductsList;

    @FXML
    private Pane mapPane;
    private GTMapSearchController GTMapSearchController;

    public void login(String username, String password) throws AuthenticationException {
        searcher = OpenSearcher.getOpenSearcher(username,password);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resultsPane.setVisible(false);
        spinnerWait.setManaged(false);
        setSatelliteList();
        setSentinel1Data();
        setProductsList();
        initResultPaneHeader();
        GTMapSearchController = new GTMapSearchController(mapPane.getPrefWidth(),mapPane.getPrefHeight());
        GTMapSearchController.setOnMouseClicked(event-> {
            Product product = resultProductsList.getItems().stream()
                    .filter(p -> p.getId().equals(GTMapSearchController.getSelectedProduct()))
                    .findFirst()
                    .orElse(null);
            if (product != null) {
                resultProductsList.setFocusTraversable(true);
                resultProductsList.getFocusModel().focus(resultProductsList.getItems().indexOf(product));
                resultProductsList.scrollTo(product);
                resultProductsList.getSelectionModel().select(resultProductsList.getItems().indexOf(product));
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
        buildContextMenu();
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
        });
        resultPaneHeader.getChildren().add(iconButton);
    }

    private void setSentinel2Instruments() {
        instrumentList.setItems(FXCollections.observableArrayList(
                "S2MSI1C", "S2MSI2A","S2MSI2Ap"));
        instrumentList.setValue("S2MSI1C");
    }

    private void setSentinel1Instruments() {
        instrumentList.setItems(FXCollections.observableArrayList(
                "GRD", "OCN","SLC"));
        instrumentList.setValue("GRD");
    }

    public void search(ActionEvent actionEvent) {
        clearListAndFilter();
        addParameters();
        clearMap();
        spinnerWait.setVisible(true);
        spinnerWait.setManaged(true);
        rootPane.setDisable(true);
        Task<OpenSearchResponse> response = getSearchTask();
        response.setOnSucceeded(event -> getOnSucceedSearchEvent(response));
        new Thread(response).start();
        addContextMenu();
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
            if (response.get().getProducts().size() > 0)
                writeProductsFootprintInMap(response.get().getProducts());
            spinnerWait.setVisible(false);
            spinnerWait.setManaged(false);
            resultsPane.setVisible(true);
            rootPane.setDisable(false);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void writeProductsFootprintInMap(List<Product> products) {
        GTMapSearchController.addProductsWKT(products);
        /*
        products.forEach(p-> {
            try {
                mapGUI.addProductWKT(p.getFootprint(),p.getId());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });*/

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

        /*if (rangeIsValid()) {
            addDateRangeFilter(dateStart.getValue(),dateFinish.getValue());
        }*/
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
        searcher.addSearchParameter(OpenSearchQueryParameter.PRODUCT_TYPE,instrument);
    }

    private void addWKTParameter(String wkt) {
        if (wkt.length()>0)
            searcher.addSearchParameter(OpenSearchQueryParameter.FOOTPRINT,"\"Intersects("+wkt+")\"");
    }

    private boolean rangeIsValid() {
        return dateStart.getValue() != null && dateFinish.getValue() != null && dateFinish.getValue().compareTo(dateStart.getValue()) > 0;
    }

    private void addDateRangeFilter(LocalDate start, LocalDate finish) {
        if (start != null || finish != null)
            searcher.addSearchParameter(OpenSearchQueryParameter.INGESTION_DATE,getFromToIngestionDate(start,finish));
    }

    private String getFromToIngestionDate(LocalDate start, LocalDate finish) {
        String startS,endS = "";
        if (start == null)
            startS = "*";
        else {
            LocalDateTime localDateTimeStart = start.atTime(0, 0, 0, 0);
            localDateTimeStart.atZone(ZoneId.of("UTC"));
            startS = localDateTimeStart.toString()+":00.001Z";
        }
        if (finish == null)
            endS = "NOW";
        else {
            LocalDateTime localDateTimeFinish = finish.atTime(23, 59, 59, 0);
            localDateTimeFinish.atZone(ZoneId.of("UTC"));
            endS = localDateTimeFinish.toString()+".999Z";
        }

        return "["+ startS+ " TO " + endS + "]";
    }

    private void addPlatformNameParameter(String value) {
        searcher.addSearchParameter(OpenSearchQueryParameter.PLATFORM_NAME,value);
    }

    private void addContextMenu() {
        resultProductsList.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            /*ProductOData productOData = getSelectedItem();
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                productContextMenu.show(list, event.getScreenX(), event.getScreenY());
            } else {
                textArea.setText(productOData.getInfo());
                productContextMenu.hide();
            }*/
        });
    }

    private Product getSelectedItem() {
        return resultProductsList.getSelectionModel().getSelectedItems().get(0);
    }

    private void buildContextMenu() {
        productContextMenu = new ContextMenu();
        MenuItem replaceCardMenuItem = new MenuItem("Ver producto");
        MenuItem replaceCardMenuItem2 = new MenuItem("Añadir a la lista...");
        replaceCardMenuItem.setOnAction(event -> {
            /*ProductDialog pd = new ProductDialog(getSelectedItem());
            pd.init();
            pd.show();*/
        });
        replaceCardMenuItem2.setOnAction(event -> {
            initCreateListDialog();
        });
        productContextMenu.getItems().add(replaceCardMenuItem);
        productContextMenu.getItems().add(replaceCardMenuItem2);
    }

    private void initCreateListDialog() {
        addProductToProductListDialog = new AddProductToProductListDialog(productList);
        addProductToProductListDialog.init();
        addProductToProductListDialog.setOnHidden(ev -> {
            //if (addProductToProductListDialog.getSelectedItem() > -1)
                //addSelectedProductToProductList(productList.get(addProductToProductListDialog.getSelectedItem()));
        });
        addProductToProductListDialog.show();
    }

    /*private void addSelectedProductToProductList(ProductList productList) {
        productList.addProduct(list.getSelectionModel().getSelectedItem());
    }

    public void showProduct(MouseEvent mouseEvent) {
        ProductOData selectedItem = list.getSelectionModel().getSelectedItem();
    }*/

    /*public void setProductList(List<ProductList> list) {
        this.productList = list;
    }

    public List<ProductList> getUserProductList() {
        return productList;
    }*/
}
