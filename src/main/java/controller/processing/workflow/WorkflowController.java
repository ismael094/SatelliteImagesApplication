package controller.processing.workflow;

import model.preprocessing.workflow.WorkflowDTO;

public interface WorkflowController {
    void setWorkflow(WorkflowDTO workflow);
    WorkflowDTO getWorkflow();
}
