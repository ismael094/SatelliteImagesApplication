package gui.components;

import controller.SatelliteApplicationController;
import gui.components.listener.ComponentChangeListener;
import gui.components.listener.ComponentEventType;
import gui.components.listener.ToolbarComponentEvent;
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
    public void addComponentListener(ComponentEventType type, ComponentChangeListener listener) {

    }

    @Override
    public void fireEvent(ToolbarComponentEvent event) {

    }
}
