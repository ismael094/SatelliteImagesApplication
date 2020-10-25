package gui.menu;

import controller.MainController;
import controller.interfaces.ProductListTabItem;
import controller.interfaces.TabItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public class EditMenu extends Menu implements SatInfMenuItem{
    private final MainController mainController;

    public EditMenu(MainController mainController) {
        super("Edit");
        this.mainController = mainController;
        init();
    }

    private void init() {
        MenuItem undo = new MenuItem("Undo");
        undo.setOnAction(e->{
            Tab active = mainController.getTabController().getActive();
            if (mainController.getTabController().getControllerOf(active) instanceof ProductListTabItem) {
                ProductListTabItem controllerOf = (ProductListTabItem) mainController.getTabController().getControllerOf(active);
                controllerOf.undo();
            }

        });
        undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        MenuItem redo = new MenuItem("Redo");
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
}
