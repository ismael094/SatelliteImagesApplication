package gui.components;

import controller.MainController;
import controller.interfaces.TabItem;
import controller.search.SearchController;
import gui.components.Component;
import gui.components.listener.ComponentEvent;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import model.listeners.ComponentChangeListener;
import utils.gui.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class TabPaneComponent extends TabPane implements Component {
    protected final MainController mainController;
    protected final List<ComponentChangeListener> listeners;
    protected List<Observer> observers;

    public TabPaneComponent(MainController mainController) {
        this.mainController = mainController;
        listeners = new ArrayList<>();
        observers = new ArrayList<>();
    }

    @Override
    public Parent getView() {
        return this;
    }

    @Override
    public void addComponentListener(ComponentChangeListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void fireEvent(ComponentEvent event) {
        listeners.forEach(l->l.onComponentChange(event));
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public MainController getMainController() {
        return mainController;
    }

    @Override
    public void updateObservers(Object args) {
        observers.forEach(o->o.update(args));
    }

    public abstract void add(Tab t);

    public abstract void create(String name, String id, Parent node);

    public abstract Tab get(String id);

    public abstract void select(String id);

    public abstract boolean isLoaded(String id);

    public abstract Tab getActive();

    public abstract void select(Tab tab);

    public abstract TabItem getControllerOf(Tab tab);

    public abstract TabItem getControllerOf(String id);

    public abstract void load(TabItem item);
}
