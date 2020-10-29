package gui.menu;

import controller.MainController;
import controller.interfaces.ModifiableTabItem;
import controller.interfaces.ProductListTabItem;
import controller.interfaces.TabItem;
import gui.components.MenuComponent;
import gui.components.SatInfTabPaneComponent;
import gui.components.tabcomponent.TabPaneComponent;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import utils.gui.Observer;

public class EditMenu extends Menu implements SatInfMenuItem, Observer {
    private final MenuComponent menuComponent;
    private MenuItem redo;
    private MenuItem undo;

    public EditMenu(MenuComponent menuComponent) {
        super("Edit");
        this.menuComponent = menuComponent;
        init();
    }

    private void init() {
        undo = new MenuItem("Undo");
        undo.setOnAction(e->{

            Tab active = menuComponent.getMainController().getTabComponent().getActive();
            if (menuComponent.getMainController().getTabComponent().getControllerOf(active) instanceof ProductListTabItem) {
                ProductListTabItem controllerOf = (ProductListTabItem) menuComponent.getMainController().getTabComponent().getControllerOf(active);
                controllerOf.undo();
            }

        });
        undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        redo = new MenuItem("Redo");
        redo.setOnAction(e->{
            Tab active = menuComponent.getMainController().getTabComponent().getActive();
            if (menuComponent.getMainController().getTabComponent().getControllerOf(active) instanceof ProductListTabItem) {
                ProductListTabItem controllerOf = (ProductListTabItem) menuComponent.getMainController().getTabComponent().getControllerOf(active);
                controllerOf.redo();
            }
        });
        redo.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        getItems().addAll(undo,redo);
        menuComponent.getMainController().getTabComponent().addObserver(this);
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
            TabItem controllerOf = tabController.getControllerOf(tabController.getActive());
            undo.setDisable(!(controllerOf instanceof ModifiableTabItem));
            redo.setDisable(!(controllerOf instanceof ModifiableTabItem));
        });
    }
}
