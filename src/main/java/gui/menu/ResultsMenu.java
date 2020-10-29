package gui.menu;

import controller.MainController;
import controller.interfaces.ProcessingResultsTabItem;
import controller.interfaces.TabItem;
import gui.components.MenuComponent;
import gui.components.SatInfTabPaneComponent;
import gui.components.tabcomponent.TabPaneComponent;
import gui.events.ExecuteAlgorithmEvent;
import gui.events.OpenProcessingResultsOfCurrentProductListViewEvent;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import model.list.ProductListDTO;
import utils.AlgorithmsLoader;
import utils.gui.Observer;
import utils.gui.ProductListDTOUtil;

import java.io.File;
import java.util.List;

public class ResultsMenu extends Menu implements SatInfMenuItem, Observer {
    private final MenuComponent menuComponent;
    private MenuItem results;
    private Menu algorithm;

    public ResultsMenu(MenuComponent menuComponent) {
        super("Results");
        this.menuComponent = menuComponent;
        init();
    }

    private void init() {
        results = new MenuItem("Processing results");

        algorithm = new Menu("Algorithms");

        List<File> files = AlgorithmsLoader.loadAlgorithms();

        files.forEach(a->{
            MenuItem menuItem = new MenuItem(a.getName().split("\\.")[0]);
            menuItem.setOnAction(new ExecuteAlgorithmEvent(menuComponent.getMainController(),a));
            algorithm.getItems().add(menuItem);
        });

        results.setOnAction(new OpenProcessingResultsOfCurrentProductListViewEvent(menuComponent.getMainController()));

        menuComponent.getMainController().getTabComponent().addObserver(this);
        getItems().addAll(results,algorithm);
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
        TabPaneComponent tabController = menuComponent.getMainController().getTabComponent();
        Platform.runLater(()->{
            ProductListDTO currentList = ProductListDTOUtil.getCurrentList(menuComponent.getMainController().getTabComponent());
            results.setDisable(currentList == null);
            TabItem controllerOf = tabController.getControllerOf(tabController.getActive());
            algorithm.setDisable(!(controllerOf instanceof ProcessingResultsTabItem));
        });
    }
}
