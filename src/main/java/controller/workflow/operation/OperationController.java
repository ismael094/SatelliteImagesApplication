package controller.workflow.operation;

import javafx.collections.ObservableList;
import model.processing.Operation;


public interface OperationController {
    Operation getOperation();
    void setOperation(Operation operation);
    void inputBands(ObservableList<String> inputBands);
    ObservableList<String> getOutputBands();
    void setNextOperationController(OperationController operationController);
    void updateInput();
}
