package controller.workflow.operation;

import com.beust.jcommander.Strings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.processing.Operation;
import model.processing.Operator;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class TerrainCorrectionOperationController implements Initializable, OperationController {
    @FXML
    private ChoiceBox<String> demResampling;
    @FXML
    private ChoiceBox<String> incidenceAngle;
    @FXML
    private ChoiceBox<String> imageResampling;
    @FXML
    private ChoiceBox<String> pixelSpacingInMeter;
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
        operation = new Operation(Operator.TERRAIN_CORRECTION,new HashMap<>());
        initDemResamplingControl();
        initImageResamplingControl();
        initPixelSpacingInMeter();
        initDemName();
        initCorrectionSourceBands();
        initIncidenceAngleControl();
        correctionSourceBands.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        correctionSourceBands.getItems().addListener((ListChangeListener<String>) c -> {
            updateInput();
        });
    }

    private void initIncidenceAngleControl() {
        ObservableList<String> items = FXCollections.observableArrayList("Use incidence angle from Ellipsoid");
        incidenceAngle.setItems(items);
        if (incidenceAngle.getValue() == null)
            incidenceAngle.setValue(items.get(0));
    }

    private void initImageResamplingControl() {
        ObservableList<String> items = FXCollections.observableArrayList("NEAREST_NEIGHBOUR");
        imageResampling.setItems(items);
        if (imageResampling.getValue() == null)
            imageResampling.setValue(items.get(0));
    }

    private void initPixelSpacingInMeter() {
        ObservableList<String> items = FXCollections.observableArrayList("\"Use incidence angle from Ellipsoid\"");
        pixelSpacingInMeter.setItems(items);
        if (pixelSpacingInMeter.getValue() == null)
            pixelSpacingInMeter.setValue(items.get(0));
    }

    private void initDemName() {
        ObservableList<String> items = FXCollections.observableArrayList("SRTM 3Sec", "SRTM 1Sec HGT");
        demName.setItems(items);
        if (demName.getValue() == null)
            demName.setValue(items.get(0));
    }

    private void initCorrectionSourceBands() {
        ObservableList<String> items = FXCollections.observableArrayList("Beta0_VH");
        correctionSourceBands.setItems(items);
        /*if (correctionSourceBands.getValue() == null)
            correctionSourceBands.setValue(items.get(0));*/
    }

    private void initDemResamplingControl() {
        ObservableList<String> items = FXCollections.observableArrayList("NEAREST_NEIGHBOUR");
        demResampling.setItems(items);
        if (demResampling.getValue() == null)
            demResampling.setValue(items.get(0));
    }

    private void selectNoDataValueAtSea(boolean select) {
        noDataValueAtSea.setSelected(select);
    }

    @Override
    public Operation getOperation() {
        getParameters();
        return operation;
    }

    private void getParameters() {
        operation.getParameters().put("demResamplingMethod", demResampling.getValue());
        operation.getParameters().put("imgResamplingMethod", imageResampling.getValue());
        operation.getParameters().put("incidenceAngleForSigma0", incidenceAngle.getValue());
        operation.getParameters().put("demName", demName.getValue());
        operation.getParameters().put("pixelSpacingInMeter", pixelSpacingInMeter.getValue());
        operation.getParameters().put("nodataValueAtSea", noDataValueAtSea.isSelected());
        operation.getParameters().put("sourceBands", Strings.join(",",correctionSourceBands.getSelectionModel().getSelectedItems()));
    }

    @Override
    public void setOperation(Operation operation) {
        this.operation = operation;
        setParameters();
    }

    @Override
    public void inputBands(ObservableList<String> inputBands) {
        correctionSourceBands.getItems().clear();
        correctionSourceBands.getItems().addAll(inputBands);
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
            nextOperationController.inputBands(correctionSourceBands.getSelectionModel().getSelectedItems());
    }

    private void setParameters() {
        selectNoDataValueAtSea((Boolean)(operation.getParameters().getOrDefault("nodataValueAtSea",false)));
        demResampling.setValue(String.valueOf(operation.getParameters().getOrDefault("demResamplingMethod",demResampling.getItems().get(0))));
        imageResampling.setValue(String.valueOf(operation.getParameters().getOrDefault("imgResamplingMethod",imageResampling.getItems().get(0))));
        incidenceAngle.setValue(String.valueOf(operation.getParameters().getOrDefault("incidenceAngleForSigma0",incidenceAngle.getItems().get(0))));
        demName.setValue(String.valueOf(operation.getParameters().getOrDefault("demName",demName.getItems().get(0))));
        correctionSourceBands.getItems().addAll(FXCollections.observableArrayList(Arrays.asList(operation.getParameters().get("sourceBands").toString().split(","))));
        pixelSpacingInMeter.setValue(String.valueOf(operation.getParameters().getOrDefault("pixelSpacingInMeter",pixelSpacingInMeter.getItems().get(0))));
    }
}
