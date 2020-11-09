package controller.processing.operation;

import controller.processing.workflow.Sentinel1GRDWorkflowController;
import controller.processing.workflow.operation.OperationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import model.preprocessing.workflow.operation.Operator;
import model.preprocessing.workflow.defaultWorkflow.GRDDefaultWorkflowDTO;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

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
    public void set_parameters() {
        GRDDefaultWorkflowDTO workflow = new GRDDefaultWorkflowDTO();
        interact(() -> {
            controller.setParameters(workflow.getOperation(Operator.TERRAIN_CORRECTION).getParameters());
        });

        assertThat(controller.getParameters().get("demName")).isEqualTo("SRTM 3Sec");
    }

    @Test
    public void get_parameters() {
        GRDDefaultWorkflowDTO workflow = new GRDDefaultWorkflowDTO();

        interact(() -> {
            controller.setParameters(workflow.getOperation(Operator.TERRAIN_CORRECTION).getParameters());
        });

        clickOn("#demName");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);

        Map<String, Object> parameters = controller.getParameters();
        assertThat(parameters.get("demName")).isEqualTo("SRTM 1Sec HGT");
        assertThat(parameters.get("demResamplingMethod")).isEqualTo("NEAREST_NEIGHBOUR");
        assertThat(parameters.get("imgResamplingMethod")).isEqualTo("NEAREST_NEIGHBOUR");
    }
}
