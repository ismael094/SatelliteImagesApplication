package gui.events;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import controller.MainController;
import controller.identification.UserDataEditController;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;

import java.io.IOException;
import java.net.URL;

import static utils.ThemeConfiguration.getJMetroStyled;

public class AboutApplicationEvent extends Event {
    public AboutApplicationEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        showInformationView("/fxml/AboutSatInfManagerView.fxml");
    }

}
