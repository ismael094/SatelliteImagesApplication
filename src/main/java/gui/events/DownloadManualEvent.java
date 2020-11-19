package gui.events;

import controller.MainController;
import javafx.event.ActionEvent;
import javafx.scene.control.Hyperlink;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class DownloadManualEvent extends Event{
    public DownloadManualEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        if(Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI("https://drive.google.com/file/d/1BYSYXYKC7QjYrK75oL5vnEyX-cZ9iXGK/view?usp=sharing"));
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }
        }
    }
}
