package gui.components;

import controller.interfaces.TabItem;
import controller.SatelliteApplicationController;
import controller.search.SearchController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static utils.AlertFactory.showErrorDialog;

public class TabPaneComponent extends TabPane implements Component {
    static final Logger logger = LogManager.getLogger(TabPaneComponent.class.getName());
    private final SatelliteApplicationController mainController;
    private final Map<String,TabItem> loadedControllers;
    private final BooleanProperty isSearchControllerOpen;

    public TabPaneComponent(SatelliteApplicationController mainController) {
        super();
        getStyleClass().add(JMetroStyleClass.UNDERLINE_TAB_PANE);
        setId("TabComponent");
        this.mainController = mainController;
        this.loadedControllers = new HashMap<>();
        isSearchControllerOpen = new SimpleBooleanProperty(false);
    }

    @Override
    public void init() {
        getStyleClass().add("myTab");
        getTabs().add(new Tab("Information"));
        getTabs().get(0).setClosable(true);
        setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        setPrefWidth(Double.MAX_VALUE);
    }

    @Override
    public Parent getView() {
        return this;
    }

    @Override
    public SatelliteApplicationController getMainController() {
        return mainController;
    }

    public void add(Tab t) {
        t.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        getTabs().add(t);
        getSelectionModel().select(t);
    }

    public void create(String name, Parent node) {
        node.prefWidth(this.getPrefWidth());
        add(new Tab(name, node));
    }

    public Tab get(String tab) {
        return getTabs().stream()
        .filter(t->t.getText().equals(tab))
        .findAny()
        .orElse(null);
    }

    public Tab getActive() {
        return getSelectionModel().getSelectedItem();
    }

    public void select(Tab tab) {
        getSelectionModel().select(tab);
    }

    public TabItem getControllerOf(Tab tab) {
        return loadedControllers.getOrDefault(tab.getText(),null);
    }

    public void load(TabItem item) {
        if (loadedControllers.getOrDefault(item.getName(),null)!=null){
            System.out.println("something");
            Tab tab = get(item.getName());
            if (tab == null)
                create(item.getName(),loadedControllers.get(item.getName()).getView());
            else
                select(get(item.getName()));
            return;
        }

        Task<Parent> task = item.start();
        mainController.showWaitSpinner();
        task.exceptionProperty().addListener(exceptionWhileOpeningController(item.getName()));
        task.setOnSucceeded(e->{
            create(item.getName(), item.getView());
            loadedControllers.put(item.getName(),item);
            item.setTabPaneComponent(this);
            if (item instanceof SearchController)
                isSearchControllerOpen.setValue(true);
            mainController.hideWaitSpinner();
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