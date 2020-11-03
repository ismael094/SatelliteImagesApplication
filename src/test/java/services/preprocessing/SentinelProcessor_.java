package services.preprocessing;

import model.preprocessing.workflow.WorkflowDTO;
import model.preprocessing.workflow.WorkflowType;
import model.preprocessing.workflow.defaultWorkflow.GRDDefaultWorkflowDTO;
import model.preprocessing.workflow.defaultWorkflow.S2MSI2ADefaultWorkflowDTO;
import model.preprocessing.workflow.defaultWorkflow.SLCDefaultWorkflowDTO;
import org.junit.Test;
import services.processing.processors.SentinelProcessor;

import static org.assertj.core.api.Assertions.assertThat;

public class SentinelProcessor_ {

    @Test
    public void get_default_workflow() {
        SentinelProcessor sentinelProcessor = new SentinelProcessor();
        WorkflowDTO workflow = sentinelProcessor.getWorkflow(WorkflowType.GRD);
        assertThat(workflow).isNotNull().isInstanceOf(GRDDefaultWorkflowDTO.class);
    }

    @Test
    public void get_default_workflow_s2msi2a() {
        SentinelProcessor sentinelProcessor = new SentinelProcessor();
        WorkflowDTO workflow = sentinelProcessor.getWorkflow(WorkflowType.S2MSI2A);
        assertThat(workflow).isNotNull().isInstanceOf(S2MSI2ADefaultWorkflowDTO.class);
    }
}
