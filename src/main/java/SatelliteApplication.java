import controller.ListViewController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import gui.MapCanvas;
import gui.MapGUI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.locationtech.jts.geom.Coordinate;

import java.net.URL;


public class SatelliteApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL location = getClass().getResource("/ListView.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);

        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setScene(scene);
        primaryStage.show();


        /*Scene scene = new Scene(new MapGUI(1024,724));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Map Example");
        primaryStage.show();*/

    }



    public static void main(String[] args) {
        launch(args);
    }
}
