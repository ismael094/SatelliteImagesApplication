package gui.components;

import controller.SatelliteApplicationController;
import model.events.EventType;
import model.listeners.ComponentChangeListener;
import model.events.ToolbarComponentEvent;
import javafx.scene.Parent;

public class ConsoleComponent implements Component {

    @Override
    public void init() {

    }

    @Override
    public Parent getView() {
        return null;
    }

    @Override
    public SatelliteApplicationController getMainController() {
        return null;
    }

    @Override
    public void addComponentListener(EventType.ComponentEventType type, ComponentChangeListener listener) {

    }

    @Override
    public void fireEvent(ToolbarComponentEvent event) {

    }
}
