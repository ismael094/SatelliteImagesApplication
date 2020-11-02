package controller;

import controller.processing.workflow.Sentinel1GRDWorkflowController;
import controller.processing.workflow.Sentinel1SLCWorkflowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import model.preprocessing.workflow.WorkflowType;
import model.preprocessing.workflow.defaultWorkflow.SLCDefaultWorkflowDTO;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.assertj.core.api.Assertions.assertThat;

public class Sentinel1SLCWorkflowController_ extends ApplicationTest {
    Sentinel1SLCWorkflowController controller;
    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Sentinel1GRDWorkflowController.class.getResource("/fxml/Sentinel1SLCWorkflowView.fxml"));
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
        interact(() -> controller.setWorkflow(new SLCDefaultWorkflowDTO()));

        assertThat(controller.getWorkflow().getType()).isEqualTo(WorkflowType.SLC);
    }
}
