package controller.workflow;

import com.jfoenix.controls.JFXCheckBox;
import controller.workflow.operation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import jfxtras.styles.jmetro.JMetroStyleClass;
import model.processing.Operator;
import model.processing.Sentinel1GRDDefaultWorkflow;
import model.processing.Workflow;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class Sentinel1GRDWorkflowController implements Initializable {
    @FXML
    public ListView<Workflow> workflowList;
    @FXML
    private Accordion accordion;
    @FXML
    private AnchorPane orbitOperation;
    @FXML
    private OrbitOperationController orbitOperationController;
    @FXML
    private AnchorPane calibrationOperation;
    @FXML
    private CalibrationOperationController calibrationOperationController;
    @FXML
    private AnchorPane terrainCorrectionOperation;
    @FXML
    private TerrainCorrectionOperationController terrainCorrectionOperationController;
    @FXML
    private AnchorPane writeOperation;
    @FXML
    private WriteOperationController writeOperationController;
    private Workflow workflow;
    private Map<Operator, OperationController> operationsMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accordion.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        System.out.println(orbitOperation);
        System.out.println(orbitOperationController);

        this.operationsMap = new HashMap<>();
        operationsMap.put(Operator.APPLY_ORBIT_FILE,orbitOperationController);
        operationsMap.put(Operator.CALIBRATION,calibrationOperationController);
        operationsMap.put(Operator.TERRAIN_CORRECTION,terrainCorrectionOperationController);
        operationsMap.put(Operator.WRITE,writeOperationController);
        workflowList.getItems().add(new Sentinel1GRDDefaultWorkflow());
        setWorkflow(new Sentinel1GRDDefaultWorkflow());
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
        workflow.getOperations().forEach(o->{
            if (operationsMap.containsKey(o.getName()))
                operationsMap.get(o.getName()).setOperation(o);
        });
    }

    public Workflow getWorkflow() {
        return workflow;
    }
}
