package controller;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Test;
import org.locationtech.jts.io.ParseException;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class GTMapControllerTest extends ApplicationTest {
    GTMapController controller;

    private static final String WKT = "POLYGON((-18.10511746017351 28.92606310885101,-17.61073269454851 28.92606310885101,-17.61073269454851 28.337851903184273,-18.10511746017351 28.337851903184273,-18.10511746017351 28.92606310885101))";

    @Override
    public void start (Stage stage) throws Exception {
        //FXMLLoader fxmlLoader = new FXMLLoader(Sentinel1GRDWorkflowController.class.getResource("/fxml/GeotoolsMapView.fxml"));
        controller = new GTMapController(1200,570,true);
        Parent mainNode = controller.getView();
        stage.setScene(new Scene(mainNode));
        stage.show();
        //stage.toFront();
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
    public void delete_search_area() throws ParseException {
        controller.setSearchArea(WKT);
        WaitForAsyncUtils.waitForFxEvents();

        Button n = lookup("#delete").query();
        assertThat(n.isVisible()).isTrue();
        n.setVisible(true);
        n.getOnAction().handle(null);
        assertThat(controller.getWKT()).isEqualTo(null);
    }
}
