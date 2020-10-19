package model;

import model.processing.Operation;
import model.processing.Operator;
import model.processing.Sentinel1GRDDefaultWorkflow;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Sentinel1GRDWorkflow_ {

    private Sentinel1GRDDefaultWorkflow grdWorkflow;

    @Before
    public void init() {
        this.grdWorkflow = new Sentinel1GRDDefaultWorkflow();
    }

    @Test
    public void get_workflow() {
        List<Operation> operations = this.grdWorkflow.getOperations();
        assertThat(operations.size()).isEqualTo(6);
        assertThat(operations.get(0).getName()).isEqualTo(Operator.READ);
        assertThat(operations.get(1).getName()).isEqualTo(Operator.APPLY_ORBIT_FILE);
    }

    @Test
    public void test() {
        System.out.println(grdWorkflow);
    }
}
