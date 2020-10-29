package gui.components;

import controller.MainController;
import gui.components.listener.ComponentEvent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import model.events.EventType;
import model.listeners.ComponentChangeListener;
import javafx.scene.Parent;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import utils.gui.Observer;

import static java.time.LocalDate.now;

public class ConsoleComponent extends ListView<String> implements Component {

    public static final String LINE = "\n";
    private final MainController mainController;
    private final List<ComponentChangeListener> listeners;
    private List<Observer> observers;

    public ConsoleComponent(MainController mainController) {
        super();
        this.mainController = mainController;
        this.listeners = new ArrayList<>();
        observers = new ArrayList<>();
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
    public void addComponentListener(ComponentChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void fireEvent(ComponentEvent event) {
        listeners.forEach(l->l.onComponentChange(event));
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void println(String message) {
        this.getItems().add(getTime() + " > " + message);
    }

    private String getTime(){
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        return format.format(new Date());
    }
}
