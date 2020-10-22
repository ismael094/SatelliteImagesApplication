package controller.workflow;

import model.processing.WorkflowDTO;

public interface WorkflowController {
    void setWorkflow(WorkflowDTO workflow);
    WorkflowDTO getWorkflow();
}
