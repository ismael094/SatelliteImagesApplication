package gui.menu;

import controller.MainController;
import controller.search.CopernicusOpenSearchController;
import gui.components.MenuComponent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class SearcherMenu extends Menu implements SatInfMenuItem{
    private final MenuComponent menuComponent;

    public SearcherMenu(MenuComponent menuComponent) {
        super("Searchers");
        this.menuComponent = menuComponent;
        init();
    }

    private void init() {
        MenuItem searcher = new MenuItem("Copernicus Open Search");
        searcher.setOnAction(e -> menuComponent.getMainController().getTabComponent().load(new CopernicusOpenSearchController("id")));
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
