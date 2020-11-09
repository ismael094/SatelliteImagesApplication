package gui.menu;

import controller.interfaces.ProcessingResultsTabItem;
import controller.interfaces.ProductListTabItem;
import gui.components.MenuComponent;
import gui.events.OpenProcessingResultsOfCurrentProductListViewEvent;
import gui.events.ProcessListEvent;
import gui.events.ShowMyWorkflowsOfUserEvent;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import utils.gui.Observer;

public class PreProcessingMenu extends Menu implements SatInfMenuItem {
    private final MenuComponent menuComponent;
    private MenuItem listProcessing;
    private MenuItem results;

    public PreProcessingMenu(MenuComponent menuComponent) {
        super("Pre Processing");
        this.menuComponent = menuComponent;
        init();
    }

    private void init() {
        listProcessing = new MenuItem("Process current list");
        MenuItem myWorkflows = new MenuItem("My Workflows");

        results = new MenuItem("Load results");
        results.setOnAction(new OpenProcessingResultsOfCurrentProductListViewEvent(menuComponent.getMainController()));

        myWorkflows.setOnAction(new ShowMyWorkflowsOfUserEvent(menuComponent.getMainController()));
        listProcessing.setOnAction(new ProcessListEvent(menuComponent.getMainController()));
        getItems().addAll(myWorkflows,listProcessing,results);
    }

    @Override
    public Menu getMenu() {
        return this;
    }

    @Override
    public String getName() {
        return this.getText();
    }

    @Override
    public void update(Object args) {
        Platform.runLater(()->{
            listProcessing.setDisable(!(args instanceof ProductListTabItem));
            results.setDisable(!(args instanceof ProductListTabItem));
        });
    }
}
