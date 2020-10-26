package controller.processing.workflow.operation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import model.processing.workflow.operation.Operation;
import org.controlsfx.control.ToggleSwitch;

import java.net.URL;
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
    private Operation operation;
    private ObservableList<String> inputBands;
    private OperationController next;

    @Override
    public Operation getOperation() {
        getParameters();
        return operation;
    }

    @Override
    public void setOperation(Operation operation) {
        this.operation = operation;
        setParameters();
    }

    private void setParameters() {
        targetedBand.setValue(String.valueOf(operation.getParameters().getOrDefault("referenceBand","B2")));
        upsampling.setValue(String.valueOf(operation.getParameters().getOrDefault("upsampling","Nearest")));
        downsampling.setValue(String.valueOf(operation.getParameters().getOrDefault("downsampling","First")));
        flagDownsampling.setValue(String.valueOf(operation.getParameters().getOrDefault("flagDownsampling","First")));
    }

    private void getParameters() {
        operation.getParameters().put("referenceBand",targetedBand.getValue());
        operation.getParameters().put("upsampling",upsampling.getValue());
        operation.getParameters().put("downsampling",downsampling.getValue());
        operation.getParameters().put("flagDownsampling",flagDownsampling.getValue());
    }

    @Override
    public void setInputBands(ObservableList<String> inputBands) {
        this.inputBands = inputBands;
    }

    @Override
    public ObservableList<String> getInputBands() {
        return inputBands;
    }

    @Override
    public ObservableList<String> getOutputBands() {
        return FXCollections.observableArrayList("B2","B3","B4","B5","B6","B7","B8","B8A","B11","B12");
    }

    @Override
    public void setNextOperationController(OperationController operationController) {
        this.next = operationController;
    }

    @Override
    public void updateInput() {
        if (this.next != null)
            next.setInputBands(getOutputBands());
    }

    @Override
    public OperationController getNextOperationController() {
        return next;
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
    }
}
