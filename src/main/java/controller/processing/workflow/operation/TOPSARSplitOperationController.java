package controller.processing.workflow.operation;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class TOPSARSplitOperationController implements OperationController, Initializable {
    @FXML
    private ChoiceBox<String> subswath;
    @FXML
    private ChoiceBox<String> firstBurstIndex;
    @FXML
    private ChoiceBox<String> lastBurstIndex;
    private Map<String,Object> parameters;


    @Override
    public Map<String, Object> getParameters() {
        parameters.put("subswath", subswath.getValue());
        parameters.put("firstBurstIndex", firstBurstIndex.getValue());
        parameters.put("lastBurstIndex", lastBurstIndex.getValue());
        return parameters;
    }

    @Override
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
        subswath.setValue(String.valueOf(parameters.getOrDefault("subswath", "IW1")));
        firstBurstIndex.setValue(String.valueOf(parameters.getOrDefault("firstBurstIndex", "1")));
        lastBurstIndex.setValue(String.valueOf(parameters.getOrDefault("lastBurstIndex", "9")));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.parameters = new HashMap<>();
        subswath.setItems(FXCollections.observableArrayList("IW1","IW2","IW3"));
        subswath.setValue("IW1");

        addIndex(firstBurstIndex);
        firstBurstIndex.setValue("1");

        firstBurstIndex.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (Integer.parseInt(newValue) >= Integer.parseInt(lastBurstIndex.getValue()))
                firstBurstIndex.setValue(oldValue);
        });

        addIndex(lastBurstIndex);
        lastBurstIndex.setValue("9");

        lastBurstIndex.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (Integer.parseInt(newValue) <= Integer.parseInt(firstBurstIndex.getValue()))
                lastBurstIndex.setValue(oldValue);
        });
    }

    private void addIndex(ChoiceBox<String> choiceBox) {
        for (int i = 1; i <=9;i++)
            choiceBox.getItems().add(i+"");
    }
}
