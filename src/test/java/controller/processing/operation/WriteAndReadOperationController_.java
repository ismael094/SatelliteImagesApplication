package controller.processing.operation;

import controller.processing.workflow.Sentinel1GRDWorkflowController;
import controller.processing.workflow.operation.OperationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import model.processing.workflow.operation.Operator;
import model.processing.workflow.defaultWorkflow.GRDDefaultWorkflowDTO;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class WriteAndReadOperationController_ extends ApplicationTest {
    OperationController controller;
    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Sentinel1GRDWorkflowController.class.getResource("/fxml/WriteAndReadOperationController.fxml"));
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
        GRDDefaultWorkflowDTO workflow = new GRDDefaultWorkflowDTO();
        controller.setParameters(workflow.getOperation(Operator.WRITE_AND_READ).getParameters());
        assertThat(controller.getParameters().get("formatName")).isEqualTo("BEAM-DIMAP");
    }
}
