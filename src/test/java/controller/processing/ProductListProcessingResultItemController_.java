package controller.processing;

import controller.postprocessing.ProductListProcessingResultItemController;
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

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductListProcessingResultItemController_ extends ApplicationTest {
    ProductListProcessingResultItemController controller;
    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ProductListProcessingResultItemController.class.getResource("/fxml/ProductListProcessingResultItemView.fxml"));
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
        interact(() -> {
            controller.setFile(new File("C:\\Users\\Ismael2\\Documents\\SatInf\\Lists\\Cádiz - SAR\\S1B_IW_GRDH_1SDV_20201013T182556_20201013T182621_023798_02D392_C25D_0.tif"));
        });
    }
}
