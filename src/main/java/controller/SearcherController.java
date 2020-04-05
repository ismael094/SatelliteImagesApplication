package controller;

import filter.Filter;
import filter.filterItems.FilterItemDateTime;
import filter.filterItems.FilterItemStartWith;
import filter.operators.ComparisonOperators;
import gui.dialog.CreateListDialog;
import gui.dialog.ProductDialog;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Product;
import model.ProductList;
import services.Searcher;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class SearcherController {
    @FXML
    public ImageView image;
    private Searcher searcher;
    private Filter filter;
    private ContextMenu productContextMenu;
    private ListView<ProductList> productList;
    @FXML
    public ListView<Product> list;
    @FXML
    public TextArea textArea;

    @FXML
    public ChoiceBox<String> sateliteList;

    @FXML
    public DatePicker dateStart;

    @FXML
    public DatePicker dateFinish;
    @FXML
    public URL location;

    @FXML
    public ResourceBundle resources;

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
        //System.out.println(filter.evaluate());
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
            CreateListDialog pd = new CreateListDialog(productList);
            pd.init();
            pd.show();
        });
        productContextMenu.getItems().add(replaceCardMenuItem);
        productContextMenu.getItems().add(replaceCardMenuItem2);
    }

    public void showProduct(MouseEvent mouseEvent) {
        Product selectedItem = list.getSelectionModel().getSelectedItem();

    }

    public void setProductList(ListView<ProductList> list) {
        this.productList = list;
    }
}
