package gui.menu;

import controller.MainController;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class ProcessingMenu extends Menu implements SatInfMenuItem{
    private final MainController mainController;

    public ProcessingMenu(MainController mainController) {
        super("Processing");
        this.mainController = mainController;
        init();
    }

    private void init() {
        MenuItem listProcessing = new MenuItem("Process list");
        listProcessing.setOnAction(e->mainController.process());
        getItems().add(listProcessing);
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
