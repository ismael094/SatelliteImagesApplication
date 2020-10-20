package model.processing;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.util.LinkedList;
import java.util.List;

public class SentinelWorkflow implements Workflow{
    private final List<Operation> operations;
    private StringProperty name;
    private ObjectProperty<WorkflowType> type;

    public SentinelWorkflow(StringProperty name, ObjectProperty<WorkflowType> type) {
        this.name = name;
        this.type = type;
        operations = new LinkedList<>();
    }

    @Override
    public WorkflowType getType() {
        return type.get();
    }

    @Override
    public ObjectProperty<WorkflowType> typeProperty() {
        return type;
    }

    @Override
    public void setType(WorkflowType type) {
        this.type.set(type);
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public StringProperty nameProperty() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name.set(name);
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

    @Override
    public String toString() {
        return "SentinelWorkflow{" +
                "operations=" + operations +
                ", name=" + name +
                ", type=" + type +
                '}';
    }
}
