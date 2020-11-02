package controller;

import controller.processing.workflow.Sentinel1GRDWorkflowController;
import controller.processing.workflow.operation.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import model.preprocessing.workflow.GeneralWorkflowDTO;
import model.preprocessing.workflow.operation.Operation;
import model.preprocessing.workflow.defaultWorkflow.GRDDefaultWorkflowDTO;
import model.preprocessing.workflow.WorkflowType;
import model.preprocessing.workflow.operation.Operator;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class Sentinel1GRDWorkflowController_ extends ApplicationTest {
    Sentinel1GRDWorkflowController controller;
    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Sentinel1GRDWorkflowController.class.getResource("/fxml/Sentinel1GRDWorkflowView.fxml"));
        Parent mainNode = fxmlLoader.load();
        controller = fxmlLoader.getController();
        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void set_workflow () {
        interact(() -> controller.setWorkflow(new GRDDefaultWorkflowDTO()));

        assertThat(controller.getWorkflow().getType()).isEqualTo(WorkflowType.GRD);
    }

    @Test
    public void creates_map_with_operation_controller () {
        interact(() -> controller.setWorkflow(new GRDDefaultWorkflowDTO()));

        assertThat(controller.getOperationsMap().size()).isGreaterThan(0);
        assertThat(controller.getOperationsMap().get(Operator.CALIBRATION)).isNotNull();
    }

    @Test
    public void inject_next_controller() {

        GeneralWorkflowDTO workflow = new GeneralWorkflowDTO(new SimpleStringProperty("default"), new SimpleObjectProperty<>(WorkflowType.GRD));
        workflow.addOperation(new Operation(Operator.READ, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.THERMAL_NOISE_REMOVAL, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.APPLY_ORBIT_FILE, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.CALIBRATION, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.WRITE_AND_READ, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.TERRAIN_CORRECTION, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.SUBSET, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.WRITE, new HashMap<>()));


        interact(() -> controller.setWorkflow(workflow));

        OperationController orbit = controller.getOperationsMap().get(Operator.APPLY_ORBIT_FILE);
        assertThat(orbit).isNotNull();
    }

    @Test
    public void calibration_to_write_and_read_injection() {

        GeneralWorkflowDTO workflow = new GeneralWorkflowDTO(new SimpleStringProperty("default"), new SimpleObjectProperty<>(WorkflowType.GRD));
        workflow.addOperation(new Operation(Operator.READ, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.THERMAL_NOISE_REMOVAL, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.APPLY_ORBIT_FILE, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.CALIBRATION, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.WRITE_AND_READ, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.TERRAIN_CORRECTION, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.SUBSET, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.WRITE, new HashMap<>()));


        interact(() -> controller.setWorkflow(workflow));

        assertThat(controller.getOperationsMap().get(Operator.CALIBRATION)).isInstanceOf(CalibrationOperationController.class);
        assertThat(controller.getOperationsMap().get(Operator.TERRAIN_CORRECTION)).isInstanceOf(TerrainCorrectionOperationController.class);
        assertThat(controller.getOperationsMap().get(Operator.WRITE)).isInstanceOf(WriteOperationController.class);
        assertThat(controller.getOperationsMap().get(Operator.WRITE).getParameters().get("formatName")).isEqualTo("GeoTIFF");
    }

    @Test
    public void add_flat_operation() {
        interact(() -> controller.setWorkflow(new GRDDefaultWorkflowDTO()));
        OperationController operationController = controller.getOperationsMap().get(Operator.CALIBRATION);

        assertThat(operationController).isNotNull();
        assertThat(controller.getWorkflow().getOperations().size()).isEqualTo(8);
        assertThat(controller.getWorkflow().getOperations().get(5).getName()).isEqualTo(Operator.TERRAIN_CORRECTION);

        clickOn("#add");
        clickOn("#terrainFlatteningButton");

        assertThat(controller.getWorkflow().getOperations().size()).isEqualTo(9);
        assertThat(controller.getWorkflow().getOperations().get(5).getName()).isEqualTo(Operator.TERRAIN_FLATTENING);
    }

    @Test
    public void remove_flat_operation() {
        interact(() -> controller.setWorkflow(new GRDDefaultWorkflowDTO()));
        OperationController operationController = controller.getOperationsMap().get(Operator.CALIBRATION);

        clickOn("#add");
        clickOn("#terrainFlatteningButton");

        assertThat(controller.getWorkflow().getOperations().size()).isEqualTo(9);
        assertThat(controller.getWorkflow().getOperations().get(5).getName()).isEqualTo(Operator.TERRAIN_FLATTENING);


        clickOn("#remove");
        clickOn("#removeTerrainFlatteningButton");

        assertThat(controller.getWorkflow().getOperations().size()).isEqualTo(8);
        assertThat(controller.getWorkflow().getOperations().get(5).getName()).isEqualTo(Operator.TERRAIN_CORRECTION);
    }

    @Test
    public void flat_operation_beta_activated() {
        interact(() -> controller.setWorkflow(new GRDDefaultWorkflowDTO()));
        OperationController operationController = controller.getOperationsMap().get(Operator.CALIBRATION);

        clickOn("#add");
        clickOn("#terrainFlatteningButton");

        assertThat(controller.getWorkflow().getOperations().size()).isEqualTo(9);
        assertThat(controller.getWorkflow().getOperations().get(5).getName()).isEqualTo(Operator.TERRAIN_FLATTENING);


        clickOn("#calibrationPane");
        clickOn("#outputBeta");

        OperationController operationController1 = controller.getOperationsMap().get(Operator.CALIBRATION);

        assertThat(operationController1.getParameters().get("outputBetaBand")).isEqualTo(true);

        clickOn("#outputBeta");
        assertThat(operationController1.getParameters().get("outputBetaBand")).isEqualTo(true);


    }

    @Test
    public void flat_operation_beta_deactivated() {
        interact(() -> controller.setWorkflow(new GRDDefaultWorkflowDTO()));
        OperationController operationController = controller.getOperationsMap().get(Operator.CALIBRATION);

        clickOn("#add");
        clickOn("#terrainFlatteningButton");

        clickOn("#calibrationPane");
        clickOn("#outputBeta");

        clickOn("#outputBeta");

        clickOn("#remove");
        clickOn("#removeTerrainFlatteningButton");

        OperationController operationController1 = controller.getOperationsMap().get(Operator.CALIBRATION);

        assertThat(operationController1.getParameters().get("outputBetaBand")).isEqualTo(true);

        clickOn("#outputBeta");

        assertThat(operationController1.getParameters().get("outputBetaBand")).isEqualTo(false);


    }

}
