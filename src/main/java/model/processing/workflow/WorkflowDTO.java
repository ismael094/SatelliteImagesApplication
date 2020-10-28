package model.processing.workflow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import model.processing.workflow.operation.Operator;
import model.processing.workflow.operation.Operation;
import org.bson.types.ObjectId;

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

    ObjectId getId();

    void setId(ObjectId id);

    boolean containsOperation(Operator operator);
}
