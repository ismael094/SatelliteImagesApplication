package controller;

import controller.workflow.Sentinel1GRDWorkflowController;
import controller.workflow.operation.OperationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.locationtech.jts.io.ParseException;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.awt.*;
import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;

public class GTMapSearchController_   extends ApplicationTest {
    GTMapSearchController controller;

    private static final String WKT = "POLYGON((-18.10511746017351 28.92606310885101,-17.61073269454851 28.92606310885101,-17.61073269454851 28.337851903184273,-18.10511746017351 28.337851903184273,-18.10511746017351 28.92606310885101))";

    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Sentinel1GRDWorkflowController.class.getResource("/fxml/GeotoolsMapView.fxml"));
        controller = new GTMapSearchController(570,570,true);
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
    public void print_features() {
        controller.setSearchArea(WKT);
        assertThat(controller.getWKT()).isEqualTo(WKT);
    }

    @Test
    public void test() throws ParseException {
        controller.setSearchArea(WKT);
        controller.addProductWKT(WKT,"id1","products");
        controller.drawFeaturesOfLayer("products", Color.BLACK, Color.CYAN);
        controller.setLayerSelectedAreaEvent("products");
        clickOn("#delete");
        assertThat(controller.getWKT()).isEqualTo(null);
    }
}
