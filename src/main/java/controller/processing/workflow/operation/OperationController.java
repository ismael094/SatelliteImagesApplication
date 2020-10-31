package controller.processing.workflow.operation;

import java.util.Map;


public interface OperationController {
    Map<String,Object> getParameters();
    void setParameters(Map<String,Object> parameters);
}
