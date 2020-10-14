package controller.cell;

import com.jfoenix.controls.JFXButton;
import controller.interfaces.TabItem;
import controller.search.CopernicusOpenSearchController;
import controller.search.CopernicusProductDetailsController_;
import gui.components.TabPaneComponent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import model.openSearcher.SentinelProductParameters;
import model.products.ProductDTO;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProductResultListCell extends ListCell<ProductDTO> {
    private final TabPaneComponent tabPaneComponent;
    private FXMLLoader loader;

    @FXML
    private GridPane root;
    @FXML
    private Label title;
    @FXML
    private ImageView image;
    @FXML
    private Label platformName;
    @FXML
    private Label instrumentName;
    @FXML
    private Label size;
    @FXML
    private JFXButton details;
    @FXML
    private JFXButton opposite;
    private ProductDTO product;

    public ProductResultListCell(TabPaneComponent component) {
        this.tabPaneComponent = component;
    }

    @Override
    protected void updateItem(ProductDTO product, boolean empty) {
        super.updateItem(product, empty);

        if(empty || product == null) {

            setText(null);
            setGraphic(null);

        } else {
            this.product = product;
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/fxml/ProductResultListCell.fxml"));
                loader.setController(this);

                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            prefWidthProperty().bind(getListView().prefWidthProperty().subtract(2));
            setMaxWidth(Control.USE_PREF_SIZE);

            title.textProperty().bind(product.titleProperty());
            Tooltip tooltip = new Tooltip(product.getTitle());
            //tooltip.setShowDelay(new Duration(0.2));
            tooltip.setFont(Font.font(10));
            title.setTooltip(tooltip);
            platformName.setText(product.getPlatformName());
            instrumentName.setText(product.getProductType());
            size.setText(product.getSize());
            details.setOnMouseClicked(e-> detailsEvent(product));
            setText(null);
            setGraphic(root);

            opposite.setOnAction(e->{
                TabItem copernicusOpenSearch = tabPaneComponent.getControllerOf("Copernicus Open Search");
                CopernicusOpenSearchController controller = (CopernicusOpenSearchController)copernicusOpenSearch;
                Map<String, String> parameters = getParameters();
                controller.setParameters(parameters);
                controller.search();
            });

        }

    }

    public void detailsEvent(ProductDTO product) {
        CopernicusProductDetailsController_ copernicusPDC = new CopernicusProductDetailsController_(product);
        tabPaneComponent.load(copernicusPDC);
    }

    private Map<String,String> getParameters() {
        if (product.getPlatformName().equals("Sentinel-1"))
            return sentinel2Parameters();
        return sentinel1Parameters();
    }

    private Map<String,String> sentinel1Parameters() {
        Map<String,String> map = new HashMap<>();
        map.put(SentinelProductParameters.PLATFORM_NAME.getParameterName(),"Sentinel-1");
        map.put(SentinelProductParameters.PRODUCT_TYPE.getParameterName(),"GRD");
        map.put(SentinelProductParameters.FOOTPRINT.getParameterName(),product.getFootprint());
        return map;
    }

    private Map<String,String> sentinel2Parameters() {
        Map<String,String> map = new HashMap<>();
        map.put(SentinelProductParameters.PLATFORM_NAME.getParameterName(),"Sentinel-2");
        map.put(SentinelProductParameters.CLOUD_COVER_PERCENTAGE.getParameterName()+"_from", "0");
        map.put(SentinelProductParameters.CLOUD_COVER_PERCENTAGE.getParameterName()+"_to", "10");
        map.put(SentinelProductParameters.FOOTPRINT.getParameterName(),product.getFootprint());
        return map;
    }

}
