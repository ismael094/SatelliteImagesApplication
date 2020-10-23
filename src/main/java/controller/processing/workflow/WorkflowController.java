package controller.processing.workflow;

import model.processing.workflow.WorkflowDTO;

public interface WorkflowController {
    void setWorkflow(WorkflowDTO workflow);
    WorkflowDTO getWorkflow();
}
