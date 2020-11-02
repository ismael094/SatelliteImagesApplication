package services.entities;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import model.preprocessing.workflow.operation.Operation;
import model.preprocessing.workflow.WorkflowType;
import org.bson.types.ObjectId;

import java.util.List;

@Entity("workflows")
public class Workflow {
    @Id
    private ObjectId id;
    private List<Operation> operations;
    private WorkflowType type;
    private String name;
    public Workflow() {
    }

    public Workflow(List<Operation> operations, WorkflowType type, String name) {
        this.operations = operations;
        this.type = type;
        this.name = name;
    }

    public Workflow(List<Operation> operations, String type, String name) {
        this.operations = operations;
        this.type = WorkflowType.valueOf(type);
        this.name = name;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }

    public WorkflowType getType() {
        return type;
    }

    public void setType(WorkflowType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
