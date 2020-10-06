package controller.cell;

import controller.GTMapSearchController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import model.list.ProductListDTO;
import model.products.ProductDTO;
import model.products.Sentinel1ProductDTO;
import model.products.Sentinel2ProductDTO;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductListCell extends ListCell<ProductDTO> {
    private final ProductListDTO productListDTO;
    private final GTMapSearchController mapController;
    private FXMLLoader loader;

    @FXML
    private GridPane root;
    @FXML
    private Label title;
    @FXML
    private Label platformName;
    @FXML
    private Label instrumentName;
    @FXML
    private Label size;
    @FXML
    private Label property;
    @FXML
    private Label value;
    @FXML
    private MenuItem remove;
    @FXML
    private MenuItem download;
    @FXML
    private MenuItem add;
    @FXML
    private MenuItem search;
    @FXML
    private Button showFootprint;
    @FXML
    private Button contextButton;
    @FXML
    private ContextMenu contextMenu;

    private ProductDTO product;

    public ProductListCell(ProductListDTO productListDTO, GTMapSearchController controller) {
        this.productListDTO = productListDTO;
        this.mapController = controller;
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
                loader = new FXMLLoader(getClass().getResource("/fxml/ProductListCellView.fxml"));
                loader.setController(this);
                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            initData(product);
            setText(null);
            setGraphic(root);
        }

    }

    private void initData(ProductDTO product) {
        setData(product);
        setButtonIcon();

        onActionInRemoveButtonDeleteSelectedAreaOfWork(product);

        onActionContextButtonShowContextButton();

        onActionShowFootprintButtonShowProductFootprintInMap(product);

        add.setVisible(false);
        search.setVisible(false);

        setAreaOfWorkStyle(product);
    }

    private void onActionShowFootprintButtonShowProductFootprintInMap(ProductDTO product) {
        showFootprint.setOnAction(e->{
            ArrayList<String> id = new ArrayList<>();
            id.add(product.getId());
            mapController.setSelectedFeaturesBorderColor(Color.BLUE,null);
            mapController.setNotSelectedFeaturesBorderColor(Color.BLACK,null);
            mapController.showProductArea(id,"products");
            mapController.setSelectedFeaturesBorderColor(Color.MAGENTA,null);
            mapController.setNotSelectedFeaturesBorderColor(Color.ORANGE,null);
        });
    }

    private void onActionContextButtonShowContextButton() {
        contextButton.setText("");
        contextButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
            contextMenu.show(contextButton,e.getScreenX(),e.getScreenY());
        });
    }

    private void onActionInRemoveButtonDeleteSelectedAreaOfWork(ProductDTO product) {
        remove.setOnAction(e->{
            productListDTO.remove(product);
        });
    }

    private void setButtonIcon() {
        GlyphsDude.setIcon(contextButton,FontAwesomeIcon.BARS);
        GlyphsDude.setIcon(download, FontAwesomeIcon.DOWNLOAD);
        GlyphsDude.setIcon(add, FontAwesomeIcon.PLUS);
        GlyphsDude.setIcon(remove, FontAwesomeIcon.TRASH);
        GlyphsDude.setIcon(search, FontAwesomeIcon.SEARCH);
    }

    private void setAreaOfWorkStyle(ProductDTO product) {
        if (productListDTO.getAreasOfWork().size() == 0) {
            defaultStyle();
            return;
        }
        List<String> area = productListDTO.areasOfWorkOfProduct(product.getFootprint());

        if (area == null)
            defaultStyle();
         else if(area.size() == 0) {
            invalidStyle();
        } else {
            validStyle();
        }
    }

    private void setData(ProductDTO product) {
        setTitle(product);
        set(platformName, product.platformNameProperty());
        set(instrumentName, product.productTypeProperty());
        set(size, product.sizeProperty());
        setVariableLabel(product);
    }

    private void setVariableLabel(ProductDTO product) {
        StringProperty key = new SimpleStringProperty();
        StringProperty value = new SimpleStringProperty();
        property.textProperty().bind(key);
        this.value.textProperty().bind(value);

        if (product.getPlatformName().equals("Sentinel-2")) {
            key.set("Cloud Coverage");
            value.set(((Sentinel2ProductDTO) product).getCloudCoverPercentage()+" %");
        } else if (product.getPlatformName().equals("Sentinel-1")) {
            key.set("Polarisation");
            value.set(((Sentinel1ProductDTO) product).getPolarizationMode()+"");
        } else {
            key.set("");
            value.set("");
        }
    }

    private void set(Label platformName, StringProperty platformName2) {
        platformName.textProperty().bind(platformName2);
    }

    private void setTitle(ProductDTO product) {
        set(title, product.titleProperty());
        Tooltip tooltip = new Tooltip(product.getTitle());
        tooltip.setShowDelay(new Duration(0.2));
        tooltip.setFont(Font.font(10));
        title.setTooltip(tooltip);
    }

    private void invalidStyle() {
        defaultStyle();
        root.getStyleClass().add("workingAreaInvalid");
        showFootprint.setDisable(false);
    }

    private void validStyle() {
        defaultStyle();
        root.getStyleClass().add("workingAreaValid");
    }

    private void defaultStyle() {
        root.getStyleClass().remove("workingAreaInvalid");
        root.getStyleClass().remove("workingAreaValid");
        showFootprint.setDisable(true);
    }

}
