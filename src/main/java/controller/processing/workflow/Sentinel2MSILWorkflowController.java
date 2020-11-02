package controller.processing.workflow;

import controller.processing.workflow.operation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.layout.AnchorPane;
import jfxtras.styles.jmetro.JMetroStyleClass;
import model.preprocessing.workflow.WorkflowDTO;
import model.preprocessing.workflow.operation.Operation;
import model.preprocessing.workflow.operation.Operator;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Sentinel2MSILWorkflowController implements Initializable, WorkflowController {
    @FXML
    private Accordion accordion;
    @FXML
    private AnchorPane readOperation;
    @FXML
    private ReadOperationController readOperationController;
    @FXML
    private AnchorPane resampleOperation;
    @FXML
    private ResampleOperationController resampleOperationController;
    @FXML
    private AnchorPane subsetOperation;
    @FXML
    private SubsetOperationController subsetOperationController;
    @FXML
    private AnchorPane writeOperation;
    @FXML
    private WriteSentinel2OperationController writeOperationController;

    private WorkflowDTO workflow;
    private final Map<Operator, OperationController> operationsMap;

    public Sentinel2MSILWorkflowController() {
        operationsMap = new HashMap<>();
    }

    @Override
    public void setWorkflow(WorkflowDTO workflow) {
        this.workflow = workflow;

        if (workflow.getOperations().isEmpty()) {
            addMSILOperations(workflow);
        }

        workflow.getOperations().forEach(operation -> {
            if (operationsMap.containsKey(operation.getName())) {
                OperationController op = operationsMap.get(operation.getName());
                op.setParameters(operation.getParameters());
            }
        });
    }

    private void addMSILOperations(WorkflowDTO workflow) {
        workflow.addOperation(new Operation(Operator.READ, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.RESAMPLE, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.SUBSET, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.WRITE, new HashMap<>()));
    }

    @Override
    public WorkflowDTO getWorkflow() {
        workflow.getOperations().forEach(operation -> {
            if (operationsMap.containsKey(operation.getName())) {
                operation.setParameters(operationsMap.get(operation.getName()).getParameters());
            }
        });
        return workflow;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accordion.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        operationsMap.put(Operator.READ,readOperationController);
        operationsMap.put(Operator.RESAMPLE,resampleOperationController);
        operationsMap.put(Operator.SUBSET,subsetOperationController);
        operationsMap.put(Operator.WRITE,writeOperationController);
    }
}
