package gui.components;

import controller.TabItem;
import controller.SatelliteApplicationController;
import controller.search.SearchController;
import gui.TabPaneManager;
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
    private final TabPaneManager tabManager;
    private final SatelliteApplicationController mainController;
    private final List<TabItem> loadedControllers;

    static final Logger logger = LogManager.getLogger(TabPaneComponent.class.getName());
    private static TabPaneComponent instance;

    public TabPaneComponent(SatelliteApplicationController mainController) {
        super();
        getStyleClass().add(JMetroStyleClass.UNDERLINE_TAB_PANE);
        setId("TabComponent");
        this.mainController = mainController;
        this.tabManager = TabPaneManager.getTabPaneManager();
        this.loadedControllers = new ArrayList<>();
    }

    @Override
    public void init() {
        tabManager.getStyleClass().add("myTab");
        tabManager.getTabs().add(new Tab("Information"));
        tabManager.getTabs().get(0).setClosable(true);
        tabManager.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        tabManager.setPrefWidth(Double.MAX_VALUE);
    }

    @Override
    public Parent getView() {
        return tabManager;
    }

    public void add(Tab t) {
        t.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        getTabs().add(t);
        getSelectionModel().select(t);
    }

    public void create(String name, Parent node) {
        add(new Tab(name, node));
    }

    public Tab get(Tab tab) {
        return get(tab);
    }

    public Tab get(String tab) {
        return get(tab);
    }

    public SearchController getSearchController() {
        return (SearchController)loadedControllers.stream()
                .filter(c -> c instanceof SearchController)
                .findFirst()
                .orElse(null);
    }

    public void load(TabItem item) {
        mainController.showWaitSpinner();
        Task<Parent> task = item.start();
        task.exceptionProperty().addListener(exceptionWhileOpeningController(item.getName()));
        task.setOnSucceeded(e->{
            create(item.getName(), item.getView());
            loadedControllers.add(item);
            item.setTabPaneComponent(this);
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
        };
    }
}
