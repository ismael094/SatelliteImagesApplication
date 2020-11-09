package gui.events;

import controller.MainController;
import javafx.event.ActionEvent;

public class ShowInformationHelpEvent extends Event {
    public ShowInformationHelpEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        showInformationView("/fxml/HelpView.fxml");
    }
}
