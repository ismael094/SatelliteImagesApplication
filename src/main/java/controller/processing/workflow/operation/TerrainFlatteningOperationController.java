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

public class TerrainFlatteningOperationController implements Initializable, OperationController {
    @FXML
    private TextField oversamplingMultiple;
    @FXML
    private TextField additionalOverlap;
    @FXML
    private ChoiceBox<String> demResampling;
    @FXML
    private ChoiceBox<String> incidenceAngle;
    @FXML
    private ChoiceBox<String> imageResampling;
    @FXML
    private TextField pixelSpacingInMeter;
    @FXML
    private ChoiceBox<String> demName;
    @FXML
    private CheckBox noDataValueAtSea;
    private Map<String,Object> parameters;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDemResamplingControl();
        initDemName();
        initOversamplingMultiple();
        initAdditionalOverlap();
        this.parameters = new HashMap<>();
    }

    private void initAdditionalOverlap() {
        oversamplingMultiple.setText("1");
        oversamplingMultiple.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[1-4]$"))
                oversamplingMultiple.setText(oldValue);
        });
    }

    private void initOversamplingMultiple() {
        additionalOverlap.setText("0.1");
        additionalOverlap.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^0.[1-9]$") || !newValue.matches("^1.0$"))
                additionalOverlap.setText(oldValue);
        });
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
        return parameters;
     }

    @Override
    public void setParameters(Map<String,Object> parameters) {
        this.parameters = parameters;
        demResampling.setValue(String.valueOf(parameters.getOrDefault("demResamplingMethod",demResampling.getItems().get(0))));
        demName.setValue(String.valueOf(parameters.getOrDefault("demName",demName.getItems().get(0))));
    }
}
