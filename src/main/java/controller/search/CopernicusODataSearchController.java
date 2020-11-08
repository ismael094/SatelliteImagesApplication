package controller.search;

import controller.GTMapSearchController;
import gui.components.TabPaneComponent;
import gui.components.tabcomponent.SatInfTabPaneComponent;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import model.filter.Filter;
import model.filter.filterItems.FilterItemDateTime;
import model.filter.filterItems.FilterItemStartWith;
import model.filter.filterItems.FilterItemSubstringOf;
import model.filter.filterItems.FootPrintFilter;
import model.filter.operators.ComparisonOperators;
import gui.dialog.AddProductToProductListDialog;
import gui.dialog.ProductDialog;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.ProductOData;
import model.list.ProductListDTO;
import model.products.ProductDTO;
import services.search.ODataSearcher;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class CopernicusODataSearchController extends SearchController<ProductDTO> {

    @FXML
    private ImageView image;
    private final ODataSearcher ODataSearcher;
    private final Filter filter;
    private ContextMenu productContextMenu;
    private List<ProductListDTO> productListDTO;
    private AddProductToProductListDialog addProductToProductListDialog;
    @FXML
    private ListView<ProductOData> list;
    @FXML
    private TextArea textArea;

    @FXML
    private ChoiceBox<String> satelliteList;
    @FXML
    public ChoiceBox<String> instrumentList;

    @FXML
    private DatePicker dateStart;

    @FXML
    private DatePicker dateFinish;
    @FXML
    private URL location;

    @FXML
    private ResourceBundle resources;

    @FXML
    public GTMapSearchController GTMapSearchController;

    public CopernicusODataSearchController() {
        ODataSearcher = new ODataSearcher();
        filter = new Filter();
    }

    public void initialize() {
        satelliteList.setItems(FXCollections.observableArrayList(
                "S1", "S2", "S3"));
        satelliteList.setValue("S1");
        instrumentList.setItems(FXCollections.observableArrayList(
                "GRD", "IW"));
        instrumentList.setValue("GRD");
        //list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> textArea.setText(newValue.getInfo()));
        buildContextMenu();
    }

    @Override
    public void search() {
        //if (dateStart.getValue() != null && dateFinish.getValue() != null && dateFinish.getValue().compareTo(dateStart.getValue()) > 0) {
        clearListAndFilter();
        addFilters();
        //System.out.println(model.filter.evaluate());
        //list.getItems().addAll(ODataSearcher.getImages(filter));
        addContextMenu();
    }

    @Override
    public void setSearchParameters(Map<String, String> parametersOfAllResponses) {

    }

    @Override
    public Map<String, String> getSearchParameters() {
        return null;
    }

    @Override
    public void setTabPaneComponent(TabPaneComponent component) {

    }

    @Override
    public Parent getView() {
        return null;
    }

    @Override
    public Task<Parent> start() {
        return null;
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public String getItemId() {
        return null;
    }

    @Override
    public void undo() {

    }

    @Override
    public void redo() {

    }

    @Override
    public String getRedo() {
        return null;
    }

    @Override
    public String getUndo() {
        return null;
    }

    public String getId() {
        return null;
    }

    @Override
    public ObservableList<ProductDTO> getSelectedProducts() {
        return null;
    }

    @Override
    public ObservableList<ProductDTO> getProducts() {
        return null;
    }

    private void clearListAndFilter() {
        list.getItems().clear();
        filter.clear();
    }

    private void addFilters() {
        addNameFilter(satelliteList.getValue());
        addWKTFilter(GTMapSearchController.getWKT());
        addInstrumentFilter(instrumentList.getValue());
        if (rangeIsValid()) {
            addDateRangeLower(dateStart.getValue());
            addDateRangeUpper(dateFinish.getValue());
        }
    }

    private void addInstrumentFilter(String instrument) {
        filter.add(new FilterItemSubstringOf("Name",instrument));
    }

    private void addWKTFilter(String wkt) {
        if (wkt.length()>0)
            filter.add(new FootPrintFilter(wkt));
    }

    private boolean rangeIsValid() {
        return dateStart.getValue() != null && dateFinish.getValue() != null && dateFinish.getValue().compareTo(dateStart.getValue()) > 0;
    }

    private void addDateRangeUpper(LocalDate date) {
        if (date != null)
            filter.add(new FilterItemDateTime("IngestionDate",ComparisonOperators.LE,date.atTime(23,59, 59, 1600)));
    }

    private void addDateRangeLower(LocalDate date) {
        if (date != null)
            filter.add(new FilterItemDateTime("IngestionDate", ComparisonOperators.GE,date.atTime(0,0, 0, 1600)));
    }

    private void addNameFilter(String value) {
        filter.add(new FilterItemStartWith("Name",value));
    }

    private void addContextMenu() {
        list.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ProductOData productOData = getSelectedItem();
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                productContextMenu.show(list, event.getScreenX(), event.getScreenY());
            } else {
                textArea.setText(productOData.getInfo());
                productContextMenu.hide();
            }
        });
    }

    private ProductOData getSelectedItem() {
        return list.getSelectionModel().getSelectedItems().get(0);
    }

    private void buildContextMenu() {
        productContextMenu = new ContextMenu();
        MenuItem replaceCardMenuItem = new MenuItem("Ver producto");
        MenuItem replaceCardMenuItem2 = new MenuItem("Añadir a la lista...");
        replaceCardMenuItem.setOnAction(event -> {
            ProductDialog pd = new ProductDialog(getSelectedItem());
            pd.init();
            pd.show();
        });
        replaceCardMenuItem2.setOnAction(event -> {
            initCreateListDialog();
        });
        productContextMenu.getItems().add(replaceCardMenuItem);
        productContextMenu.getItems().add(replaceCardMenuItem2);
    }

    private void initCreateListDialog() {
        addProductToProductListDialog = new AddProductToProductListDialog(productListDTO);
        addProductToProductListDialog.init();
        addProductToProductListDialog.setOnHidden(ev -> {
            if (addProductToProductListDialog.getSelectedItem() > -1)
            addSelectedProductToProductList(productListDTO.get(addProductToProductListDialog.getSelectedItem()));
        });
        addProductToProductListDialog.show();
    }

    private void addSelectedProductToProductList(ProductListDTO productListDTO) {
        //productList.addProduct(list.getSelectionModel().getSelectedItem());
    }

    public void showProduct(MouseEvent mouseEvent) {
        ProductOData selectedItem = list.getSelectionModel().getSelectedItem();
    }

    public void setProductList(List<ProductListDTO> list) {
        this.productListDTO = list;
    }

    public List<ProductListDTO> getUserProductList() {
        return productListDTO;
    }
}
