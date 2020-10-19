package model.processing;

import java.util.List;

public interface Workflow {
    List<Operation> getOperations();
    void addOperation(Operation operation);
    void removeOperation(Operation operation);
}
