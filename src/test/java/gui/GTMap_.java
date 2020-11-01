package gui;

import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.assertj.core.util.Lists;
import org.junit.After;
import org.junit.Test;
import org.locationtech.jts.io.ParseException;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import java.awt.*;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class GTMap_ extends ApplicationTest {
    private GTMap map;
    private static final String WKT = "POLYGON((-18.10511746017351 28.92606310885101,-17.61073269454851 28.92606310885101,-17.61073269454851 28.337851903184273,-18.10511746017351 28.337851903184273,-18.10511746017351 28.92606310885101))";

    @Override
    public void start (Stage stage) throws Exception {
        map = new GTMap(500,500,false);
        stage.setScene(new Scene(new AnchorPane(map)));
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
    public void create_layer() throws ParseException, IOException {
        map.createFeatureFromWKT(WKT,"id1","products");
        map.createAndDrawLayer("products", Color.BLACK, Color.CYAN);
        map.focusOnLayer("products");
        assertThat(map.getSelectedFeatureID()).isNotEqualTo("id1");
    }


    @Test
    public void select_feature_in_map() throws ParseException, IOException {
        map.createFeatureFromWKT(WKT,"id1","products");
        map.createAndDrawLayer("products", Color.BLACK, Color.CYAN);
        map.focusOnLayer("products");

        map.selectFeature(new Point2D(100,100), false,"products",Color.BLACK,Color.BLACK,Color.BLACK,Color.BLACK);

        assertThat(map.getSelectedFeatureID()).isEqualTo("id1");
    }


}
