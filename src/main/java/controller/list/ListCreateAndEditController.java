package controller.list;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXToggleButton;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.list.ProductListDTO;
import model.restriction.PlatformRestriction;
import model.restriction.ProductTypeRestriction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.ToggleSwitch;

import java.net.URL;
import java.util.*;

public class ListCreateAndEditController implements Initializable {

    @FXML
    private JFXCheckBox grd;
    @FXML
    private JFXCheckBox slc;
    @FXML
    private JFXCheckBox ocn;
    @FXML
    private JFXCheckBox sC;
    @FXML
    private JFXCheckBox sAp;
    @FXML
    private JFXCheckBox sA;
    @FXML
    private ToggleSwitch s1;
    @FXML
    private ToggleSwitch s2;
    @FXML
    private GridPane restrictions;
    @FXML
    private ToggleSwitch restrictionSwitch;
    @FXML
    private BorderPane root;
    @FXML
    private TextField name;
    @FXML
    private TextField description;
    @FXML
    private Button create;
    @FXML
    private JFXToggleButton toggle;

    private ProductListDTO productListDTO;
    SimpleStringProperty grdStringProperty = new SimpleStringProperty();
    SimpleStringProperty slcStringProperty = new SimpleStringProperty();
    SimpleStringProperty ocnStringProperty = new SimpleStringProperty();
    SimpleStringProperty sCStringProperty = new SimpleStringProperty();
    SimpleStringProperty sAStringProperty = new SimpleStringProperty();
    SimpleStringProperty sApStringProperty = new SimpleStringProperty();
    SimpleBooleanProperty sentinel1AllProducts = new SimpleBooleanProperty();
    SimpleBooleanProperty sentinel2ALlProducts = new SimpleBooleanProperty();
    List<SimpleStringProperty> productTypes;
    private boolean listWasCreated = false;
    private Map<String,JFXCheckBox> checkboxMap;
    private Map<String,ToggleSwitch> toggleSwitchMap;

    static final Logger logger = LogManager.getLogger(ListCreateAndEditController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        logger.atInfo().log("CreateListView initiated...");

        productTypes = new ArrayList<>();
        productTypes.add(grdStringProperty);
        productTypes.add(slcStringProperty);
        productTypes.add(ocnStringProperty);
        productTypes.add(sCStringProperty);
        productTypes.add(sAStringProperty);
        productTypes.add(sApStringProperty);

        checkboxMap = new HashMap<>();
        toggleSwitchMap = new HashMap<>();

        checkboxMap.put("GRD",grd);
        checkboxMap.put("SLC",slc);
        checkboxMap.put("OCN",ocn);
        checkboxMap.put("S2MSI1C",sC);
        checkboxMap.put("S2MSI2A",sA);
        checkboxMap.put("S2MSI2Ap",sAp);

        toggleSwitchMap.put("Sentinel-1",s1);
        toggleSwitchMap.put("Sentinel-2",s2);

        create.setOnAction(e->{
            listWasCreated = true;
            Stage stage = (Stage) root.getScene().getWindow();
            stage.close();
        });

        create.disableProperty().bind(Bindings.isEmpty(name.textProperty()));

        restrictions.disableProperty().bind(restrictionSwitch.selectedProperty().not());

        bindSentinel1Property();

        bindSentinel2Property();

        setCheckboxes();
    }

    public boolean isListWasCreated() {
        return listWasCreated;
    }

    public void setProductList(ProductListDTO productList) {
        productListDTO = productList;
        name.setText(productListDTO.getName());
        description.setText(productListDTO.getDescription());

        if (productListDTO.getRestrictions().size() > 0) {
            restrictionSwitch.setSelected(true);
            productListDTO.getRestrictions().forEach(restriction -> {
                if (restriction.getValues().size()>0)
                    restriction.getValues().forEach(this::activateCheckboxOrToggleSwitch);
            });
        }
    }

    private void activateCheckboxOrToggleSwitch(String value) {
        if (checkboxMap.getOrDefault(value, null) == null)
            toggleSwitchMap.get(value).setSelected(true);
        else {
            checkboxMap.get(value).setSelected(true);
        }

    }

    private void setCheckboxes() {

        sentinel1AllProducts.bind(
                Bindings.when(isOneSelected(grd, slc, ocn))
                        .then(false)
                        .otherwise(true));

        sentinel2ALlProducts.bind(
                Bindings.when(isOneSelected(sC, sA, sAp))
                        .then(false)
                        .otherwise(true));
    }

    private BooleanBinding isOneSelected(JFXCheckBox sC, JFXCheckBox sA, JFXCheckBox sAp) {
        return sC.selectedProperty()
                .or(sA.selectedProperty()
                        .or(sAp.selectedProperty()));
    }

    private void bindSentinel2Property() {
        bindDisableProperty(sC,s2);
        bindDisableProperty(sA,s2);
        bindDisableProperty(sAp,s2);
        bind(sCStringProperty,sC,"S2MSI1C");
        bind(sAStringProperty,sA,"S2MSI2A");
        bind(sApStringProperty,sAp,"S2MSI2Ap");
  }

    private void bindSentinel1Property() {
        bindDisableProperty(grd,s1);
        bindDisableProperty(slc,s1);
        bindDisableProperty(ocn,s1);
        bind(grdStringProperty,grd,"GRD");
        bind(slcStringProperty,slc,"SLC");
        bind(ocnStringProperty,ocn,"OCN");
    }

    private void bindDisableProperty(JFXCheckBox checkBox, ToggleSwitch product) {
        checkBox.disableProperty().bind(product.selectedProperty().not());
    }

    private void bind(StringProperty stringProperty, JFXCheckBox checkBox, String value) {
        stringProperty.bind(Bindings.when(checkBox.selectedProperty()).then(value).otherwise(""));
    }

    public ProductListDTO getProductList() {
        if (!listWasCreated) return null;

        productListDTO.setName(name.getText());
        productListDTO.setDescription(description.getText());

        if (restrictionSwitch.isSelected()) {
            createRestrictions();
        } else {
            productListDTO.getRestrictions().clear();
        }
        logger.atInfo().log("New product list ({}) created",productListDTO.getName());
        return productListDTO;
    }

    private void createRestrictions() {
        productListDTO.getRestrictions().clear();
        if ((sentinel1AllProducts.get() && s1.isSelected()) || (sentinel2ALlProducts.get() && s2.isSelected())) {
            createPlatformRestriction();
        } else {
            createProductTypeRestriction();
        }
    }

    private void createProductTypeRestriction() {
        ProductTypeRestriction type = new ProductTypeRestriction();
        productTypes.forEach(s->{
            if (!s.get().isEmpty()) {
                type.add(s.get());
            }
        });
        productListDTO.addRestriction(type);
    }

    private void createPlatformRestriction() {
        PlatformRestriction platFormRestriction = new PlatformRestriction();
        if (sentinel1AllProducts.get() && s1.isSelected()) {
            platFormRestriction.add("Sentinel-1");
        }

        if (sentinel2ALlProducts.get() && s2.isSelected()) {
            platFormRestriction.add("Sentinel-2");
        }

        productListDTO.addRestriction(platFormRestriction);
    }
}
