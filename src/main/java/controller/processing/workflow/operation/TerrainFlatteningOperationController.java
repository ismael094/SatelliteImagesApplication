package controller.processing.workflow.operation;

import com.beust.jcommander.Strings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.processing.workflow.operation.Operation;
import model.processing.workflow.operation.Operator;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
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
    private ListView<String> correctionSourceBands;
    @FXML
    private CheckBox noDataValueAtSea;
    private Operation operation;
    private OperationController nextOperationController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        operation = new Operation(Operator.TERRAIN_FLATTENING,new HashMap<>());
        initDemResamplingControl();
        initDemName();
        initOversamplingMultiple();
        initAdditionalOverlap();
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
    public Operation getOperation() {
        getParameters();
        return operation;
    }

    private void getParameters() {
        operation.getParameters().put("demResamplingMethod", demResampling.getValue());
        operation.getParameters().put("demName", demName.getValue());
     }

    @Override
    public void setOperation(Operation operation) {
        this.operation = operation;
        setParameters();
    }

    @Override
    public void setInputBands(ObservableList<String> inputBands) {
        correctionSourceBands.getItems().clear();
        correctionSourceBands.getItems().addAll(inputBands);
        inputBands.forEach(b->{
            correctionSourceBands.getSelectionModel().select(b);
        });
        updateInput();
    }

    @Override
    public ObservableList<String> getInputBands() {
        return correctionSourceBands.getItems();
    }

    @Override
    public ObservableList<String> getOutputBands() {
        return correctionSourceBands.getSelectionModel().getSelectedItems();
    }

    @Override
    public void setNextOperationController(OperationController operationController) {
        this.nextOperationController = operationController;
        updateInput();
    }

    @Override
    public void updateInput() {
        if (nextOperationController!=null)
            nextOperationController.setInputBands(getOutputBands());
    }

    @Override
    public OperationController getNextOperationController() {
        return nextOperationController;
    }

    private void setParameters() {
        demResampling.setValue(String.valueOf(operation.getParameters().getOrDefault("demResamplingMethod",demResampling.getItems().get(0))));
        demName.setValue(String.valueOf(operation.getParameters().getOrDefault("demName",demName.getItems().get(0))));
    }
}
