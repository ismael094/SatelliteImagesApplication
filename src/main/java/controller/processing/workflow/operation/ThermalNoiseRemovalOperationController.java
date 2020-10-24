package controller.processing.workflow.operation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import model.processing.workflow.operation.Operation;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ThermalNoiseRemovalOperationController implements OperationController, Initializable {
    private Operation operation;
    private ObservableList<String> inputBands;
    private OperationController nextOperationController;

    @Override
    public Operation getOperation() {
        return operation;
    }

    @Override
    public void setOperation(Operation operation) {
        this.operation = operation;
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
        return FXCollections.observableArrayList("Intensity_VH","Intensity_VV");
    }

    @Override
    public void setNextOperationController(OperationController operationController) {
        this.nextOperationController = operationController;
        updateInput();
    }

    @Override
    public void updateInput() {
        if (nextOperationController != null)
            nextOperationController.setInputBands(getOutputBands());
    }

    @Override
    public OperationController getNextOperationController() {
        return nextOperationController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
