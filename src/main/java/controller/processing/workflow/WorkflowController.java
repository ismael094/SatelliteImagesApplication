package controller.processing.workflow;

import model.preprocessing.workflow.WorkflowDTO;

/**
 * Interface to manage workflowControllers
 */
public interface WorkflowController {
    /**
     * Set workflow
     * @param workflow Workflow
     */
    void setWorkflow(WorkflowDTO workflow);

    /**
     * Get Workflow
     * @return workflow
     */
    WorkflowDTO getWorkflow();
}
