package controller.processing.workflow.operation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import model.processing.workflow.operation.Operation;
import model.processing.workflow.operation.Operator;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class TopSarDeburstOperationController implements OperationController, Initializable {


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
