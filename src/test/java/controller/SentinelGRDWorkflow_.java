package controller;

import controller.processing.workflow.Sentinel1GRDWorkflowController;
import controller.processing.workflow.operation.CalibrationOperationController;
import controller.processing.workflow.operation.OperationController;
import controller.processing.workflow.operation.WriteAndReadOperationController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import model.processing.*;
import model.processing.workflow.GeneralWorkflowDTO;
import model.processing.workflow.Operation;
import model.processing.workflow.WorkflowType;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class SentinelGRDWorkflow_ extends ApplicationTest {
    Sentinel1GRDWorkflowController controller;
    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Sentinel1GRDWorkflowController.class.getResource("/fxml/GRDWorkflowVi.fxml"));
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
        interact(() -> controller.setWorkflow(new Sentinel1GRDDefaultWorkflowDTO()));

        assertThat(controller.getWorkflow().getType()).isEqualTo(WorkflowType.GRD);
    }

    @Test
    public void creates_map_with_operation_controller () {
        interact(() -> controller.setWorkflow(new Sentinel1GRDDefaultWorkflowDTO()));

        assertThat(controller.getOperationsMap().size()).isGreaterThan(0);
        assertThat(controller.getOperationsMap().get(Operator.CALIBRATION)).isNotNull();
    }

    @Test
    public void inject_next_controller() {
        GeneralWorkflowDTO workflow = new GeneralWorkflowDTO(new SimpleStringProperty("default"), new SimpleObjectProperty<>(WorkflowType.GRD));
        workflow.addOperation(new Operation(Operator.APPLY_ORBIT_FILE, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.CALIBRATION, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.WRITE_AND_READ, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.TERRAIN_CORRECTION, new HashMap<>()));
        workflow.addOperation(new Operation(Operator.WRITE, new HashMap<>()));


        interact(() -> controller.setWorkflow(workflow));

        OperationController operationController = controller.getOperationsMap().get(Operator.APPLY_ORBIT_FILE);
        assertThat(operationController).isNotNull();
        assertThat(operationController.getNextOperationController().getOperation().getName()).isEqualTo(Operator.CALIBRATION);
        CalibrationOperationController cOp = (CalibrationOperationController) operationController.getNextOperationController();
        assertThat(operationController.getOutputBands()).isEqualTo(cOp.getCalibrationSourceBands());
    }

    @Test
    public void calibration_to_save_dim() {
        interact(() -> controller.setWorkflow(new Sentinel1GRDDefaultWorkflowDTO()));
        OperationController operationController = controller.getOperationsMap().get(Operator.CALIBRATION);

        assertThat(operationController).isNotNull();
        assertThat(operationController.getNextOperationController().getOperation().getName()).isEqualTo(Operator.WRITE_AND_READ);

        WriteAndReadOperationController writeAndRead = (WriteAndReadOperationController) operationController.getNextOperationController();

        assertThat(operationController.getOutputBands()).isEqualTo(writeAndRead.getOutputBands());

        clickOn("#calibrationPane");
        clickOn("#outputBeta");
        clickOn("#outputGamma");

        //((CalibrationOperationController)operationController).getOutputGamma().set(true);
        //((CalibrationOperationController)operationController).getOutputBeta().set(false);

        assertThat(((CalibrationOperationController)operationController).getOutputGamma().get()).isTrue();
        assertThat(((CalibrationOperationController)operationController).getOutputBeta().get()).isFalse();
        assertThat(operationController.getOutputBands()).isEqualTo(writeAndRead.getOutputBands());
    }
}
