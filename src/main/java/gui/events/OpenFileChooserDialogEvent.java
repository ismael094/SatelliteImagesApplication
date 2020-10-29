package gui.events;

import controller.MainController;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import utils.AlertFactory;
import utils.FileUtils;

import java.io.File;

public class OpenFileChooserDialogEvent extends Event {

    public OpenFileChooserDialogEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Executable files", "*.exe"),
                new FileChooser.ExtensionFilter("Executable files", "*.bat"));
        File selectedFile = fileChooser.showOpenDialog(mainController.getRoot().getScene().getWindow());
        if (selectedFile != null) {
            if (!FileUtils.copyAlgorithm(selectedFile)) {
                AlertFactory.showErrorDialog("Error","Error","Error while loading algorithm");
            } else {
                AlertFactory.showSuccessDialog("Loaded","Loaded","Algorithm successfully loaded");
            }
        }

    }
}
