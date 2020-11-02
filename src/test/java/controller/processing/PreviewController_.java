package controller.processing;

import controller.processing.preview.PreviewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import model.SentinelData;
import model.preprocessing.workflow.defaultWorkflow.GRDDefaultWorkflowDTO;
import model.preprocessing.workflow.operation.Operator;
import org.junit.After;
import org.junit.Test;
import org.locationtech.jts.io.ParseException;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testfx.matcher.control.TextMatchers.hasText;

public class PreviewController_ extends ApplicationTest {
    PreviewController controller;
    private Parent mainNode;
    private GRDDefaultWorkflowDTO workflow;

    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(PreviewController.class.getResource("/fxml/PreviewView.fxml"));
        workflow = new GRDDefaultWorkflowDTO();
        controller = new PreviewController(SentinelData.getSentinel1Product(), SentinelData.FOOTPRINT,workflow,"test");
        fxmlLoader.setController(controller);
        mainNode = fxmlLoader.load();
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
    public void get_source_bands() throws ParseException {
        interact(()-> {
            try {
                controller.initData();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        RadioButton n = lookup("#Sigma0_VV").query();
        assertThat(n).isNotNull();
        clickOn("#Sigma0_VV");
        assertThat(workflow.getOperation(Operator.SUBSET).getParameters().get("sourceBands")).isNotNull();
        assertThat(workflow.getOperation(Operator.SUBSET).getParameters().get("sourceBands")).isEqualTo("Sigma0_VV");
        clickOn("#Sigma0_VH");
        assertThat(workflow.getOperation(Operator.SUBSET).getParameters().get("sourceBands")).isEqualTo("Sigma0_VH");

        ;
    }
}
