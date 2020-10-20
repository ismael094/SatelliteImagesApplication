package model.processing;

import java.util.List;

public interface Workflow {
    WorkflowType getType();

    String getName();

    List<Operation> getOperations();

    void addOperation(Operation operation);

    Operation getOperation(Operator operator);

    void removeOperation(Operation operation);
}
