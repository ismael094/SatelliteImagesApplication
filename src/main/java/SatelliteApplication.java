
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.net.URL;


public class SatelliteApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        URL location = getClass().getResource("/fxml/MainApp.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);

        JMetro jMetro = new JMetro(Style.LIGHT);
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setMaximized(true);

        jMetro.setScene(scene);
        primaryStage.getIcons().add(new Image(getClass().getResource("/img/logo.jpg").openStream()));
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });


        /*Scene scene = new Scene(new MapGUI(1024,724));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Map Example");
        primaryStage.show();*/

    }



    public static void main(String[] args) {
        launch(args);
    }
}
