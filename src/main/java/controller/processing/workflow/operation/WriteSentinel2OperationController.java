package controller.processing.workflow.operation;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import model.processing.workflow.operation.Operation;
import org.controlsfx.control.ToggleSwitch;

import java.net.URL;
import java.util.*;

public class WriteSentinel2OperationController implements OperationController, Initializable {
    @FXML
    private ChoiceBox<String> writeFormat;
    @FXML
    private ToggleSwitch generatePng;
    @FXML
    private TextField red;
    @FXML
    private TextField green;
    @FXML
    private TextField blue;
    @FXML
    private ChoiceBox<String> profiles;
    @FXML

    private Map<String, Object> parameters;
    private ObservableList<String> inputBands;
    private Map<String, List<String>> mapProfiles;

    @Override
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
        writeFormat.setValue(String.valueOf(parameters.getOrDefault("formatName","GeoTIFF")));
        generatePng.selectedProperty().set((boolean) parameters.getOrDefault("generatePNG", false));
        profiles.setValue(String.valueOf(parameters.getOrDefault("profile","Some")));
        red.setText(String.valueOf(parameters.getOrDefault("red","B4")));
        green.setText(String.valueOf(parameters.getOrDefault("green","B3")));
        blue.setText(String.valueOf(parameters.getOrDefault("blue","B2")));

    }

    @Override
    public Map<String, Object> getParameters() {
        parameters.clear();
        parameters.put("formatName",writeFormat.getValue());
        if (generatePng.isSelected()) {
            parameters.put("generatePNG",true);
            parameters.put("profile",profiles.getValue());
            parameters.put("red",red.getText());
            parameters.put("green",green.getText());
            parameters.put("blue",blue.getText());
        } else {
            parameters.put("generatePNG",false);
        }
        return parameters;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.parameters = new HashMap<>();
        mapProfiles = new HashMap<>();
        loadMapProfiles();
        setProfiles();
        bindProperties();
        red.setText("B4");
        green.setText("B3");
        blue.setText("B2");

        profiles.setValue("Some1");
        writeFormat.setItems(FXCollections.observableArrayList("GeoTIFF","PolSARPro"));
    }

    private void setProfiles() {
        mapProfiles.forEach((k,v)->{
            profiles.getItems().add(k);
        });

        profiles.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            setRGBValues(mapProfiles.getOrDefault(newValue,Arrays.asList("B4","B3","B2")));
        });

        profiles.setValue("Natural Colors");
    }

    private void setRGBValues(List<String> rgbValues) {
        red.setText(rgbValues.get(0));
        blue.setText(rgbValues.get(1));
        green.setText(rgbValues.get(2));
    }

    private void loadMapProfiles() {
        mapProfiles.put("Natural Colors", Arrays.asList("B4","B3","B2"));
        mapProfiles.put("False Color Infrared", Arrays.asList("B8","B4","B3"));
        mapProfiles.put("False Color Urban", Arrays.asList("B12","B11","B4"));
        mapProfiles.put("Agriculture", Arrays.asList("B11","B8","B2"));
        mapProfiles.put("Atmospheric Penetration", Arrays.asList("B12","B11","B8a"));
        mapProfiles.put("Healthy Vegetation", Arrays.asList("B8","B11","B2"));
        mapProfiles.put("Land/Water", Arrays.asList("B8","B11","B4"));
        mapProfiles.put("Natural Colors with Atmospheric Removal", Arrays.asList("B12","B8","B3"));
        mapProfiles.put("Shortwave Infrared", Arrays.asList("B12","B8","B4"));
        mapProfiles.put("Vegetation Analysis", Arrays.asList("B11","B8","B4"));
    }

    public ToggleSwitch getGeneratePng() {
        return generatePng;
    }

    public TextField getRed() {
        return red;
    }

    public TextField getGreen() {
        return green;
    }

    public TextField getBlue() {
        return blue;
    }

    private void bindProperties() {
        profiles.disableProperty().bind(generatePng.selectedProperty().not());
        red.disableProperty().bind(generatePng.selectedProperty().not());
        green.disableProperty().bind(generatePng.selectedProperty().not());
        blue.disableProperty().bind(generatePng.selectedProperty().not());
    }

    public ChoiceBox<String> getProfile() {
        return profiles;
    }
}
