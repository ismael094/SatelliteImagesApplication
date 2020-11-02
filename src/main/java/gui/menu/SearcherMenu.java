package gui.menu;

import controller.search.CopernicusOpenSearchController;
import gui.components.MenuComponent;
import gui.events.LoadTabItemEvent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import services.CopernicusService;
import services.search.OpenSearcher;
import utils.ServiceFactory;

public class SearcherMenu extends Menu implements SatInfMenuItem {
    private final MenuComponent menuComponent;

    public SearcherMenu(MenuComponent menuComponent) {
        super("Searchers");
        this.menuComponent = menuComponent;
        init();
    }

    private void init() {
        MenuItem searcher = new MenuItem("Copernicus Open Search");
        searcher.setOnAction(new LoadTabItemEvent(menuComponent.getMainController(),new CopernicusOpenSearchController("id", new OpenSearcher())));
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

    @Override
    public void update(Object args) {

    }
}
