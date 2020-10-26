package controller.processing.workflow;

import controller.processing.workflow.operation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import jfxtras.styles.jmetro.JMetroStyleClass;
import model.processing.workflow.WorkflowDTO;
import model.processing.workflow.operation.Operation;
import model.processing.workflow.operation.Operator;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
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
        List<Operation> operations = workflow.getOperations();
        for (int i = 0; i < operations.size(); i++) {
            if (operationsMap.containsKey(operations.get(i).getName())) {
                OperationController op = operationsMap.get(operations.get(i).getName());
                op.setOperation(operations.get(i));
                if (i < operations.size()-1) {
                    op.setNextOperationController(operationsMap.get(operations.get(i+1).getName()));
                }
            }
        }
    }

    private void addMSILOperations(WorkflowDTO workflow) {
        workflow.addOperation(new Operation(Operator.READ, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.RESAMPLE, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.SUBSET, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.WRITE, new HashMap<>()));
    }

    @Override
    public WorkflowDTO getWorkflow() {
        List<Operation> operations = workflow.getOperations();
        for (Operation operation : operations) {
            if (operationsMap.containsKey(operation.getName())) {
                OperationController op = operationsMap.get(operation.getName());
                op.getOperation();
            }
        }
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
