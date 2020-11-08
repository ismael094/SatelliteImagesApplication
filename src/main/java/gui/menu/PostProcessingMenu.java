package gui.menu;

import controller.interfaces.ProcessingResultsTabItem;
import controller.interfaces.ProductListTabItem;
import controller.interfaces.TabItem;
import gui.components.MenuComponent;
import gui.components.TabPaneComponent;
import gui.events.ExecuteAlgorithmEvent;
import gui.events.OpenProcessingResultsOfCurrentProductListViewEvent;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import model.list.ProductListDTO;
import model.postprocessing.algorithms.MedianFilterAlgorithm;
import utils.AlgorithmsLoader;
import utils.gui.Observer;
import utils.gui.ProductListDTOUtil;

import java.io.File;
import java.util.List;

public class PostProcessingMenu extends Menu implements SatInfMenuItem {
    private final MenuComponent menuComponent;
    private MenuItem results;
    private Menu algorithm;

    public PostProcessingMenu(MenuComponent menuComponent) {
        super("PostProcessing");
        this.menuComponent = menuComponent;
        init();
    }

    private void init() {
        results = new MenuItem("Load processing results");

        algorithm = new Menu("Algorithms");

        List<File> files = AlgorithmsLoader.loadAlgorithms();
        MenuItem menuItem = new MenuItem("Median Filter");
        menuItem.setOnAction(new ExecuteAlgorithmEvent(menuComponent.getMainController(), new MedianFilterAlgorithm()));
        algorithm.getItems().add(menuItem);
        /*files.forEach(a->{
            MenuItem menuItem = new MenuItem(a.getName().split("\\.")[0]);
            menuItem.setOnAction(new ExecuteAlgorithmEvent(menuComponent.getMainController(),a));
            algorithm.getItems().add(menuItem);
        });*/

        results.setOnAction(new OpenProcessingResultsOfCurrentProductListViewEvent(menuComponent.getMainController()));

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
    public void update(Object args) {
        Platform.runLater(()->{
            results.setDisable(!(args instanceof ProductListTabItem));
            algorithm.setDisable(!(args instanceof ProcessingResultsTabItem));
        });
    }
}
