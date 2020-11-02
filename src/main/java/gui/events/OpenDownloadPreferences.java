package gui.events;

import controller.MainController;
import controller.download.DownloadPreferencesController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import jfxtras.styles.jmetro.JMetro;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import static utils.ThemeConfiguration.getJMetroStyled;

public class OpenDownloadPreferences extends Event{

    public OpenDownloadPreferences(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        openDownloadPreferences();
    }

    private void openDownloadPreferences() {
        URL location = getClass().getResource("/fxml/DownloadPreferences.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Dialog<ButtonType> dialog = new Dialog<>();
        try {
            dialog.setDialogPane(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        JMetro jMetro = getJMetroStyled();
        jMetro.setScene(dialog.getDialogPane().getScene());
        DownloadPreferencesController controller = fxmlLoader.getController();

        dialog.setOnCloseRequest(e->{
            dialog.hide();
        });
        Optional<ButtonType> buttonType = dialog.showAndWait();
        buttonType.filter(DownloadPreferencesController.APPLY::equals).ifPresent(e-> controller.applyChanges());
    }
}
