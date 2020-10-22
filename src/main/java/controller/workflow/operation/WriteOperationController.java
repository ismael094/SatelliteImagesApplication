package controller.workflow.operation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import model.processing.Operation;
import model.processing.Operator;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class WriteOperationController implements Initializable, OperationController {
    @FXML
    private ChoiceBox<String> writeFormat;
    private Operation operation;
    private Map<String, Object> parameters;
    private Operation previewOperation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        previewOperation = null;
        initWriteFormat();
    }

    private void initWriteFormat() {
        ObservableList<String> items = FXCollections.observableArrayList("GeoTIFF", "PolSARPro");
        writeFormat.setItems(items);
        if (writeFormat.getValue() == null || writeFormat.getValue().isEmpty())
            writeFormat.setValue("GeoTIFF");
    }

    @Override
    public Operation getOperation() {
        getParameters();
        return operation;
    }

    private void getParameters() {
        operation.getParameters().put("formatName",writeFormat.getValue());
    }

    @Override
    public void setOperation(Operation operation) {
        this.operation = operation;
        this.parameters = operation.getParameters();
        setParameters(operation.getParameters());
    }

    @Override
    public void setInputBands(ObservableList<String> inputBands) {

    }

    @Override
    public ObservableList<String> getInputBands() {
        return FXCollections.observableArrayList();
    }

    @Override
    public ObservableList<String> getOutputBands() {
        return null;
    }

    @Override
    public void setNextOperationController(OperationController operationController) {

    }

    @Override
    public void updateInput() {

    }

    @Override
    public OperationController getNextOperationController() {
        return null;
    }

    private void setParameters(Map<String, Object> parameters) {
        writeFormat.setValue(String.valueOf(parameters.get("formatName")));
    }
}
