package controller.processing.operation;

import controller.processing.workflow.Sentinel1GRDWorkflowController;
import controller.processing.workflow.operation.OperationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
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

public class OrbitOperationControllerTest extends ApplicationTest {
    OperationController controller;
    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Sentinel1GRDWorkflowController.class.getResource("/fxml/OrbitOperationView.fxml"));
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
        controller.setParameters(workflow.getOperation(Operator.APPLY_ORBIT_FILE).getParameters());
        assertThat(controller.getParameters().size()).isEqualTo(2);
        assertThat(controller.getParameters().get("polyDegree")).isEqualTo(3);
    }

    @Test
    public void get_parameters() {
        clickOn("#plyDegree");
        doubleClickOn("#plyDegree");
        write("5");
        type(KeyCode.ENTER);

        assertThat(controller.getParameters().get("polyDegree")).isEqualTo(5);

        clickOn("#plyDegree");
        doubleClickOn("#plyDegree");
        write("");
        type(KeyCode.ENTER);
        assertThat(controller.getParameters().get("polyDegree")).isEqualTo(5);
    }
}
