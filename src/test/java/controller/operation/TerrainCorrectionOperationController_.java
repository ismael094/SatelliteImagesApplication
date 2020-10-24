package controller.operation;

import controller.processing.workflow.Sentinel1GRDWorkflowController;
import controller.processing.workflow.operation.OperationController;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import model.processing.workflow.operation.Operation;
import model.processing.workflow.operation.Operator;
import model.processing.workflow.Sentinel1GRDDefaultWorkflowDTO;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class TerrainCorrectionOperationController_ extends ApplicationTest {
    OperationController controller;
    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Sentinel1GRDWorkflowController.class.getResource("/fxml/TerrainCorrectionView.fxml"));
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
    public void set_operation() {
        Sentinel1GRDDefaultWorkflowDTO workflow = new Sentinel1GRDDefaultWorkflowDTO();
        interact(() -> {
            controller.setOperation(workflow.getOperation(Operator.TERRAIN_CORRECTION));
        });
        assertThat(controller.getOperation().getName()).isEqualTo(Operator.TERRAIN_CORRECTION);
        assertThat(controller.getOutputBands().toString()).isEqualTo(controller.getInputBands().toString());
    }

    @Test
    public void select_source_bands() {
        Sentinel1GRDDefaultWorkflowDTO workflow = new Sentinel1GRDDefaultWorkflowDTO();

        interact(() -> {
            controller.setOperation(workflow.getOperation(Operator.TERRAIN_CORRECTION));
            controller.setInputBands(FXCollections.observableArrayList("Beta0_VV","Beta0_VH"));
        });

        clickOn("#correctionSourceBands");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);

        Operation operation = controller.getOperation();
        assertThat(operation.getParameters().get("sourceBands")).isEqualTo("Beta0_VH");

        assertThat(controller.getOutputBands().size()).isGreaterThan(0);
        assertThat(controller.getOutputBands().get(0)).isEqualTo("Beta0_VH");
    }

    @Test
    public void get_parameters() {
        Sentinel1GRDDefaultWorkflowDTO workflow = new Sentinel1GRDDefaultWorkflowDTO();

        interact(() -> {
            controller.setOperation(workflow.getOperation(Operator.TERRAIN_CORRECTION));
            controller.setInputBands(FXCollections.observableArrayList("Beta0_VV","Beta0_VH"));
            clickOn("#correctionSourceBands");
            clickOn("#demName");
            type(KeyCode.DOWN);
            type(KeyCode.ENTER);
        });

        Map<String, Object> parameters = controller.getOperation().getParameters();
        assertThat(parameters.get("demName")).isEqualTo("SRTM 1Sec HGT");
        assertThat(parameters.get("demResamplingMethod")).isEqualTo("NEAREST_NEIGHBOUR");
        assertThat(parameters.get("imgResamplingMethod")).isEqualTo("NEAREST_NEIGHBOUR");
    }



}
