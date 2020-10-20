package controller.workflow.operation;

import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import model.processing.Operation;

import java.net.URL;
import java.util.ResourceBundle;

public class WriteAndReadOperationController implements Initializable, OperationController {

    private Operation operation;
    private ObservableList<String> inputBands;
    private OperationController nextOperationController;

    @Override
    public Operation getOperation() {
        return operation;
    }

    @Override
    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    @Override
    public void inputBands(ObservableList<String> inputBands) {
        this.inputBands = inputBands;
        updateInput();
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
        if (nextOperationController!=null)
            nextOperationController.inputBands(inputBands);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
