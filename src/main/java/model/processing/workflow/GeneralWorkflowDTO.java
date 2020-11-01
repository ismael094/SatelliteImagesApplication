package model.processing.workflow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.processing.workflow.operation.Operator;
import model.processing.workflow.operation.Operation;
import org.bson.types.ObjectId;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class GeneralWorkflowDTO implements WorkflowDTO {
    private List<Operation> operations;
    private StringProperty name;
    private ObjectProperty<WorkflowType> type;
    private ObjectId id;

    public GeneralWorkflowDTO(StringProperty name, ObjectProperty<WorkflowType> type) {
        this.name = name;
        this.type = type;
        operations = new LinkedList<>();
    }

    public GeneralWorkflowDTO() {
        this.name = new SimpleStringProperty();
        this.type = new SimpleObjectProperty<>();
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
    public void setType(String type) {
        this.type.set(WorkflowType.valueOf(type));
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
    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    @Override
    public boolean containsOperation(Operator operator) {
        return operations.stream()
                .filter(o->o.getName()==operator)
                .findAny()
                .orElse(null) != null;
    }

    @Override
    public ObjectId getId() {
        return id;
    }

    @Override
    public void setId(ObjectId id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GeneralWorkflow{" +
                "operations=" + operations +
                ", name=" + name +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeneralWorkflowDTO)) return false;
        GeneralWorkflowDTO that = (GeneralWorkflowDTO) o;
        if ((id == null || that.id == null))
            return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
