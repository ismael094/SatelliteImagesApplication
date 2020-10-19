package gui.components;

import controller.MainController;
import javafx.scene.control.TextArea;
import model.events.EventType;
import model.listeners.ComponentChangeListener;
import model.events.ToolbarComponentEvent;
import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.List;

public class ConsoleComponent extends TextArea implements Component {

    public static final String LINE = "\n";
    private final MainController mainController;
    private final List<ComponentChangeListener> listTreeViewListener;

    public ConsoleComponent(MainController mainController) {
        super();
        this.mainController = mainController;
        this.listTreeViewListener = new ArrayList<>();
    }

    @Override
    public void init() {
        this.setEditable(false);
    }

    @Override
    public Parent getView() {
        return this;
    }

    @Override
    public MainController getMainController() {
        return mainController;
    }

    @Override
    public void addComponentListener(EventType.ComponentEventType type, ComponentChangeListener listener) {

    }

    @Override
    public void fireEvent(ToolbarComponentEvent event) {
        listTreeViewListener.forEach(l->l.onComponentChange(event));
    }

    public void println(String message) {
        this.appendText(message+ LINE);
    }
}
