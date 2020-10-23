package controller.processing.workflow.operation;

import javafx.collections.ObservableList;
import model.processing.workflow.Operation;

public class ReadOperationController implements OperationController {
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
    public void setInputBands(ObservableList<String> inputBands) {
        this.inputBands = inputBands;
        updateInput();
    }

    @Override
    public ObservableList<String> getInputBands() {
        return this.inputBands;
    }

    @Override
    public ObservableList<String> getOutputBands() {
        return this.inputBands;
    }

    @Override
    public void setNextOperationController(OperationController operationController) {
        this.nextOperationController = operationController;
        updateInput();
    }

    @Override
    public void updateInput() {
        if (nextOperationController != null)
            nextOperationController.setInputBands(inputBands);
    }

    @Override
    public OperationController getNextOperationController() {
        return nextOperationController;
    }
}
