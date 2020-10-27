package controller.processing.workflow.operation;

import com.beust.jcommander.Strings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import model.processing.workflow.operation.Operation;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class SubsetOperationController implements OperationController, Initializable {

    @FXML
    private ListView<String> subsetSourceBands;
    @FXML
    private CheckBox metadata;
    @FXML
    private CheckBox outputInDb;

    private Operation operation;
    private ObservableList<String> inputBands;
    private OperationController nextOperationController;


    @Override
    public Operation getOperation() {
        getParameters();
        return operation;
    }

    @Override
    public void setOperation(Operation operation) {
        this.operation = operation;
        setParameters();
    }

    @Override
    public void setInputBands(ObservableList<String> inputBands) {
        subsetSourceBands.getItems().clear();
        subsetSourceBands.getItems().addAll(inputBands);
        inputBands.forEach(b->subsetSourceBands.getSelectionModel().select(b));
        updateInput();
    }

    @Override
    public ObservableList<String> getInputBands() {
        return subsetSourceBands.getItems();
    }

    @Override
    public ObservableList<String> getOutputBands() {
        return subsetSourceBands.getSelectionModel().getSelectedItems();
    }

    @Override
    public void setNextOperationController(OperationController operationController) {
        this.nextOperationController = operationController;
        updateInput();
    }

    @Override
    public void updateInput() {
        if (nextOperationController != null)
            nextOperationController.setInputBands(getOutputBands());
    }

    @Override
    public OperationController getNextOperationController() {
        return nextOperationController;
    }

    private void getParameters() {
        operation.getParameters().put("copyMetadata", metadata.isSelected());
        operation.getParameters().put("outputImageScaleInDb", outputInDb.isSelected());
        //operation.getParameters().put("sourceBands", Strings.join(",",subsetSourceBands.getSelectionModel().getSelectedItems()));
    }

    private void setParameters() {

        ObservableList<String> strings = FXCollections.observableArrayList(Arrays.asList(operation.getParameters().getOrDefault("sourceBands", "").toString().split(",")));
        if (!strings.get(0).equals("")) {
            subsetSourceBands.getItems().clear();
            subsetSourceBands.getItems().addAll(strings);
            strings.forEach(s-> subsetSourceBands.getSelectionModel().select(s));
        }
        selectMetadata((Boolean)(operation.getParameters().getOrDefault("copyMetadata",false)));
        selectOutputInDbControl((Boolean)(operation.getParameters().getOrDefault("outputImageScaleInDb",false)));
    }

    private void selectMetadata(Boolean select) {
        metadata.setSelected(select);
    }

    private void selectOutputInDbControl(Boolean select) {
        outputInDb.setSelected(select);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        subsetSourceBands.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
}
