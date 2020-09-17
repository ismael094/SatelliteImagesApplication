package controller;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSpinner;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gui.MapGUI;
import gui.ProductResultListCell;
import gui.dialog.AddProductToProductListDialog;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
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
import model.exception.NotAuthenticatedException;
import model.openSearcher.OpenSearchQueryParameter;
import model.openSearcher.OpenSearchResponse;
import model.products.Product;
import org.locationtech.jts.io.ParseException;
import services.OpenSearcher;

import java.io.IOException;
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
    public ChoiceBox<String> instrumentList;
    @FXML
    private JFXDatePicker dateStart;
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
    private MapGUI mapGUI;

    public void login(String username, String password) throws AuthenticationException {
        searcher = OpenSearcher.getOpenSearcher(username,password);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        satelliteList.setItems(FXCollections.observableArrayList(
                SENTINEL_1, SENTINEL_2, SENTINEL_3));

        resultsPane.setVisible(false);
        satelliteList.getSelectionModel()
                .selectedIndexProperty()
                .addListener((observableValue, oldValue, newValue) -> {
                        if (newValue.equals(SENTINEL_1)) {
                            setSentinel1Instruments();
                        } else if (newValue.equals(SENTINEL_2)) {
                            setSentinel2Instruments();
                        }

                    }
                );

        resultProductsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        resultProductsList.setOnMouseClicked(event -> {
            Product product = resultProductsList.getSelectionModel().getSelectedItem();
            try {
                mapGUI.drawProductWKT(product.getFootprint());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        initResultPaneHeader();
        satelliteList.setValue(SENTINEL_1);
        setSentinel1Instruments();
        mapGUI = new MapGUI(mapPane.getPrefWidth(),mapPane.getPrefHeight());
        mapPane.getChildren().addAll(mapGUI);
        //list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> textArea.setText(newValue.getInfo()));
        buildContextMenu();
    }

    private void initResultPaneHeader() {
        Button iconButton = GlyphsDude.createIconButton(FontAwesomeIcon.CLOSE,"");
        iconButton.setAccessibleText("Close results");
        iconButton.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            resultsPane.setVisible(false);
        });
        resultPaneHeader.getChildren().add(iconButton);
    }

    private void setSentinel3Instruments() {
        instrumentList.setItems(FXCollections.observableArrayList(
                "S2MSI1C", "S2MSI2A","S2MSI2Ap"));
        instrumentList.setValue("S2MSI1C");
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

    public void search(ActionEvent actionEvent) throws NotAuthenticatedException, IOException, AuthenticationException {
        clearListAndFilter();
        addParameters();
        spinnerWait.setVisible(true);
        rootPane.setDisable(true);
        Task<OpenSearchResponse> response = new Task<>() {
            @Override
            protected OpenSearchResponse call() throws Exception {
                return searcher.search();
            }
        };

        response.setOnSucceeded(event -> {
            spinnerWait.setVisible(false);
            try {
                ObservableList<Product> tObservableArray =
                        FXCollections.observableArrayList(response.get().getProducts());

                resultProductsList.setItems(tObservableArray);
                resultProductsList.setCellFactory(e -> new ProductResultListCell());

                spinnerWait.setVisible(false);
                resultsPane.setVisible(true);
                rootPane.setDisable(false);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        new Thread(response).start();
        addContextMenu();
    }

    private void clearListAndFilter() {
        resultProductsList.getItems().clear();
        searcher.clearSearchParameters();
    }

    private void addParameters() {
        addNameParameter(satelliteList.getValue());
        addWKTParameter(mapGUI.getWKT());
        addInstrumentParameter(instrumentList.getValue());
        /*if (rangeIsValid()) {
            addDateRangeFilter(dateStart.getValue(),dateFinish.getValue());
        }*/
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
        if (start != null && finish != null)
            searcher.addSearchParameter(OpenSearchQueryParameter.INGESTION_DATE,getFromToIngestionDate(start,finish));
    }

    private String getFromToIngestionDate(LocalDate start, LocalDate finish) {
        LocalDateTime localDateTimeStart = start.atTime(0, 0, 0, 0);
        LocalDateTime localDateTimeFinish = start.atTime(0, 0, 0, 0);
        localDateTimeStart.atZone(ZoneId.of("UTC"));
        localDateTimeFinish.atZone(ZoneId.of("UTC"));
        return "["+ localDateTimeStart.toString() + " TO " + localDateTimeFinish.toString() + "]";
    }

    private void addNameParameter(String value) {
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
