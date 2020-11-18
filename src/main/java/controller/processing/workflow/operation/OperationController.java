package controller.processing.workflow.operation;

import java.util.Map;

public interface OperationController {
    /**
     * Get parameters of the operation
     * @return Map with parameters
     */
    Map<String,Object> getParameters();

    /**
     * Set operation parameters
     * @param parameters Operation Parameters
     */
    void setParameters(Map<String,Object> parameters);
}
