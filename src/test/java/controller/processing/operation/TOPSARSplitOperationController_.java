package controller.processing.operation;

import controller.processing.workflow.Sentinel1GRDWorkflowController;
import controller.processing.workflow.operation.OperationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import model.processing.workflow.WorkflowDTO;
import model.processing.workflow.defaultWorkflow.SLCDefaultWorkflowDTO;
import model.processing.workflow.operation.Operator;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class TOPSARSplitOperationController_ extends ApplicationTest {
    OperationController controller;
    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Sentinel1GRDWorkflowController.class.getResource("/fxml/TopSarSplitOperationView.fxml"));
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
        WorkflowDTO workflow = new SLCDefaultWorkflowDTO();
        controller.setParameters(workflow.getOperation(Operator.TOPSAR_SPLIT).getParameters());
        assertThat(controller.getParameters().size()).isEqualTo(4);
        assertThat(controller.getParameters().get("subswath")).isEqualTo("IW1");
    }

    @Test
    public void set_new_operation() {
        clickOn("#subswath");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        release(new KeyCode[]{});

        clickOn("#firstBurstIndex");
        type(KeyCode.DOWN,3);
        type(KeyCode.ENTER);
        release(new KeyCode[]{});

        clickOn("#lastBurstIndex");
        type(KeyCode.UP,1);
        type(KeyCode.ENTER);
        release(new KeyCode[]{});

        assertThat(controller.getParameters().get("subswath")).isEqualTo("IW2");
        assertThat(controller.getParameters().get("firstBurstIndex")).isEqualTo("4");
        assertThat(controller.getParameters().get("lastBurstIndex")).isEqualTo("8");
    }

    @Test
    public void first_burst_must_be_lesser_than_last() {
        clickOn("#lastBurstIndex");
        type(KeyCode.UP,5);
        type(KeyCode.ENTER);
        release(new KeyCode[]{});

        clickOn("#firstBurstIndex");
        type(KeyCode.DOWN,3);
        type(KeyCode.ENTER);
        release(new KeyCode[]{});

        assertThat(controller.getParameters().get("firstBurstIndex")).isEqualTo("1");
        assertThat(controller.getParameters().get("lastBurstIndex")).isEqualTo("4");
    }

    @Test
    public void last_burst_must_be_greater_than_last() {
        clickOn("#firstBurstIndex");
        type(KeyCode.DOWN,5);
        type(KeyCode.ENTER);
        release(new KeyCode[]{});

        clickOn("#lastBurstIndex");
        type(KeyCode.UP,5);
        type(KeyCode.ENTER);
        release(new KeyCode[]{});

        assertThat(controller.getParameters().get("firstBurstIndex")).isEqualTo("6");
        assertThat(controller.getParameters().get("lastBurstIndex")).isEqualTo("9");
    }
}
