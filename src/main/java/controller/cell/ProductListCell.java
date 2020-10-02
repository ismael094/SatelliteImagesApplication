package controller.cell;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import model.ProductList;
import model.products.Product;
import model.products.Sentinel1Product;
import model.products.Sentinel2Product;
import org.locationtech.jts.io.ParseException;
import utils.WKTUtil;

import java.io.IOException;
import java.util.Objects;

public class ProductListCell extends ListCell<Product> {
    private final ProductList productList;
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
    private JFXButton details;
    @FXML
    private Button download;

    public ProductListCell(ProductList productList) {
        this.productList = productList;
    }

    @Override
    protected void updateItem(Product product, boolean empty) {
        super.updateItem(product, empty);

        if(empty || product == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/fxml/ProductListCellView.fxml"));
                loader.setController(this);
                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            title.setText(product.getTitle());
            Tooltip tooltip = new Tooltip(product.getTitle());
            tooltip.setShowDelay(new Duration(0.2));
            tooltip.setFont(Font.font(12));
            title.setTooltip(tooltip);
            platformName.setText(product.getPlatformName());
            instrumentName.setText(product.getProductType());
            size.setText(product.getSize());
            SimpleStringProperty key = new SimpleStringProperty();
            SimpleStringProperty value = new SimpleStringProperty();
            property.textProperty().bind(key);
            this.value.textProperty().bind(value);
            if (product.getPlatformName().equals("Sentinel-2")) {
                key.set("Cloud Coverage");
                value.set(((Sentinel2Product)product).getCloudCoverPercentage()+" %");
            } else if (product.getPlatformName().equals("Sentinel-1")) {
                key.set("Polarisation");
                value.set(((Sentinel1Product)product).getPolarizationMode()+"");
            } else {
                key.set("");
                value.set("");
            }

            GlyphsDude.setIcon(download, FontAwesomeIcon.DOWNLOAD);
            GlyphsDude.setIcon(details, FontAwesomeIcon.TRASH);
            details.setOnAction(e->{
                productList.remove(product);
            });


            productList.getAreasOfWork().addListener((MapChangeListener<String,String>) c -> {
                System.out.println(product.getId());
                if (productList.getAreasOfWork().size() == 0) {
                    root.getStyleClass().remove("workingAreaInvalid");
                    root.getStyleClass().remove("workingAreaValid");
                    return;
                }

                String area = productList.getAreaOfWork(product.getId());
                String aDefault = productList.getAreaOfWork("default");
                System.out.println(area + " - " + aDefault);
                if (area == null && aDefault == null) {
                    root.getStyleClass().add("workingAreaInvalid");
                } else {
                    String areaTest;
                    if (area == null)
                        areaTest = aDefault;
                    else
                        areaTest = area;
                    try {
                        if (WKTUtil.workingAreaContains(product.getFootprint(),areaTest)) {
                            root.getStyleClass().remove("workingAreaInvalid");
                            root.getStyleClass().add("workingAreaValid");
                        } else {
                            root.getStyleClass().add("workingAreaInvalid");
                            root.getStyleClass().remove("workingAreaValid");
                        }

                    } catch (ParseException e) {
                        System.out.println("errrpr");
                    }
                }

            });

            details.setText("");
            setText(null);
            setGraphic(root);
        }

    }

}
