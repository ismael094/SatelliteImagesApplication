package controller.processing.workflow.operation;

import javafx.collections.ObservableList;
import model.processing.workflow.operation.Operation;


public interface OperationController {
    Operation getOperation();
    void setOperation(Operation operation);
    void setInputBands(ObservableList<String> inputBands);
    ObservableList<String> getInputBands();
    ObservableList<String> getOutputBands();
    void setNextOperationController(OperationController operationController);
    void updateInput();
    OperationController getNextOperationController();
}
