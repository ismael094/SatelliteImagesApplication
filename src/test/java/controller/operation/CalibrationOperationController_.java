package controller.operation;

import controller.processing.workflow.operation.CalibrationOperationController;
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

import java.util.HashMap;

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
        clickOn("#outputBeta");
        clickOn("#polarisations");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        clickOn("#calibrationSourceBands");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);

        assertThat(controller.getOutputBands().size()).isGreaterThan(0);
        assertThat(controller.getOutputBands().get(0)).isEqualTo("Beta0_VV");
    }

    @Test
    public void test_output() {
        Sentinel1GRDDefaultWorkflowDTO workflow = new Sentinel1GRDDefaultWorkflowDTO();

        interact(() -> {
            controller.setOperation(workflow.getOperation(Operator.CALIBRATION));
        });
        clickOn("#outputBeta");
        clickOn("#outputSigma");
        clickOn("#outputGamma");


        //clickOn("#calibrationSourceBands");

        assertThat(controller.getOutputBands().size()).isEqualTo(4);
        assertThat(controller.getOutputBands().toString()).contains("Gamma0_VV").contains("Gamma0_VH");
        assertThat(controller.getOutputBands().toString()).contains("Sigma0_VV").contains("Sigma0_VH");
        assertThat(controller.getOutputBands().toString()).doesNotContain("Beta0");
    }

    @Test
    public void source_bands() {
        clickOn("#polarisations");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);

        assertThat(controller.getOutputBands().size()).isEqualTo(1);
        assertThat(controller.getOutputBands().toString()).doesNotContain("VH");
    }

    @Test
    public void polarisation() {
        interact(() -> {
            controller.getOutputGamma().set(true);
        });
        clickOn("#polarisations");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        clickOn("#calibrationSourceBands");
        type(KeyCode.UP);
        type(KeyCode.ENTER);
        assertThat(controller.getPolarisations().size()).isEqualTo(1);
        assertThat(controller.getPolarisations().contains("VV")).isTrue();
        assertThat(controller.getOutputBands().size()).isGreaterThan(0);
        assertThat(controller.getOutputBands().toString()).doesNotContain("VH");
    }

    @Test
    public void output_parameters() {
        clickOn("#calibrationSourceBands");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        controller.getOutputGamma().set(true);
        controller.getOutputBeta().set(true);
        clickOn("#outputInDb");
        clickOn("#polarisations");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        Operation op = controller.getOperation();

        assertThat(op.getName()).isEqualTo(Operator.CALIBRATION);
        assertThat(op.getParameters().size()).isGreaterThan(0);
        assertThat(op.getParameters().get("outputBetaBand")).isEqualTo(true);
        assertThat(op.getParameters().get("outputSigmaBand")).isEqualTo(false);
        assertThat(op.getParameters().get("outputImageScaleInDb")).isEqualTo(true);
        assertThat(op.getParameters().get("sourceBands")).isEqualTo("Intensity_VH");
        assertThat(op.getParameters().get("selectedPolarisations")).isEqualTo("VV");
    }

    @Test
    public void with_output_empty_should_return_sigma_output() {
        clickOn("#calibrationSourceBands");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        clickOn("#outputInDb");
        clickOn("#polarisations");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        Operation op = controller.getOperation();

        assertThat(op.getName()).isEqualTo(Operator.CALIBRATION);
        System.out.println(controller.getOutputBands().toString());
        assertThat(controller.getOutputBands().size()).isEqualTo(1);
        assertThat(controller.getOutputBands().toString()).contains("Sigma").doesNotContain("Gamma").doesNotContain("Beta");
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

    @Test
    public void empty_parameters() {
        interact(() -> {
            controller.setOperation(new Operation(Operator.CALIBRATION, new HashMap<>()));
        });
        assertThat(controller.getOutputSigma().get()).isFalse();
        assertThat(controller.getOutputBeta().get()).isFalse();

        Operation op = controller.getOperation();
        assertThat(op.getName()).isEqualTo(Operator.CALIBRATION);

        assertThat(op.getParameters().get("sourceBands")).isEqualTo("Intensity_VH,Intensity_VV");
        assertThat(op.getParameters().get("selectedPolarisations")).isEqualTo("VH,VV");
        assertThat(controller.getOutputBands().toString()).contains("Sigma").doesNotContain("Gamma").doesNotContain("Beta");

    }
}
