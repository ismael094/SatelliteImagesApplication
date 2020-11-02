package controller.processing.workflow.operation;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import org.controlsfx.control.ToggleSwitch;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ResampleOperationController implements Initializable,OperationController {
    @FXML
    private ChoiceBox<String> targetedBand;
    @FXML
    private ChoiceBox<String> downsampling;
    @FXML
    private ChoiceBox<String> upsampling;
    @FXML
    private ChoiceBox<String> flagDownsampling;
    @FXML
    private ToggleSwitch resampleOnPyramidLevels;

    private Map<String,Object> parameters;

    @Override
    public void setParameters(Map<String,Object> parameters) {
        this.parameters = parameters;
        targetedBand.setValue(String.valueOf(parameters.getOrDefault("referenceBand","B2")));
        upsampling.setValue(String.valueOf(parameters.getOrDefault("upsampling","Nearest")));
        downsampling.setValue(String.valueOf(parameters.getOrDefault("downsampling","First")));
        flagDownsampling.setValue(String.valueOf(parameters.getOrDefault("flagDownsampling","First")));
    }

    @Override
    public Map<String,Object> getParameters() {
        parameters.put("referenceBand",targetedBand.getValue());
        parameters.put("upsampling",upsampling.getValue());
        parameters.put("downsampling",downsampling.getValue());
        parameters.put("flagDownsampling",flagDownsampling.getValue());
        parameters.put("sourceBands","B2,B3,B4,B5,B6,B7,B8,B8A,B11,B12");
        return parameters;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        targetedBand.setItems(FXCollections.observableArrayList("B2","B3","B4","B5","B6","B7","B8","B8A","B11","B12"));
        targetedBand.setValue("B2");
        upsampling.setItems(FXCollections.observableArrayList("Nearest", "Bilinear", "Bicubic"));
        upsampling.setValue("Nearest");
        downsampling.setItems(FXCollections.observableArrayList("First", "Min", "Max", "Mean", "Median"));
        downsampling.setValue("First");
        flagDownsampling.setItems(FXCollections.observableArrayList("First", "FlagAnd", "FlagOr", "FlagMedianAnd", "FlagMedianOr"));
        flagDownsampling.setValue("First");
        this.parameters = new HashMap<>();
    }
}
