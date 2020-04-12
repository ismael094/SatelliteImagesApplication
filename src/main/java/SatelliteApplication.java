import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.geotools.map.MapContent;

import java.net.URL;


public class SatelliteApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL location = getClass().getResource("/ListView.fxml");
        System.out.println(location.toString());
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setScene(scene);
        primaryStage.show();
        MapContent map = new MapContent();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
