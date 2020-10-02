package controller.list;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXToggleButton;
import controller.TabItem;
import gui.components.TabPaneComponent;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.ProductList;
import model.products.Product;
import model.restriction.PlatFormRestriction;
import model.restriction.ProductTypeRestriction;
import org.controlsfx.control.ToggleSwitch;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CreateListController implements Initializable {

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
    private ToggleSwitch activate;
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

    private ProductList productList;
    SimpleStringProperty grdStringProperty = new SimpleStringProperty();
    SimpleStringProperty slcStringProperty = new SimpleStringProperty();
    SimpleStringProperty ocnStringProperty = new SimpleStringProperty();
    SimpleStringProperty sCStringProperty = new SimpleStringProperty();
    SimpleStringProperty sAStringProperty = new SimpleStringProperty();
    SimpleStringProperty sApStringProperty = new SimpleStringProperty();
    SimpleBooleanProperty sentinel1Property = new SimpleBooleanProperty();
    SimpleBooleanProperty sentinel2Property = new SimpleBooleanProperty();
    List<SimpleStringProperty> list;
    private boolean created = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        productList = new ProductList(new SimpleStringProperty(),new SimpleStringProperty());
        productList.nameProperty().bindBidirectional(name.textProperty());
        productList.descriptionProperty().bindBidirectional(description.textProperty());
        list = new ArrayList<>();
        list.add(grdStringProperty);
        list.add(slcStringProperty);
        list.add(ocnStringProperty);
        list.add(sCStringProperty);
        list.add(sAStringProperty);
        list.add(sApStringProperty);
        create.setOnAction(e->{
            created = true;
            Stage stage = (Stage) root.getScene().getWindow();
            stage.close();
        });

        create.disableProperty().bind(
                Bindings.isEmpty(name.textProperty()));

        restrictions.disableProperty().bind(activate.selectedProperty().not());

        setCheckboxes();

        grdStringProperty.addListener(((observable,oldValue,newValue) -> {
            System.out.println(newValue);
        }));
    }

    private void setCheckboxes() {
        sentinel1();
        sentinel2();
        sentinel1Property.bind(
                Bindings.when(
                        grd.selectedProperty()
                                .or(slc.selectedProperty()
                                        .or(ocn.selectedProperty())))
                        .then(false)
                        .otherwise(true));
        sentinel2Property.bind(
                Bindings.when(
                        sC.selectedProperty()
                                .or(sA.selectedProperty()
                                        .or(sAp.selectedProperty())))
                        .then(false)
                        .otherwise(true));
    }

    private void sentinel2() {
        sC.disableProperty().bind(s2.selectedProperty().not());
        sCStringProperty.bind(Bindings.when(sC.selectedProperty()).then("S2MSI1C").otherwise(""));
        sA.disableProperty().bind(s2.selectedProperty().not());
        sAStringProperty.bind(Bindings.when(sA.selectedProperty()).then("S2MSI2A").otherwise(""));
        sAp.disableProperty().bind(s2.selectedProperty().not());
        sApStringProperty.bind(Bindings.when(sAp.selectedProperty()).then("S2MSI2Ap").otherwise(""));
    }

    private void sentinel1() {
        grd.disableProperty().bind(s1.selectedProperty().not());
        grdStringProperty.bind(Bindings.when(grd.selectedProperty()).then("GRD").otherwise(""));
        slc.disableProperty().bind(s1.selectedProperty().not());
        slcStringProperty.bind(Bindings.when(slc.selectedProperty()).then("SLC").otherwise(""));
        ocn.disableProperty().bind(s1.selectedProperty().not());
        ocnStringProperty.bind(Bindings.when(ocn.selectedProperty()).then("OCN").otherwise(""));
    }

    public ProductList getProductList() {
        if (!created) return null;

        if (activate.isSelected()) {
            setData();
        }
        return productList;

    }

    private void setData() {
        if ((sentinel1Property.get() && s1.isSelected()) || (sentinel2Property.get() && s2.isSelected())) {
            PlatFormRestriction platFormRestriction = new PlatFormRestriction();
            if (sentinel1Property.get()&& s1.isSelected()) {
                System.out.println("Sen1f");
                platFormRestriction.add("Sentinel-1");
            }

            if (sentinel2Property.get() && s2.isSelected()) {
                System.out.println("Sen2");
                platFormRestriction.add("Sentinel-2");
            }

            productList.addRestriction(platFormRestriction);

        } else {
            ProductTypeRestriction type = new ProductTypeRestriction();
            list.forEach(s->{
                if (!s.get().isEmpty()) {
                    type.add(s.get());
                }
            });

            productList.addRestriction(type);
        }

    }
}
