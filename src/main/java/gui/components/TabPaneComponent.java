package gui.components;

import controller.MainController;
import controller.interfaces.TabItem;
import controller.search.SearchController;
import gui.components.listener.ComponentEvent;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import jfxtras.styles.jmetro.JMetroStyleClass;
import model.listeners.ComponentChangeListener;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.gui.Observer;

import java.util.*;

import static utils.AlertFactory.showErrorDialog;

public class TabPaneComponent extends TabPane implements Component {
    protected final MainController mainController;
    protected final List<ComponentChangeListener> listeners;
    protected List<Observer> observers;
    private final Map<String,TabItem> loadedControllers;
    private final BooleanProperty isSearchControllerOpen;
    static final Logger logger = LogManager.getLogger(TabPaneComponent.class.getName());


    public TabPaneComponent(MainController mainController) {
        this.mainController = mainController;
        listeners = new ArrayList<>();
        observers = new ArrayList<>();
        getStyleClass().add(JMetroStyleClass.UNDERLINE_TAB_PANE);
        setId("TabComponent");

        this.loadedControllers = new HashMap<>();
        isSearchControllerOpen = new SimpleBooleanProperty(false);

        onTabChangeUpdateObservers();
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

    private void onTabChangeUpdateObservers() {
        getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateObservers(getControllerOf(newValue));
        });
    }

    @Override
    public void init() {
        getStyleClass().add("myTab");
        setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        setPrefWidth(Double.MAX_VALUE);
        //setTabDragPolicy(TabDragPolicy.REORDER);
    }

    public void add(Tab t) {
        t.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        getTabs().add(t);
        select(t);
        updateObservers(getControllerOf(t));
    }

    public void create(String name, String id, Parent node) {
        node.prefWidth(this.getPrefWidth());
        Tab tab = new Tab(name, node);
        tab.setId(id);
        add(tab);

    }

    public Tab get(String id) {
        return getTabs().stream()
                .filter(Objects::nonNull)
                .filter(t->t.getId().equals(id))
                .findAny()
                .orElse(null);
    }

    public void select(String id) {
        select(getTabs().stream()
                .filter(Objects::nonNull)
                .filter(t->t.getId().equals(id))
                .findAny()
                .orElse(null));
    }

    public boolean isLoaded(String id) {
        return getTabs().stream()
                .filter(Objects::nonNull)
                .filter(t->t.getId().equals(id))
                .findAny()
                .orElse(null) != null;
    }

    public Tab getActive() {
        return getSelectionModel().getSelectedItem();
    }

    public void select(Tab tab) {
        getSelectionModel().select(tab);
        updateObservers(getControllerOf(tab));
    }

    public TabItem getControllerOf(Tab tab) {
        return loadedControllers.getOrDefault(tab.getId(),null);
    }

    public TabItem getControllerOf(String id) {
        return loadedControllers.getOrDefault(id,null);
    }

    public void load(TabItem item) {
        //If TabItem was already loaded, print in screen
        if (loadedControllers.getOrDefault(item.getItemId(),null)!=null){
            Tab tab = get(item.getItemId());
            if (tab == null)
                create(item.getName(),item.getItemId(),loadedControllers.get(item.getItemId()).getView());
            else
                select(get(item.getItemId()));
            return;
        }

        //Create task to init TabItem
        Task<Parent> task = item.start();
        mainController.showWaitSpinner();

        task.exceptionProperty().addListener(exceptionWhileOpeningController(item.getName()));

        task.setOnSucceeded(e->{
            fireEvent(new ComponentEvent(this,"Opening new tab "+item.getName()));
            create(item.getName(),item.getItemId(), item.getView());
            loadedControllers.put(item.getItemId(),item);
            item.setTabPaneComponent(this);
            if (item instanceof SearchController)
                isSearchControllerOpen.setValue(true);
            mainController.hideWaitSpinner();
            updateObservers(item);
        });
        new Thread(task).start();
    }

    private ChangeListener<Throwable> exceptionWhileOpeningController(String id) {
        return (observable, oldValue, newValue) -> {
            logger.atLevel(Level.WARN).log("Exception while opening tab -> {}: {}",
                    id, newValue.getMessage());
            showErrorDialog(id, "An error occurred while opening "+
                    id,newValue.getLocalizedMessage());
            mainController.hideWaitSpinner();
            newValue.printStackTrace();
        };
    }

    public BooleanProperty getIsSearchControllerOpenProperty() {
        return isSearchControllerOpen;
    }
}
