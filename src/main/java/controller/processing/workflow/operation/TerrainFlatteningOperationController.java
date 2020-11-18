package controller.processing.workflow.operation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller to TerrainFlattening operation
 */
public class TerrainFlatteningOperationController implements Initializable, OperationController {
    @FXML
    private TextField oversamplingMultiple;
    @FXML
    private TextField additionalOverlap;
    @FXML
    private ChoiceBox<String> demResampling;
    @FXML
    private ChoiceBox<String> demName;
    private Map<String,Object> parameters;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDemResamplingControl();
        initDemName();
        initOversamplingMultiple();
        initAdditionalOverlap();
        this.parameters = new HashMap<>();
    }

    private void initOversamplingMultiple() {
        oversamplingMultiple.setText("1");
        Tooltip tp = new Tooltip("Range value: 1-4");
        Tooltip.install(additionalOverlap,tp);
        oversamplingMultiple.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !newValue.matches("^[1-4]$"))
                oversamplingMultiple.setText(oldValue);
        });
    }

    private void initAdditionalOverlap() {
        additionalOverlap.setText("0.1");
        Tooltip tp = new Tooltip("Range value: 0-1");
        Tooltip.install(additionalOverlap,tp);
        additionalOverlap.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!validAdditionalOverlap(newValue))
                if (!validAdditionalOverlap(oldValue))
                    additionalOverlap.setText("0.1");
                else
                    additionalOverlap.setText(oldValue);
        });
    }

    private boolean validAdditionalOverlap(String value) {
        return value.isEmpty() || value.matches("^0.[1-9]$") || value.matches("^1.0$");
    }

    private void initDemName() {
        ObservableList<String> items = FXCollections.observableArrayList("SRTM 3Sec", "SRTM 1Sec HGT");
        demName.setItems(items);
        if (demName.getValue() == null)
            demName.setValue(items.get(0));
    }

    private void initDemResamplingControl() {
        ObservableList<String> items = FXCollections.observableArrayList("NEAREST_NEIGHBOUR");
        items.add("BILINEAR_INTERPOLATION");
        items.add("CUBIC_CONVOLUTION");
        items.add("BISINC_5_POINT_INTERPOLATION");
        items.add("BISINC_11_POINT_INTERPOLATION");
        items.add("BISINC_21_POINT_INTERPOLATION");
        items.add("BICUBIC_INTERPOLATION");
        items.add("DELAUNAY_INTERPOLATION");
        demResampling.setItems(items);
        if (demResampling.getValue() == null)
            demResampling.setValue(items.get(0));
    }

    @Override
    public Map<String,Object> getParameters() {
        parameters.put("demResamplingMethod", demResampling.getValue());
        parameters.put("demName", demName.getValue());
        addParameterOrDefault(oversamplingMultiple,"oversamplingMultiple","1");
        addParameterOrDefault(additionalOverlap,"additionalOverlap","0.1");
        return parameters;
     }

    private void addParameterOrDefault(TextField field,String parameter, String default_) {
        if (field.getText().isEmpty()) {
            parameters.put(parameter, default_);
        } else
            parameters.put(parameter, field.getText());
    }

    @Override
    public void setParameters(Map<String,Object> parameters) {
        this.parameters = parameters;
        demResampling.setValue(String.valueOf(parameters.getOrDefault("demResamplingMethod",demResampling.getItems().get(0))));
        demName.setValue(String.valueOf(parameters.getOrDefault("demName",demName.getItems().get(0))));
        oversamplingMultiple.setText(String.valueOf(parameters.getOrDefault("oversamplingMultiple","1")));
        additionalOverlap.setText(String.valueOf(parameters.getOrDefault("additionalOverlap","0.1")));

    }
}
