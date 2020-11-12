package controller.processing.workflow.operation;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import utils.AlertFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class TerrainCorrectionOperationController implements Initializable, OperationController {
    @FXML
    private ChoiceBox<String> demResampling;
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
        initImageResamplingControl();
        initPixelSpacingInMeter();
        initDemName();
        this.parameters = new HashMap<>();
    }

    @Override
    public Map<String,Object> getParameters() {
        parameters.put("demResamplingMethod", demResampling.getValue());
        parameters.put("imgResamplingMethod", imageResampling.getValue());
        parameters.put("demName", demName.getValue());
        parameters.put("pixelSpacingInMeter", Double.parseDouble(pixelSpacingInMeter.getText()));
        parameters.put("nodataValueAtSea", noDataValueAtSea.isSelected());
        return parameters;
    }

    @Override
    public void setParameters(Map<String,Object> parameters) {
        this.parameters = parameters;
        selectNoDataValueAtSea((Boolean)(parameters.getOrDefault("nodataValueAtSea",false)));
        demResampling.setValue(String.valueOf(parameters.getOrDefault("demResamplingMethod",demResampling.getItems().get(0))));
        imageResampling.setValue(String.valueOf(parameters.getOrDefault("imgResamplingMethod",imageResampling.getItems().get(0))));
        demName.setValue(String.valueOf(parameters.getOrDefault("demName",demName.getItems().get(0))));
        pixelSpacingInMeter.setText(String.valueOf(parameters.getOrDefault("pixelSpacingInMeter",pixelSpacingInMeter.getText())));
    }



    private void initPixelSpacingInMeter() {
        pixelSpacingInMeter.setText("10.0");
        Tooltip tp = new Tooltip("Format value: 10.5, 11,3...");
        Tooltip.install(pixelSpacingInMeter,tp);
        pixelSpacingInMeter.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("^[\\d*]+.[\\d*]*$"))
                pixelSpacingInMeter.setText(oldValue);
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

    private void initImageResamplingControl() {
        ObservableList<String> items = FXCollections.observableArrayList("NEAREST_NEIGHBOUR");
        items.add("BILINEAR_INTERPOLATION");
        items.add("CUBIC_CONVOLUTION");
        items.add("BISINC_5_POINT_INTERPOLATION");
        items.add("BISINC_11_POINT_INTERPOLATION");
        items.add("BISINC_21_POINT_INTERPOLATION");
        items.add("BICUBIC_INTERPOLATION");
        imageResampling.setItems(items);
        if (imageResampling.getValue() == null)
            imageResampling.setValue(items.get(0));
    }

    private void selectNoDataValueAtSea(boolean select) {
        noDataValueAtSea.setSelected(select);
    }


}
