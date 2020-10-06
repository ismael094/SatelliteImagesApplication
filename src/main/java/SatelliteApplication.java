
import controller.identification.LoginController;
import controller.SatelliteApplicationController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.user.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.database.MongoDBConfiguration;
import utils.database.MongoDBManager;

import java.io.IOException;
import java.net.URL;


public class SatelliteApplication extends Application {



    static final Logger logger = LogManager.getLogger(SatelliteApplicationController.class.getName());
    JMetro jMetro = new JMetro(Style.LIGHT);

    @Override
    public void start(Stage primaryStage) throws Exception {

        initDatabase();

        UserDTO userDTO = loginWindows();
        System.out.println(userDTO.getProductListsDTO().size());
        if (userDTO.getEmail().isEmpty()) {
            Platform.exit();
            System.exit(0);
        }
        
        URL location = getClass().getResource("/fxml/MainApp.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Scene scene = new Scene(fxmlLoader.load());

        SatelliteApplicationController controller = fxmlLoader.getController();
        controller.setUser(userDTO);
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

    }

    private void initDatabase() {
        MongoDBManager mongoDBManager = MongoDBManager.getMongoDBManager();
        if (!mongoDBManager.isConnected()) {
            mongoDBManager.setCredentialsAndDatabase(MongoDBConfiguration.USER,MongoDBConfiguration.PASSWORD,MongoDBConfiguration.DATABASE);
            mongoDBManager.connect();
        }

    }

    private UserDTO loginWindows() {
        URL location = getClass().getResource("/fxml/LoginView.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        jMetro.setScene(scene);
        LoginController controller = fxmlLoader.getController();
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setScene(scene);
        stage.showAndWait();
        return controller.getUser();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
