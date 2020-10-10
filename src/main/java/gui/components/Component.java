package gui.components;

import controller.SatelliteApplicationController;
import model.events.EventType;
import model.listeners.ComponentChangeListener;
import model.events.ToolbarComponentEvent;
import javafx.scene.Parent;

public interface Component {
    void init();
    Parent getView();
    SatelliteApplicationController getMainController();
    void addComponentListener(EventType.ComponentEventType type, ComponentChangeListener listener);
    void fireEvent(ToolbarComponentEvent event);
}
