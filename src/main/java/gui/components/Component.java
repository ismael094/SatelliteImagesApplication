package gui.components;

import controller.SatelliteApplicationController;
import gui.components.listener.ComponentChangeListener;
import gui.components.listener.ComponentEventType;
import gui.components.listener.ToolbarComponentEvent;
import javafx.scene.Parent;

public interface Component {
    void init();
    Parent getView();
    SatelliteApplicationController getMainController();
    void addComponentListener(ComponentEventType type, ComponentChangeListener listener);
    void fireEvent(ToolbarComponentEvent event);
}
