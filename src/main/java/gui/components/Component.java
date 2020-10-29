package gui.components;

import controller.MainController;
import gui.components.listener.ComponentEvent;
import model.events.EventType;
import model.listeners.ComponentChangeListener;
import javafx.scene.Parent;
import utils.gui.Observer;

public interface Component {
    void init();
    Parent getView();
    MainController getMainController();
    void addComponentListener(ComponentChangeListener listener);
    void fireEvent(ComponentEvent event);
    void addObserver(Observer observer);
}
