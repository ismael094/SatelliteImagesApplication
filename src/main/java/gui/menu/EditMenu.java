package gui.menu;

import controller.MainController;
import controller.interfaces.ModifiableTabItem;
import controller.interfaces.ProductListTabItem;
import controller.interfaces.TabItem;
import controller.search.SearchController;
import gui.components.TabPaneComponent;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import model.list.ProductListDTO;
import utils.gui.Observer;
import utils.gui.ProductListDTOUtil;

public class EditMenu extends Menu implements SatInfMenuItem, Observer {
    private final MainController mainController;
    private MenuItem redo;
    private MenuItem undo;

    public EditMenu(MainController mainController) {
        super("Edit");
        this.mainController = mainController;
        init();
    }

    private void init() {
        undo = new MenuItem("Undo");
        undo.setOnAction(e->{
            Tab active = mainController.getTabController().getActive();
            if (mainController.getTabController().getControllerOf(active) instanceof ProductListTabItem) {
                ProductListTabItem controllerOf = (ProductListTabItem) mainController.getTabController().getControllerOf(active);
                controllerOf.undo();
            }

        });
        undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        redo = new MenuItem("Redo");
        redo.setOnAction(e->{
            Tab active = mainController.getTabController().getActive();
            if (mainController.getTabController().getControllerOf(active) instanceof ProductListTabItem) {
                ProductListTabItem controllerOf = (ProductListTabItem) mainController.getTabController().getControllerOf(active);
                controllerOf.redo();
            }
        });
        redo.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        getItems().addAll(undo,redo);
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
        TabPaneComponent tabController = mainController.getTabController();
        Platform.runLater(()->{
            TabItem controllerOf = tabController.getControllerOf(tabController.getActive());
            undo.setDisable(!(controllerOf instanceof ModifiableTabItem));
            redo.setDisable(!(controllerOf instanceof ModifiableTabItem));
        });
    }
}
