
import controller.MainAppController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;
import java.net.URL;


public class SatelliteApplication extends Application {

    static final Logger logger = LogManager.getLogger(MainAppController.class.getName());

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
            logger.atInfo().log("===Closing Satellite App===");
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
