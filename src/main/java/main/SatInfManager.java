package main;

import controller.identification.LoginController;
import controller.MainController;
import gui.events.AppCloseEvent;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import model.user.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.FileUtils;
import utils.database.MongoDBConfiguration;
import utils.database.MongoDBManager;

import java.io.IOException;
import java.net.URL;
import java.util.prefs.Preferences;

import static utils.ThemeConfiguration.getJMetroStyled;


public class SatInfManager extends Application {
    static final Logger logger = LogManager.getLogger(SatInfManager.class.getName());

    JMetro jMetro;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Preferences myConnectionPrefs = Preferences.userRoot().node("downloadPreferences");
        if (myConnectionPrefs.get("productFolder", null) == null) {
            myConnectionPrefs.put("productFolder", FileUtils.DEFAULT_DOWNLOAD_FOLDER);
            myConnectionPrefs.put("listFolder", FileUtils.DEFAULT_LIST_FOLDER);
            myConnectionPrefs.put("mode", "multiple");
        }

        jMetro = getJMetroStyled();

        initDatabase();

        UserDTO userDTO = getLoginUserDialog();

        if (userDTO == null || userDTO.getId() == null) {
            logger.atInfo().log("===Closing Satellite App===");
            Platform.exit();
            System.exit(0);
        }
        
        URL location = getClass().getResource("/fxml/Main_new.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Scene scene = new Scene(fxmlLoader.load());

        MainController controller = fxmlLoader.getController();

        controller.setUser(userDTO);
        controller.initComponents();
        primaryStage.setMaximized(true);

        jMetro.setScene(scene);
        primaryStage.getIcons().add(new Image(getClass().getResource("/img/logo.jpg").openStream()));
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(t -> {
           new AppCloseEvent(controller).handle(null);
        });

    }

    private void initDatabase() {
        MongoDBManager mongoDBManager = MongoDBManager.getMongoDBManager();
        if (!mongoDBManager.isConnected()) {
            mongoDBManager.setCredentialsAndDatabase(MongoDBConfiguration.USER,MongoDBConfiguration.PASSWORD,MongoDBConfiguration.DATABASE);
            mongoDBManager.connect();
        }

    }

    private UserDTO getLoginUserDialog() {
        URL location = getClass().getResource("/fxml/LoginView.fxml");
        System.out.println(location.toString());
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        JMetro jMetro = getJMetroStyled();

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
