package gui.components;

import controller.SatelliteApplicationController;
import controller.search.CopernicusOpenSearchController;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class MenuComponent extends MenuBar implements Component{

    private final SatelliteApplicationController mainController;

    public MenuComponent(SatelliteApplicationController mainController) {
        super();
        this.mainController = mainController;
    }

    @Override
    public void init() {
        Menu file = new Menu("File");
        MenuItem close = new MenuItem("Exit");
        close.setOnAction(e -> {
            Platform.exit();
            System.exit(0);
        });
        file.getItems().addAll(close);
        Menu edit = new Menu("Edit");
        Menu search = new Menu("Searchers");
        Menu lists = new Menu("List");
        MenuItem create = new MenuItem("Create new list");
        MenuItem addSel = new MenuItem("Add selected products to list");
        MenuItem addAll = new MenuItem("Add all product to list");
        MenuItem searcher = new MenuItem("Copernicus Open Search");
        lists.getItems().addAll(create,addSel,addAll);
        searcher.setOnAction(e -> mainController.getTabController().load(new CopernicusOpenSearchController("id")));
        search.getItems().addAll(searcher);
        this.getMenus().addAll(file,edit,search,lists);
    }

    @Override
    public Parent getView() {
        return this;
    }

    @Override
    public SatelliteApplicationController getMainController() {
        return mainController;
    }

    public void initSearchView(String id) {
        ;
    }
}
