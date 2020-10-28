package gui.menu;

import controller.MainController;
import gui.events.OpenResultsViewEvent;
import gui.events.ProcessListEvent;
import gui.events.ShowPreviewViewEvent;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import model.list.ProductListDTO;
import utils.gui.Observer;
import utils.gui.ProductListDTOUtil;
import utils.gui.WorkflowUtil;

public class ResultsMenu extends Menu implements SatInfMenuItem, Observer {
    private final MainController mainController;
    private MenuItem results;

    public ResultsMenu(MainController mainController) {
        super("Results");
        this.mainController = mainController;
        init();
    }

    private void init() {
        results = new MenuItem("List results");
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

    @Override
    public void update() {
        Platform.runLater(()->{
            ProductListDTO currentList = ProductListDTOUtil.getCurrentList(mainController.getTabController());
            results.setDisable(currentList == null);
        });
    }
}
