package model;

import model.preprocessing.workflow.defaultWorkflow.S2MSI1CDefaultWorkflowDTO;
import model.preprocessing.workflow.operation.Operation;
import model.preprocessing.workflow.operation.Operator;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Sentinel2MSILDefaultWorkflow_ {
    private S2MSI1CDefaultWorkflowDTO msilDefaultWorkflowDTO;

    @Before
    public void init() {
        this.msilDefaultWorkflowDTO = new S2MSI1CDefaultWorkflowDTO();
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
