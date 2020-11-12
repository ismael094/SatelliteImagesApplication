package controller.processing.workflow.operation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.*;

public class OrbitOperationController implements Initializable, OperationController {
    @FXML
    private ChoiceBox<String> orbitType;
    @FXML
    private TextField plyDegree;
    private Map<String, Object> parameters;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initControls();
        this.parameters = new HashMap<>();
    }

    private void initControls() {
        initOrbitTypeControl();
        initPolyDegree();
    }

    private void initPolyDegree() {
        plyDegree.setText("3");
        plyDegree.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty() || !newValue.matches("^[\\d*]*$"))
                plyDegree.setText("3");
        });
    }

    private void initOrbitTypeControl() {
        ObservableList<String> items = FXCollections.observableArrayList("Sentinel Precise (Auto Download)");
        orbitType.setItems(items);
        if (orbitType.getValue() == null)
            orbitType.setValue(orbitType.getItems().get(0));
    }

    @Override
    public Map<String,Object> getParameters() {
        if (plyDegree.getText().isEmpty())
            parameters.put("polyDegree",3);
        else
            parameters.put("polyDegree",Integer.parseInt(plyDegree.getText()));
        parameters.put("orbitType",orbitType.getValue());
        return parameters;
    }

    @Override
    public void setParameters(Map<String,Object> parameters) {
        this.parameters = parameters;
        plyDegree.setText(String.valueOf(parameters.getOrDefault("polyDegree",3)));
        orbitType.setValue(String.valueOf(parameters.getOrDefault("orbitType",orbitType.getItems().get(0))));
    }
}
