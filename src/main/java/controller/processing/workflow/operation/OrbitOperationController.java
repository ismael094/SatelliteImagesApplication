package controller.processing.workflow.operation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import model.processing.workflow.operation.Operation;
import model.processing.workflow.operation.Operator;

import java.net.URL;
import java.util.*;

public class OrbitOperationController implements Initializable, OperationController {
    @FXML
    private ChoiceBox<String> orbitType;
    @FXML
    private TextField plyDegree;
    private Operation operation;
    private Operation previewOperation;
    private OperationController nextOperationController;
    private ObservableList<String> inputBands;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        previewOperation = null;
        operation = new Operation(Operator.APPLY_ORBIT_FILE,new HashMap<>());
        initControls();
    }

    private void initControls() {
        initOrbitTypeControl();
        initPolyDegree();
    }

    private void initPolyDegree() {
        plyDegree.setText("3");
        plyDegree.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty())
                plyDegree.setText(oldValue);
        });
    }

    private void initOrbitTypeControl() {
        ObservableList<String> items = FXCollections.observableArrayList("Sentinel Precise (Auto Download)");
        orbitType.setItems(items);
        if (orbitType.getValue() == null)
            orbitType.setValue(orbitType.getItems().get(0));
    }

    @Override
    public Operation getOperation() {
        getParameters();
        return operation;
    }

    private void getParameters() {
        operation.getParameters().put("polyDegree",Integer.parseInt(plyDegree.getText()));
        operation.getParameters().put("orbitType",orbitType.getValue());
    }

    @Override
    public void setOperation(Operation operation) {
        this.operation = operation;
        setParameters();
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
        return inputBands;
    }

    @Override
    public void setNextOperationController(OperationController operationController) {
        this.nextOperationController = operationController;
        updateInput();
    }

    @Override
    public void updateInput() {
        nextOperationController.setInputBands(getOutputBands());
    }

    @Override
    public OperationController getNextOperationController() {
        return nextOperationController;
    }

    private void setParameters() {
        plyDegree.setText(String.valueOf(operation.getParameters().getOrDefault("polyDegree",3)));
        orbitType.setValue(String.valueOf(operation.getParameters().getOrDefault("orbitType",orbitType.getItems().get(0))));
    }
}
