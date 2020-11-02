package controller.processing.operation;

import controller.processing.workflow.operation.CalibrationOperationController;
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

import static org.assertj.core.api.Assertions.assertThat;

public class CalibrationOperationController_ extends ApplicationTest {

    CalibrationOperationController controller;

    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(CalibrationOperationController.class.getResource("/fxml/CalibrationOperationView.fxml"));
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
        clickOn("#outputBeta");

        assertThat(controller.getParameters().size()).isGreaterThan(0);
    }

    @Test
    public void get_parameters() {
        GRDDefaultWorkflowDTO workflow = new GRDDefaultWorkflowDTO();

        interact(() -> {
            controller.setParameters(workflow.getOperation(Operator.CALIBRATION).getParameters());
        });

        clickOn("#outputBeta");
        clickOn("#outputSigma");
        clickOn("#outputGamma");

        assertThat(controller.getParameters().size()).isEqualTo(5);
        assertThat((boolean)controller.getParameters().get("outputBetaBand")).isTrue();
        assertThat((boolean)controller.getParameters().get("outputGammaBand")).isTrue();
        assertThat((boolean)controller.getParameters().get("outputSigmaBand")).isTrue();
    }
}
