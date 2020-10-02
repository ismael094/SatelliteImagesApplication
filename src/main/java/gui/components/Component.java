package gui.components;

import controller.SatelliteApplicationController;
import javafx.scene.Parent;

public interface Component {
    void init();
    Parent getView();
    SatelliteApplicationController getMainController();
}
