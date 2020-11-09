package gui.events;

import controller.MainController;
import javafx.event.ActionEvent;

public class AboutApplicationEvent extends Event {
    public AboutApplicationEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        showInformationView("/fxml/AboutSatInfManagerView.fxml");
    }

}
