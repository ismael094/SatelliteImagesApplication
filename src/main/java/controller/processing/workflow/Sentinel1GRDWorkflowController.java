package controller.processing.workflow;

import controller.processing.workflow.operation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import jfxtras.styles.jmetro.JMetroStyleClass;
import model.preprocessing.workflow.operation.Operation;
import model.preprocessing.workflow.WorkflowDTO;
import model.preprocessing.workflow.operation.Operator;

import java.net.URL;
import java.util.*;

public class Sentinel1GRDWorkflowController implements Initializable, WorkflowController {
    @FXML
    private TitledPane flatteningPane;
    @FXML
    private MenuItem terrainFlatteningButton;
    @FXML
    private MenuItem removeTerrainFlatteningButton;
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
    private AnchorPane terrainFlatteningOperation;
    @FXML
    private TerrainFlatteningOperationController terrainFlatteningOperationController;
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
        if (workflow.getOperations().isEmpty()) {
            addGRDOperations(this.workflow);
        }
        loadOperationsIntroControllers(this.workflow);
    }

    private void loadOperationsIntroControllers(WorkflowDTO workflow) {
        List<Operation> operations = workflow.getOperations();
        for (Operation operation : operations) {
            if (operationsMap.containsKey(operation.getName())) {
                OperationController op = operationsMap.get(operation.getName());
                op.setParameters(operation.getParameters());
                if (operation.getName() == Operator.TERRAIN_FLATTENING && !accordion.getPanes().contains(flatteningPane))
                    accordion.getPanes().add(5, flatteningPane);
            }
        }
    }

    private void addGRDOperations(WorkflowDTO workflow) {
        workflow.addOperation(new Operation(Operator.READ, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.THERMAL_NOISE_REMOVAL, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.APPLY_ORBIT_FILE, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.CALIBRATION, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.WRITE_AND_READ, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.TERRAIN_CORRECTION, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.SUBSET, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.WRITE, new HashMap<>()));
    }

    @Override
    public WorkflowDTO getWorkflow() {
        List<Operation> operations = workflow.getOperations();
        operations.forEach(op->{
            if (operationsMap.containsKey(op.getName())) {
                op.setParameters(operationsMap.get(op.getName()).getParameters());
            }
        });
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
        operationsMap.put(Operator.TERRAIN_FLATTENING,terrainFlatteningOperationController);
        accordion.getPanes().remove(flatteningPane);

        terrainFlatteningButton.setOnAction(e->{
            if (accordion.getPanes().contains(flatteningPane))
                return;
            accordion.getPanes().add(5,flatteningPane);
            WorkflowDTO workflow = getWorkflow();
            Operation operation = new Operation(Operator.TERRAIN_FLATTENING, new HashMap<>());
            LinkedList<Operation> operations1 = new LinkedList<>(workflow.getOperations());
            operations1.add(5,operation);
            workflow.setOperations(new LinkedList<>(operations1));
            calibrationOperationController.fixOutputBeta(true);
            loadOperationsIntroControllers(workflow);
        });

        removeTerrainFlatteningButton.setOnAction(e->{
            operationsMap.remove(Operator.TERRAIN_FLATTENING);
            WorkflowDTO workflow = getWorkflow();
            calibrationOperationController.fixOutputBeta(false);
            Platform.runLater(()->{
                workflow.getOperations().remove(5);
                loadOperationsIntroControllers(workflow);
                accordion.getPanes().remove(flatteningPane);
            });
        });
    }

    private void toggleFlatteningPane(boolean b) {
        flatteningPane.setVisible(b);
        flatteningPane.setManaged(b);
    }

    public Map<Operator, OperationController> getOperationsMap() {
        return operationsMap;
    }
}
