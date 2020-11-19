package model.preprocessing.workflow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import model.preprocessing.workflow.operation.Operator;
import model.preprocessing.workflow.operation.Operation;
import org.bson.types.ObjectId;

import java.util.List;

public interface WorkflowDTO {
    /**
     * Get workflow type
     * @return WorkflowType
     */
    WorkflowType getType();

    /**
     * Get workflow type property
     * @return  WorkflowType property
     */
    ObjectProperty<WorkflowType> typeProperty();

    /**
     * Set workflow type
     * @param type  WorkflowType
     */
    void setType(WorkflowType type);

    /**
     * Set workflow type as string
     * @param type  WorkflowType as string
     */
    void setType(String type);

    /**
     * Get name of the workflow
     * @return name of the workflow
     */
    String getName();

    /**
     * Get name property of the workflow
     * @return name property of the workflow
     */
    StringProperty nameProperty();

    /**
     * set name of the workflow
     * @param name  name of the workflow
     */
    void setName(String name);

    /**
     * Get operations of the workflow
     * @return Workflow operations
     */
    List<Operation> getOperations();

    /**
     * Add operation to workflow
     * @param operation operation to add
     */
    void addOperation(Operation operation);

    /**
     * Get operation by Operator
     * @param operator Operator to get
     * @return Operation
     */
    Operation getOperation(Operator operator);

    /**
     * Remove operation in the workflow
     * @param operation Operation to remove
     */
    void removeOperation(Operation operation);

    /**
     * Set workflow operations
     * @param operations Operations
     */
    void setOperations(List<Operation> operations);

    /**
     * Get Workflow Id
     * @return id of Workflow
     */
    ObjectId getId();

    /**
     * Set id of workflow
     * @param id id of workflow
     */
    void setId(ObjectId id);

    /**
     * Verify if workflow contains operator
     * @param operator Operator
     * @return true if it´s contained; false otherwise
     */
    boolean containsOperation(Operator operator);
}
