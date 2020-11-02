package gui.events;

import controller.MainController;
import controller.identification.ConfirmDeleteAccountController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import jfxtras.styles.jmetro.JMetro;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import static java.lang.Thread.sleep;
import static utils.ThemeConfiguration.getJMetroStyled;

public class AppCloseEvent extends Event {
    static final Logger logger = LogManager.getLogger(AppCloseEvent.class.getName());

    public AppCloseEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        logger.atInfo().log("===Closing Satellite App===");
        if (mainController.getDownloader().isDownloading() || mainController.getProductProcessor().isProcessing())
            confirmExit();
        else {
            exit();
        }
    }

    private void confirmExit() {
        URL location = getClass().getResource("/fxml/ConfirmAppExit.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Dialog<ButtonType> dialog = new Dialog<>();
        try {
            dialog.setDialogPane(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        JMetro jMetro = getJMetroStyled();
        jMetro.setScene(dialog.getDialogPane().getScene());

        dialog.setOnCloseRequest(e->{
            dialog.hide();
        });
        Optional<ButtonType> buttonType = dialog.showAndWait();
        buttonType.filter(ConfirmDeleteAccountController.YES::equals).ifPresent(e-> exit());
    }

    private void exit() {
        Platform.exit();
        mainController.getDownloader().cancel();
        mainController.getProductProcessor().cancel();
        System.exit(0);
    }
}
