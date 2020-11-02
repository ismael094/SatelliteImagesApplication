package gui.menu;

import controller.interfaces.ProductListTabItem;
import gui.components.MenuComponent;
import gui.events.ProcessListEvent;
import gui.events.ShowMyWorkflowsOfUserEvent;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import utils.gui.Observer;

public class ProcessingMenu extends Menu implements SatInfMenuItem {
    private final MenuComponent menuComponent;
    private MenuItem listProcessing;

    public ProcessingMenu(MenuComponent menuComponent) {
        super("Processing");
        this.menuComponent = menuComponent;
        init();
    }

    private void init() {
        listProcessing = new MenuItem("Process current list");
        MenuItem myWorkflows = new MenuItem("My Workflows");
        Menu sentinel = new Menu("Sentinel Workflows");
        Menu sentinel1 = new Menu("Sentinel 1");
        MenuItem grd = new MenuItem("GRD Default Workflow");
        MenuItem slc = new MenuItem("SLC Default Workflow");

        sentinel1.getItems().addAll(grd,slc);

        Menu sentinel2 = new Menu("Sentinel 2");
        MenuItem c1 = new MenuItem("S2MSI1C Workflow");
        MenuItem a2 = new MenuItem("S2MSI2A Workflow");
        sentinel2.getItems().addAll(c1,a2);

        sentinel.getItems().addAll(sentinel1,sentinel2);

        myWorkflows.setOnAction(new ShowMyWorkflowsOfUserEvent(menuComponent.getMainController()));
        listProcessing.setOnAction(new ProcessListEvent(menuComponent.getMainController()));
        getItems().addAll(myWorkflows,listProcessing,sentinel);
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
        });
    }
}
