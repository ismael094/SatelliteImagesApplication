package model;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.processing.workflow.defaultWorkflow.GRDDefaultWorkflowDTO;
import model.processing.workflow.operation.Operation;
import model.processing.workflow.operation.Operator;
import model.processing.workflow.GeneralWorkflowDTO;
import model.processing.workflow.WorkflowDTO;
import model.processing.workflow.WorkflowType;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class Workflow_ {
    private GeneralWorkflowDTO workflow;

    @Before
    public void init() {
        workflow = new GeneralWorkflowDTO(new SimpleStringProperty("name"), new SimpleObjectProperty<>(WorkflowType.GRD));
    }

    @Test
    public void test_constructor() {
        assertThat(workflow.getName()).isEqualTo("name");
        assertThat(workflow.getType()).isEqualTo(WorkflowType.GRD);
        assertThat(workflow.getOperations()).isNotNull();
    }

    @Test
    public void add_operation() {
        Operation op = mock(Operation.class);
        doReturn(Operator.APPLY_ORBIT_FILE).when(op).getName();

        workflow.addOperation(op);
        assertThat(workflow.getOperations().size()).isEqualTo(1);
        assertThat(workflow.getOperations().get(0).getName()).isEqualTo(op.getName());
        assertThat(workflow.getOperation(Operator.APPLY_ORBIT_FILE)).isEqualTo(op);
        workflow.removeOperation(op);
        assertThat(workflow.getOperations().size()).isEqualTo(0);
    }

    @Test
    public void add_and_remove_operation() {
        WorkflowDTO wf = new GRDDefaultWorkflowDTO();
        Operation operation = new Operation(Operator.TERRAIN_FLATTENING, new HashMap<>());
        wf.getOperations().add(5,operation);
        assertThat(wf.getOperations().get(4).getName()).isEqualTo(Operator.WRITE_AND_READ);
        assertThat(wf.getOperations().get(5).getName()).isEqualTo(Operator.TERRAIN_FLATTENING);
        assertThat(wf.getOperations().get(6).getName()).isEqualTo(Operator.TERRAIN_CORRECTION);
        wf.getOperations().remove(5);
        assertThat(wf.getOperations().get(4).getName()).isEqualTo(Operator.WRITE_AND_READ);
        assertThat(wf.getOperations().get(5).getName()).isEqualTo(Operator.TERRAIN_CORRECTION);
    }

    @Test
    public void contains_operation() {
        WorkflowDTO wf = new GRDDefaultWorkflowDTO();
        Operation operation = new Operation(Operator.TERRAIN_FLATTENING, new HashMap<>());
        assertThat(wf.containsOperation(Operator.TERRAIN_FLATTENING)).isFalse();
    }

}
