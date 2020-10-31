package controller.processing.workflow.operation;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class ThermalNoiseRemovalOperationController implements OperationController, Initializable {

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public Map<String, Object> getParameters() {
        return new HashMap<>();
    }

    @Override
    public void setParameters(Map<String, Object> parameters) {

    }
}
