package model;

import model.preprocessing.workflow.operation.Operation;
import model.preprocessing.workflow.operation.Operator;
import model.preprocessing.workflow.defaultWorkflow.GRDDefaultWorkflowDTO;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Sentinel1GRDWorkflow_ {

    private GRDDefaultWorkflowDTO grdWorkflow;

    @Before
    public void init() {
        this.grdWorkflow = new GRDDefaultWorkflowDTO();
    }

    @Test
    public void get_workflow() {
        List<Operation> operations = this.grdWorkflow.getOperations();
        assertThat(operations.size()).isEqualTo(8);
        assertThat(operations.get(0).getName()).isEqualTo(Operator.READ);
        assertThat(operations.get(1).getName()).isEqualTo(Operator.APPLY_ORBIT_FILE);
    }

    @Test
    public void test() {
        System.out.println(grdWorkflow);
    }
}
