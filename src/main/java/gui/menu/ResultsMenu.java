package gui.menu;

import controller.MainController;
import gui.events.OpenResultsViewEvent;
import gui.events.ProcessListEvent;
import gui.events.ShowPreviewViewEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import utils.gui.WorkflowUtil;

public class ResultsMenu extends Menu implements SatInfMenuItem{
    private final MainController mainController;

    public ResultsMenu(MainController mainController) {
        super("Results");
        this.mainController = mainController;
        init();
    }

    private void init() {
        MenuItem results = new MenuItem("List results");
        results.setOnAction(new OpenResultsViewEvent(mainController));

        getItems().addAll(results);
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
