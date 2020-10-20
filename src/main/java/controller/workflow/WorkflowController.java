package controller.workflow;

import model.processing.Workflow;

public interface WorkflowController {
    void setWorkflow(Workflow workflow);
    Workflow getWorkflow();
}
