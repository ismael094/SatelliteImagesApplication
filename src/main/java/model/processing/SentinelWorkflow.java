package model.processing;

import java.util.LinkedList;
import java.util.List;

public class SentinelWorkflow implements Workflow{
    private final List<Operation> operations;
    private final String name;
    private final WorkflowType type;

    public SentinelWorkflow(String name, WorkflowType type) {
        this.name = name;
        this.type = type;
        operations = new LinkedList<>();
    }

    @Override
    public WorkflowType getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Operation> getOperations() {
        return operations;
    }

    @Override
    public void addOperation(Operation operation) {
        operations.add(operation);
    }

    @Override
    public Operation getOperation(Operator operator) {
        return operations.stream()
                .filter(o->o.getName() == operator)
                .findAny()
                .orElse(null);
    }

    @Override
    public void removeOperation(Operation operation) {
        operations.remove(operation);
    }
}
