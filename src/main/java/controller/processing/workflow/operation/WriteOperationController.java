package controller.processing.workflow.operation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import org.controlsfx.control.ToggleSwitch;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller to Write operation
 */
public class WriteOperationController implements Initializable, OperationController {
    @FXML
    private ToggleSwitch generatePNG;
    @FXML
    private ChoiceBox<String> writeFormat;

    private Map<String, Object> parameters;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initWriteFormat();
        this.parameters = new HashMap<>();
    }

    private void initWriteFormat() {
        ObservableList<String> items = FXCollections.observableArrayList("GeoTIFF","GeoTIFF-BigTIFF", "PolSARPro");
        writeFormat.setItems(items);
        writeFormat.setValue("GeoTIFF");
    }

    @Override
    public Map<String, Object> getParameters() {
        parameters.put("formatName",writeFormat.getValue());
        parameters.put("generatePNG",generatePNG.isSelected());
        return parameters;
    }

    @Override
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
        writeFormat.setValue(String.valueOf(parameters.getOrDefault("formatName","GeoTIFF")));
        generatePNG.setSelected((boolean)(parameters.getOrDefault("generatePNG",false)));
    }
}
