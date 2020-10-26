package model;

import model.processing.workflow.Sentinel1GRDDefaultWorkflowDTO;
import model.processing.workflow.Sentinel2MSILDefaultWorkflowDTO;
import model.processing.workflow.operation.Operation;
import model.processing.workflow.operation.Operator;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Sentinel2MSILDefaultWorkflow_ {
    private Sentinel2MSILDefaultWorkflowDTO msilDefaultWorkflowDTO;

    @Before
    public void init() {
        this.msilDefaultWorkflowDTO = new Sentinel2MSILDefaultWorkflowDTO();
    }

    @Test
    public void get_workflow() {
        List<Operation> operations = this.msilDefaultWorkflowDTO.getOperations();
        assertThat(operations.size()).isEqualTo(4);
        assertThat(operations.get(0).getName()).isEqualTo(Operator.READ);
        assertThat(operations.get(1).getName()).isEqualTo(Operator.RESAMPLE);
        assertThat(operations.get(3).getParameters().get("red")).isEqualTo("B4");
    }
}
