package model;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.processing.Operation;
import model.processing.Operator;
import model.processing.SentinelWorkflow;
import model.processing.WorkflowType;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class Workflow_ {
    private SentinelWorkflow workflow;

    @Before
    public void init() {
        workflow = new SentinelWorkflow(new SimpleStringProperty("name"), new SimpleObjectProperty<>(WorkflowType.GRD));
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
}
