package controller.workflow.operation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("HOLAA");
        this.parameters = new HashMap<>();
        operation = new Operation(Operator.WRITE, parameters);
        initWriteFormat();
    }

    private void initWriteFormat() {
        ObservableList<String> items = FXCollections.observableArrayList("GeoTIFF", "PolSARPro");
        writeFormat.setItems(items);
        if (writeFormat.getValue() == null)
            writeFormat.setValue("GeoTIFF");
    }

    @Override
    public Operation getOperation() {
        getParameters();
        return operation;
    }

    private void getParameters() {
        parameters.put("formatName",writeFormat.getValue());
    }

    @Override
    public void setOperation(Operation operation) {
        this.operation = operation;
        this.parameters = operation.getParameters();
        setParameters(operation.getParameters());
    }

    private void setParameters(Map<String, Object> parameters) {
        writeFormat.setValue(String.valueOf(parameters.get("formatName")));
    }
}