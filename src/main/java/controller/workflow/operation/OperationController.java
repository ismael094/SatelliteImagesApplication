package controller.workflow.operation;

import model.processing.Operation;

public interface OperationController {
    Operation getOperation();
    void setOperation(Operation operation);
}
