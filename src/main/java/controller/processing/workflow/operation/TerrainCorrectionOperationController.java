package controller.processing.workflow.operation;

import com.beust.jcommander.Strings;
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

public class TerrainCorrectionOperationController implements Initializable, OperationController {
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
        correctionSourceBands.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateInput();
        });
    }

    private void initIncidenceAngleControl() {
        ObservableList<String> items = FXCollections.observableArrayList("Use incidence angle from Ellipsoid");
        incidenceAngle.setItems(items);
        if (incidenceAngle.getValue() == null)
            incidenceAngle.setValue(items.get(0));
    }

    private void initPixelSpacingInMeter() {
        pixelSpacingInMeter.setText("10.0");
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
        operation.getParameters().put("pixelSpacingInMeter", Double.parseDouble(pixelSpacingInMeter.getText()));
        operation.getParameters().put("nodataValueAtSea", noDataValueAtSea.isSelected());
        //operation.getParameters().put("sourceBands", Strings.join(",",correctionSourceBands.getSelectionModel().getSelectedItems()));
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
        selectNoDataValueAtSea((Boolean)(operation.getParameters().getOrDefault("nodataValueAtSea",false)));
        demResampling.setValue(String.valueOf(operation.getParameters().getOrDefault("demResamplingMethod",demResampling.getItems().get(0))));
        imageResampling.setValue(String.valueOf(operation.getParameters().getOrDefault("imgResamplingMethod",imageResampling.getItems().get(0))));
        incidenceAngle.setValue(String.valueOf(operation.getParameters().getOrDefault("incidenceAngleForSigma0",incidenceAngle.getItems().get(0))));
        demName.setValue(String.valueOf(operation.getParameters().getOrDefault("demName",demName.getItems().get(0))));

        ObservableList<String> sourceBands = FXCollections.observableArrayList(Arrays.asList(operation.getParameters().getOrDefault("sourceBands", "").toString().split(",")));
        if (!sourceBands.get(0).equals("")) {
            correctionSourceBands.getItems().clear();
            correctionSourceBands.getSelectionModel().clearSelection();
            correctionSourceBands.getItems().addAll(sourceBands);
            sourceBands.forEach(b->correctionSourceBands.getSelectionModel().select(b));
        }

        pixelSpacingInMeter.setText(String.valueOf(operation.getParameters().getOrDefault("pixelSpacingInMeter",pixelSpacingInMeter.getText())));
    }
}
