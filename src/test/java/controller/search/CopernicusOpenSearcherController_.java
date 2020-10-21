package controller.search;

import controller.GTMapSearchController;
import controller.workflow.Sentinel1GRDWorkflowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

public class CopernicusOpenSearcherController_ extends ApplicationTest {
    CopernicusOpenSearchController controller;

    private static final String WKT = "POLYGON((-18.10511746017351 28.92606310885101,-17.61073269454851 28.92606310885101,-17.61073269454851 28.337851903184273,-18.10511746017351 28.337851903184273,-18.10511746017351 28.92606310885101))";

    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Sentinel1GRDWorkflowController.class.getResource("/fxml/CopernicusOpenSearchView.fxml"));
        controller = new CopernicusOpenSearchController("id2");
        fxmlLoader.setController(controller);
        Parent mainNode = fxmlLoader.load();
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
    public void test() {
        clickOn("#productTypeList");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
    }
}
