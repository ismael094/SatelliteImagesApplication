package controller.processing.workflow;

import controller.processing.workflow.WorkflowController;
import controller.processing.workflow.operation.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
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

public class Sentinel1SLCWorkflowController implements WorkflowController, Initializable {
    @FXML
    private MenuItem terrainFlatteningButton;
    @FXML
    private MenuItem removeTerrainFlatteningButton;
    @FXML
    private Accordion accordion;
    @FXML
    private ScrollPane topSarSplitOperation;
    @FXML
    private TOPSARSplitOperationController topSarSplitOperationController;
    @FXML
    private AnchorPane readOperation;
    @FXML
    private ReadOperationController readOperationController;
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
    private ScrollPane topSarDeburstOperation;
    @FXML
    private TopSarDeburstOperationController topSarDeburstOperationController;
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

    public Sentinel1SLCWorkflowController() {
        operationsMap = new HashMap<>();
    }

    @Override
    public void setWorkflow(WorkflowDTO workflow) {
        this.workflow = workflow;
        if (workflow.getOperations().isEmpty()) {
            addSLCOperations();
        }
        loadOperationsIntroControllers();
    }

    private void loadOperationsIntroControllers() {
        List<Operation> operations = workflow.getOperations();
        for (Operation operation : operations) {
            System.out.println(operation.getName());
            if (operationsMap.containsKey(operation.getName())) {
                OperationController op = operationsMap.get(operation.getName());
                op.setParameters(operation.getParameters());
            }
        }
    }

    private void addSLCOperations() {
        workflow.addOperation(new Operation(Operator.READ, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.TOPSAR_SPLIT, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.APPLY_ORBIT_FILE, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.CALIBRATION, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.WRITE_AND_READ, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.TOPSAR_DEBURST, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.TERRAIN_CORRECTION, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.SUBSET, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.WRITE, new HashMap<>()));
    }

    @Override
    public WorkflowDTO getWorkflow() {
        List<Operation> operations = workflow.getOperations();
        operations.forEach(op->{
            System.out.println(op.getName());
            if (operationsMap.containsKey(op.getName())) {
                op.setParameters(operationsMap.get(op.getName()).getParameters());
            }
        });
        return workflow;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accordion.getStyleClass().add(JMetroStyleClass.BACKGROUND);

        operationsMap.put(Operator.TOPSAR_SPLIT,topSarSplitOperationController);
        operationsMap.put(Operator.READ,readOperationController);
        operationsMap.put(Operator.APPLY_ORBIT_FILE,orbitOperationController);
        operationsMap.put(Operator.CALIBRATION,calibrationOperationController);
        operationsMap.put(Operator.WRITE_AND_READ,writeAndReadOperationController);
        operationsMap.put(Operator.TOPSAR_DEBURST,topSarDeburstOperationController);
        operationsMap.put(Operator.TERRAIN_CORRECTION,terrainCorrectionOperationController);
        operationsMap.put(Operator.SUBSET,subsetOperationController);
        operationsMap.put(Operator.WRITE,writeOperationController);
    }

    public Map<Operator, OperationController> getOperationsMap() {
        return operationsMap;
    }
}
