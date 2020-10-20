package controller.workflow.operation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import model.processing.Operation;
import model.processing.Operator;
import org.esa.snap.core.util.math.FX;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        previewOperation = null;
        operation = new Operation(Operator.APPLY_ORBIT_FILE,new HashMap<>());
        initControls();
    }

    private void initControls() {
        initOrbitTypeControl();
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
    public void inputBands(ObservableList<String> inputBands) {
        //this.previewOperation = operation;
    }

    @Override
    public ObservableList<String> getOutputBands() {
        return FXCollections.observableArrayList("Intensity_VH","Intensity_VV","Amplitude_VH","Amplitude_VV");
    }

    @Override
    public void setNextOperationController(OperationController operationController) {
        this.nextOperationController = operationController;
        updateInput();
    }

    @Override
    public void updateInput() {
        nextOperationController.inputBands(getOutputBands());
    }

    private void setParameters() {
        plyDegree.setText(String.valueOf(operation.getParameters().getOrDefault("polyDegree",3)));
        orbitType.setValue(String.valueOf(operation.getParameters().getOrDefault("orbitType",orbitType.getItems().get(0))));
    }
}
