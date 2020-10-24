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
import model.processing.workflow.operation.Operator;
import model.processing.workflow.Sentinel1GRDDefaultWorkflowDTO;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class OrbitOperationController_ extends ApplicationTest {
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
    public void set_operation() {
        Sentinel1GRDDefaultWorkflowDTO workflow = new Sentinel1GRDDefaultWorkflowDTO();
        controller.setOperation(workflow.getOperation(Operator.APPLY_ORBIT_FILE));
        controller.setInputBands(FXCollections.observableArrayList("Intensity_VH","Intensity_VV"));
        assertThat(controller.getOutputBands().size()).isEqualTo(2);
        assertThat(controller.getInputBands()).isEqualTo(controller.getOutputBands());
        assertThat(controller.getOperation().getParameters().get("polyDegree")).isEqualTo(3);
    }

    @Test
    public void set_new_operation() {
        clickOn("#plyDegree");
        doubleClickOn("#plyDegree");
        write("5");
        type(KeyCode.ENTER);
        assertThat(controller.getOperation().getParameters().get("polyDegree")).isEqualTo(5);
        clickOn("#plyDegree");
        doubleClickOn("#plyDegree");
        write("");
        type(KeyCode.ENTER);
        assertThat(controller.getOperation().getParameters().get("polyDegree")).isEqualTo(5);
    }
}
