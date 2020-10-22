package controller.operation;

import controller.workflow.operation.CalibrationOperationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import model.processing.Operation;
import model.processing.Operator;
import model.processing.Sentinel1GRDDefaultWorkflowDTO;
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
    public void test_beta_output() {
        interact(() -> {
            controller.getOutputBeta().set(true);
            clickOn("#calibrationSourceBands");
        });

        assertThat(controller.getOutputBands().size()).isGreaterThan(0);
        assertThat(controller.getOutputBands().get(0)).isEqualTo("Beta0_VV");
    }

    @Test
    public void test_output() {
        interact(() -> {
            controller.getOutputGamma().set(true);
            controller.getOutputBeta().set(true);
            clickOn("#calibrationSourceBands");
        });

        assertThat(controller.getOutputBands().size()).isEqualTo(2);
        assertThat(controller.getOutputBands().contains("Gamma0_VV")).isTrue();
        assertThat(controller.getOutputBands().contains("Beta0_VV")).isTrue();
        assertThat(controller.getOutputBands().contains("Sigma0_VV")).isFalse();
    }

    @Test
    public void source_bands() {
        interact(() -> {
            clickOn("#calibrationSourceBands");
        });

        assertThat(controller.getCalibrationSourceBands().size()).isEqualTo(1);
        assertThat(controller.getCalibrationSourceBands().contains("Intensity_VV")).isTrue();
    }

    @Test
    public void polarisation() {
        clickOn("#polarisations");

        assertThat(controller.getPolarisations().size()).isEqualTo(1);
        assertThat(controller.getPolarisations().contains("VH")).isTrue();
    }

    @Test
    public void output_parameters() {
        clickOn("#calibrationSourceBands");
        controller.getOutputGamma().set(true);
        controller.getOutputBeta().set(true);
        clickOn("#outputInDb");
        clickOn("#polarisations");
        Operation op = controller.getOperation();

        assertThat(op.getName()).isEqualTo(Operator.CALIBRATION);
        assertThat(op.getParameters().size()).isGreaterThan(0);
        assertThat(op.getParameters().get("outputBetaBand")).isEqualTo(true);
        assertThat(op.getParameters().get("outputSigmaBand")).isEqualTo(false);
        assertThat(op.getParameters().get("outputImageScaleInDb")).isEqualTo(true);
        assertThat(op.getParameters().get("sourceBands")).isEqualTo("Intensity_VV");
        assertThat(op.getParameters().get("selectedPolarisations")).isEqualTo("VH");
    }

    @Test
    public void set_operation() {
        interact(() -> {
            controller.setOperation(new Sentinel1GRDDefaultWorkflowDTO().getOperation(Operator.CALIBRATION));
        });

        assertThat(controller.getOutputSigma().get()).isFalse();
        assertThat(controller.getOutputBeta().get()).isTrue();

        Operation op = controller.getOperation();

        assertThat(op.getName()).isEqualTo(Operator.CALIBRATION);
        assertThat(op.getParameters().size()).isGreaterThan(0);
        assertThat(op.getParameters().get("outputBetaBand")).isEqualTo(true);
        assertThat(op.getParameters().get("outputSigmaBand")).isEqualTo(false);
        assertThat(op.getParameters().get("sourceBands")).isEqualTo("Intensity_VV,Intensity_VH");
        assertThat(op.getParameters().get("selectedPolarisations")).isEqualTo("VV,VH");
    }
}
