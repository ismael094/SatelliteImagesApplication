package gui.menu;

import gui.components.MenuComponent;
import gui.events.ProcessListEvent;
import gui.events.ShowPreviewViewEvent;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import model.list.ProductListDTO;
import utils.gui.Observer;
import utils.gui.ProductListDTOUtil;
import utils.gui.WorkflowUtil;

public class ProcessingMenu extends Menu implements SatInfMenuItem, Observer {
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
        MenuItem preview = new MenuItem("Preview");
        preview.setOnAction(new ShowPreviewViewEvent(menuComponent.getMainController()));
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

        myWorkflows.setOnAction(e-> WorkflowUtil.loadMyWorkflowView(menuComponent.getMainController(), menuComponent.getMainController().getUserManager().getUser().getWorkflows(),false));
        listProcessing.setOnAction(new ProcessListEvent(menuComponent.getMainController()));
        getItems().addAll(myWorkflows,listProcessing,preview,sentinel);
        menuComponent.getMainController().getTabComponent().addObserver(this);
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
    public void update() {
        Platform.runLater(()->{
            ProductListDTO currentList = ProductListDTOUtil.getCurrentList(menuComponent.getMainController().getTabComponent());
            listProcessing.setDisable(currentList == null);
        });

    }
}
