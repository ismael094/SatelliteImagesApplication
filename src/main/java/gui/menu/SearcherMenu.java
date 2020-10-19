package gui.menu;

import controller.MainController;
import controller.search.CopernicusOpenSearchController;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class SearcherMenu extends Menu implements SatInfMenuItem{
    private final MainController mainController;

    public SearcherMenu(MainController mainController) {
        super("Searchers");
        this.mainController = mainController;
        init();
    }

    private void init() {
        MenuItem searcher = new MenuItem("Copernicus Open Search");
        searcher.setOnAction(e -> mainController.getTabController().load(new CopernicusOpenSearchController("id")));
        getItems().addAll(searcher);
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
