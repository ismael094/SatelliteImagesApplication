package controller.processing.workflow.operation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import org.controlsfx.control.ToggleSwitch;

import java.net.URL;
import java.util.*;

public class WriteSentinel2OperationController implements OperationController, Initializable {
    @FXML
    private ChoiceBox<String> writeFormat;
    @FXML
    private ToggleSwitch generatePng;
    @FXML
    private ChoiceBox<String> red;
    @FXML
    private ChoiceBox<String> green;
    @FXML
    private ChoiceBox<String> blue;
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
        red.setValue(String.valueOf(parameters.getOrDefault("red","B4")));
        green.setValue(String.valueOf(parameters.getOrDefault("green","B3")));
        blue.setValue(String.valueOf(parameters.getOrDefault("blue","B2")));

    }

    @Override
    public Map<String, Object> getParameters() {
        parameters.clear();
        parameters.put("formatName",writeFormat.getValue());
        if (generatePng.isSelected()) {
            parameters.put("generatePNG",true);
            parameters.put("profile",profiles.getValue());
            parameters.put("red",red.getValue());
            parameters.put("green",green.getValue());
            parameters.put("blue",blue.getValue());
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

        initColor(red);
        initColor(green);
        initColor(blue);

        setRGBValues(Arrays.asList("B4","B3","B2"));

        profiles.setValue("Some1");
        writeFormat.setItems(FXCollections.observableArrayList("GeoTIFF","PolSARPro"));
    }

    private void initColor(ChoiceBox<String> color) {
        color.setItems(FXCollections.observableArrayList("B2","B3","B4","B5","B6","B7","B8","B8A","B11","B12"));
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
        red.setValue(rgbValues.get(0));
        blue.setValue(rgbValues.get(1));
        green.setValue(rgbValues.get(2));
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

    public ChoiceBox<String> getRed() {
        return red;
    }

    public ChoiceBox<String> getGreen() {
        return green;
    }

    public ChoiceBox<String> getBlue() {
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
