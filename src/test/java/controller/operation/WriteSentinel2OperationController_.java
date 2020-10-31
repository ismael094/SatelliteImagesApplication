package controller.operation;

import controller.processing.workflow.operation.WriteSentinel2OperationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import model.processing.workflow.operation.Operation;
import model.processing.workflow.operation.Operator;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class WriteSentinel2OperationController_  extends ApplicationTest {
    WriteSentinel2OperationController controller;
    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(WriteSentinel2OperationController.class.getResource("/fxml/WriteSentinel2OperationView.fxml"));
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
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("formatName","GeoTIFF");
        parameters.put("generatePNG",true);
        parameters.put("profile","Real");
        parameters.put("red","B4");
        parameters.put("green","B3");
        parameters.put("blue","B2");

        interact(()->{
            controller.setParameters(parameters);
        });
        assertThat(controller.getParameters().get("red")).isEqualTo("B4");
        assertThat(controller.getParameters().get("formatName")).isEqualTo("GeoTIFF");
        assertThat(controller.getParameters().get("profile")).isEqualTo("Real");
    }

    @Test
    public void set_write_format() {
        //Sentinel1GRDDefaultWorkflowDTO workflow = new Sentinel1GRDDefaultWorkflowDTO();
        interact(()->{
            controller.setParameters(new HashMap<>());
        });
        assertThat(controller.getParameters().get("formatName")).isEqualTo("GeoTIFF");

        clickOn("#writeFormat");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        assertThat(controller.getParameters().get("formatName")).isEqualTo("PolSARPro");
        assertThat(controller.getGeneratePng().isSelected()).isFalse();
        assertThat(controller.getRed().isDisable()).isTrue();
    }

    @Test
    public void disable_colors_when_toggle_deselected() {
        interact(()->{
            controller.setParameters(new HashMap<>());
            assertThat(controller.getParameters().get("formatName")).isEqualTo("GeoTIFF");
        });
        clickOn("#generatePng");
        assertThat(controller.getGeneratePng().isSelected()).isTrue();
    }

    @Test
    public void change_profiles() {
        //Sentinel1GRDDefaultWorkflowDTO workflow = new Sentinel1GRDDefaultWorkflowDTO();
        interact(()->{
            controller.setParameters(new HashMap<>());
        });

        assertThat(controller.getParameters().get("formatName")).isEqualTo("GeoTIFF");
        assertThat(controller.getRed().getText()).isEqualTo("B4");

        clickOn("#generatePng");
        clickOn("#profiles");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);

        assertThat(controller.getGeneratePng().isSelected()).isTrue();
        assertThat(controller.getProfile().getValue()).isEqualTo("Healthy Vegetation");
        assertThat(controller.getRed().getText()).isEqualTo("B8");
        assertThat(controller.getGreen().getText()).isEqualTo("B2");
        assertThat(controller.getBlue().getText()).isEqualTo("B11");
    }

    @Test
    public void get_operation_parameters() {
        //Sentinel1GRDDefaultWorkflowDTO workflow = new Sentinel1GRDDefaultWorkflowDTO();
        interact(()->{
            controller.setParameters(new HashMap<>());
        });

        assertThat(controller.getRed().getText()).isEqualTo("B4");

        clickOn("#generatePng");
        clickOn("#profiles");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        Map<String, Object> parameters = controller.getParameters();
        assertThat(parameters.get("formatName")).isEqualTo("GeoTIFF");
        assertThat(parameters.get("generatePNG")).isEqualTo(true);
        assertThat(parameters.get("red")).isEqualTo("B8");
        assertThat(parameters.get("green")).isEqualTo("B2");
        assertThat(parameters.get("blue")).isEqualTo("B11");
    }

    @Test
    public void get_operation_parameters_generation_off() {
        //Sentinel1GRDDefaultWorkflowDTO workflow = new Sentinel1GRDDefaultWorkflowDTO();
        interact(()->{
            controller.setParameters(new HashMap<>());
        });

        assertThat(controller.getRed().getText()).isEqualTo("B4");

        Map<String, Object> parameters = controller.getParameters();
        assertThat(parameters.get("formatName")).isEqualTo("GeoTIFF");
        assertThat(parameters.get("generatePNG")).isEqualTo(false);
    }
}

