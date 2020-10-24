package controller.processing.workflow;

import controller.processing.workflow.operation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import jfxtras.styles.jmetro.JMetroStyleClass;
import model.processing.workflow.operation.Operation;
import model.processing.workflow.WorkflowDTO;
import model.processing.workflow.operation.Operator;

import java.net.URL;
import java.util.*;

public class Sentinel1GRDWorkflowController implements Initializable, WorkflowController {
    @FXML
    private Accordion accordion;
    @FXML
    private AnchorPane readOperation;
    @FXML
    private ReadOperationController readOperationController;
    @FXML
    private AnchorPane thermalNoiseRemovalOperation;
    @FXML
    private ThermalNoiseRemovalOperationController thermalNoiseRemovalOperationController;
    @FXML
    private AnchorPane orbitOperation;
    @FXML
    private OrbitOperationController orbitOperationController;
    @FXML
    private ScrollPane calibrationOperation;
    @FXML
    private CalibrationOperationController calibrationOperationController;
    @FXML
    private AnchorPane writeAndReadOperation;
    @FXML
    private WriteAndReadOperationController writeAndReadOperationController;
    @FXML
    private AnchorPane terrainCorrectionOperation;
    @FXML
    private TerrainCorrectionOperationController terrainCorrectionOperationController;
    @FXML
    private AnchorPane subsetOperation;
    @FXML
    private SubsetOperationController subsetOperationController;
    @FXML
    private AnchorPane writeOperation;
    @FXML
    private WriteOperationController writeOperationController;

    private WorkflowDTO workflow;
    private final Map<Operator, OperationController> operationsMap;

    public Sentinel1GRDWorkflowController() {
        operationsMap = new HashMap<>();
    }

    @Override
    public void setWorkflow(WorkflowDTO workflow) {
        this.workflow = workflow;
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
        operationsMap.put(Operator.THERMAL_NOISE_REMOVAL,thermalNoiseRemovalOperationController);
        operationsMap.put(Operator.APPLY_ORBIT_FILE,orbitOperationController);
        operationsMap.put(Operator.CALIBRATION,calibrationOperationController);
        operationsMap.put(Operator.WRITE_AND_READ,writeAndReadOperationController);
        operationsMap.put(Operator.TERRAIN_CORRECTION,terrainCorrectionOperationController);
        operationsMap.put(Operator.SUBSET,subsetOperationController);
        operationsMap.put(Operator.WRITE,writeOperationController);
    }

    public Map<Operator, OperationController> getOperationsMap() {
        return operationsMap;
    }
}
