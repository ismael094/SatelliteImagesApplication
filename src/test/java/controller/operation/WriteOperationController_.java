package controller.operation;

import controller.processing.workflow.Sentinel1GRDWorkflowController;
import controller.processing.workflow.operation.OperationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import model.processing.Operator;
import model.processing.Sentinel1GRDDefaultWorkflowDTO;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class WriteOperationController_  extends ApplicationTest {
    OperationController controller;
    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Sentinel1GRDWorkflowController.class.getResource("/fxml/WriteOperationView.fxml"));
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
        controller.setOperation(workflow.getOperation(Operator.WRITE));
        assertThat(controller.getOperation().getName()).isEqualTo(Operator.WRITE);
    }

    @Test
    public void set_write_format() {
        assertThat(controller.getOperation().getParameters().get("formatName")).isEqualTo("GeoTIFF");
        clickOn("#writeFormat");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        assertThat(controller.getOperation().getParameters().get("formatName")).isEqualTo("PolSARPro");
    }
}
