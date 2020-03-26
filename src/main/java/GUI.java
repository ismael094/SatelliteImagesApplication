import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;


public class GUI  extends Application{


    @Override
    public void start(Stage primaryStage) throws Exception {
        URL location = getClass().getResource("Sample.fxml");
        System.out.println(location.toString());
        FXMLLoader fxmlLoader = new FXMLLoader(location);

        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
