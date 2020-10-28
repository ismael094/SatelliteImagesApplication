package gui.components;

import controller.MainController;
import model.events.EventType;
import model.listeners.ComponentChangeListener;
import model.events.ToolbarComponentEvent;
import javafx.scene.Parent;
import utils.gui.Observer;

public interface Component {
    void init();
    Parent getView();
    MainController getMainController();
    void addComponentListener(EventType.ComponentEventType type, ComponentChangeListener listener);
    void fireEvent(ToolbarComponentEvent event);
    void addObserver(Observer observer);
}
