package model.processing;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.util.List;

public interface WorkflowDTO {
    WorkflowType getType();

    ObjectProperty<WorkflowType> typeProperty();

    void setType(WorkflowType type);

    void setType(String type);

    String getName();

    StringProperty nameProperty();

    void setName(String name);

    List<Operation> getOperations();

    void addOperation(Operation operation);

    Operation getOperation(Operator operator);

    void removeOperation(Operation operation);

    void setOperations(List<Operation> operations);
}
