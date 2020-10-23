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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import model.list.ProductListDTO;
import model.products.ProductDTO;
import model.products.Sentinel1ProductDTO;
import model.products.Sentinel2ProductDTO;
import utils.FileUtils;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcessingProductCell extends ListCell<ProductDTO> {
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
    private Label property;
    @FXML
    private Label value;
    @FXML
    private MenuItem remove;
    @FXML
    private Button contextButton;
    @FXML
    private ContextMenu contextMenu;

    @Override
    protected void updateItem(ProductDTO product, boolean empty) {
        super.updateItem(product, empty);

        if(empty || product == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/fxml/ProcessingProductCell.fxml"));
                loader.setController(this);
                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            prefWidthProperty().bind(getListView().prefWidthProperty().subtract(2));
            setMaxWidth(Control.USE_PREF_SIZE);


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
    }

    private void onActionContextButtonShowContextButton() {
        contextButton.setText("");
        contextButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e-> contextMenu.show(contextButton,e.getScreenX(),e.getScreenY()));
    }

    private void onActionInRemoveButtonDeleteSelectedAreaOfWork(ProductDTO product) {
        remove.setOnAction(e-> getListView().getItems().remove(product));
    }

    private void setButtonIcon() {
        GlyphsDude.setIcon(contextButton,FontAwesomeIcon.BARS);
        GlyphsDude.setIcon(remove, FontAwesomeIcon.TRASH);
    }

    private void setData(ProductDTO product) {
        setTitle(product);
        set(platformName, product.platformNameProperty());
        set(instrumentName, product.productTypeProperty());
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
        //tooltip.setShowDelay(new Duration(0.2));
        tooltip.setFont(Font.font(10));
        title.setTooltip(tooltip);
    }

}
