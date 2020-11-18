package controller.processing.workflow.operation;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller to Subset operation
 */
public class SubsetOperationController implements OperationController, Initializable {

    @FXML
    private CheckBox metadata;
    @FXML
    private CheckBox outputInDb;

    private Map<String,Object> parameters;

    @Override
    public Map<String,Object> getParameters() {
        parameters.clear();
        parameters.put("copyMetadata", metadata.isSelected());
        parameters.put("outputImageScaleInDb", outputInDb.isSelected());
        return parameters;
    }

    @Override
    public void setParameters(Map<String,Object> parameters) {
        this.parameters = parameters;
        selectMetadata((Boolean)(parameters.getOrDefault("copyMetadata",false)));
        selectOutputInDbControl((Boolean)(parameters.getOrDefault("outputImageScaleInDb",false)));
    }

    private void selectMetadata(Boolean select) {
        metadata.setSelected(select);
    }

    private void selectOutputInDbControl(Boolean select) {
        outputInDb.setSelected(select);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.parameters = new HashMap<>();
    }
}
