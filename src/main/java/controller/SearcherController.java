package controller;

import model.filter.Filter;
import model.filter.filterItems.FilterItemDateTime;
import model.filter.filterItems.FilterItemStartWith;
import model.filter.operators.ComparisonOperators;
import gui.dialog.AddProductToProductListDialog;
import gui.dialog.ProductDialog;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.Product;
import model.ProductList;
import services.Searcher;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class SearcherController {
    @FXML
    private ImageView image;
    private Searcher searcher;
    private Filter filter;
    private ContextMenu productContextMenu;
    private List<ProductList> productList;
    private AddProductToProductListDialog addProductToProductListDialog;
    @FXML
    private ListView<Product> list;
    @FXML
    private TextArea textArea;

    @FXML
    private ChoiceBox<String> sateliteList;

    @FXML
    private DatePicker dateStart;

    @FXML
    private DatePicker dateFinish;
    @FXML
    private URL location;

    @FXML
    private ResourceBundle resources;

    public SearcherController() {
        searcher = new Searcher();
        filter = new Filter();
    }

    public void initialize() {
        sateliteList.setItems(FXCollections.observableArrayList(
                "S1", "S2", "S3"));
        sateliteList.setValue("S1");
        //list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> textArea.setText(newValue.getInfo()));
        buildContextMenu();
    }
    public void search(ActionEvent actionEvent) {
        //if (dateStart.getValue() != null && dateFinish.getValue() != null && dateFinish.getValue().compareTo(dateStart.getValue()) > 0) {
        clearListAndFilter();
        addFilters();
        //System.out.println(model.filter.evaluate());
        list.getItems().addAll(searcher.getImages(filter));
        addContextMenu();
    }

    private void clearListAndFilter() {
        list.getItems().clear();
        filter.clear();
    }

    private void addFilters() {
        addNameFilter(sateliteList.getValue());
        if (rangeIsValid()) {
            addDateRangeLower(dateStart.getValue());
            addDateRangeUpper(dateFinish.getValue());
        }
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
            Product product = getSelectedItem();
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                productContextMenu.show(list, event.getScreenX(), event.getScreenY());
            } else {
                textArea.setText(product.getInfo());
                productContextMenu.hide();
            }
        });
    }

    private Product getSelectedItem() {
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
        addProductToProductListDialog = new AddProductToProductListDialog(productList);
        addProductToProductListDialog.init();
        addProductToProductListDialog.setOnHidden(ev -> {
            if (addProductToProductListDialog.getSelectedItem() > -1)
            addSelectedProductToProductList(productList.get(addProductToProductListDialog.getSelectedItem()));
        });
        addProductToProductListDialog.show();
    }

    private void addSelectedProductToProductList(ProductList productList) {
        productList.addProduct(list.getSelectionModel().getSelectedItem());
    }

    public void showProduct(MouseEvent mouseEvent) {
        Product selectedItem = list.getSelectionModel().getSelectedItem();
    }

    public void setProductList(List<ProductList> list) {
        this.productList = list;
    }

    public List<ProductList> getUserProductList() {
        return productList;
    }
}
