package gui.menu;

import controller.MainController;
import gui.events.ProcessListEvent;
import gui.events.ShowPreviewViewEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import utils.gui.WorkflowUtil;

public class ProcessingMenu extends Menu implements SatInfMenuItem{
    private final MainController mainController;

    public ProcessingMenu(MainController mainController) {
        super("Processing");
        this.mainController = mainController;
        init();
    }

    private void init() {
        MenuItem listProcessing = new MenuItem("Process current list");
        MenuItem myWorkflows = new MenuItem("My Workflows");
        MenuItem preview = new MenuItem("Preview");
        preview.setOnAction(new ShowPreviewViewEvent(mainController));
        Menu sentinel = new Menu("Sentinel");
        Menu sentinel1 = new Menu("Sentinel 1");
        MenuItem grd = new MenuItem("GRD Workflow");
        MenuItem slc = new MenuItem("SLC Workflow");

        sentinel1.getItems().addAll(grd,slc);

        Menu sentinel2 = new Menu("Sentinel 2");
        MenuItem c1 = new MenuItem("1C Workflow");
        MenuItem a2 = new MenuItem("2A Workflow");
        sentinel2.getItems().addAll(c1,a2);

        sentinel.getItems().addAll(sentinel1,sentinel2);

        myWorkflows.setOnAction(e-> WorkflowUtil.loadMyWorkflowView(mainController,mainController.getUser().getWorkflows()));
        listProcessing.setOnAction(new ProcessListEvent(mainController));
        getItems().addAll(myWorkflows,listProcessing,preview,sentinel);
    }

    @Override
    public Menu getMenu() {
        return this;
    }

    @Override
    public String getName() {
        return this.getText();
    }
}
