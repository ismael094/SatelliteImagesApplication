package controller.processing.workflow.operation;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller to WriteAndRead operation
 */
public class WriteAndReadOperationController implements Initializable, OperationController {


    private Map<String, Object> parameters;

    @Override
    public Map<String, Object> getParameters() {
        parameters.put("formatName","BEAM-DIMAP");
        return parameters;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.parameters = new HashMap<>();
    }

    @Override
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
}
