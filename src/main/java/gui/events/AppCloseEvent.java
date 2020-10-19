package gui.events;

import controller.MainController;
import gui.components.ToolBarComponent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppCloseEvent extends Event {
    static final Logger logger = LogManager.getLogger(AppCloseEvent.class.getName());

    public AppCloseEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        logger.atInfo().log("===Closing Satellite App===");
        Platform.exit();
        mainController.getDownload().cancel();
        System.exit(0);
    }
}
