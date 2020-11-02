package gui.menu;

import controller.interfaces.ModifiableTabItem;
import controller.interfaces.ProductListTabItem;
import controller.interfaces.TabItem;
import gui.components.MenuComponent;
import gui.components.TabPaneComponent;
import gui.events.RedoEvent;
import gui.events.UndoEvent;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import utils.gui.Observer;

public class EditMenu extends Menu implements SatInfMenuItem {
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
        undo.setOnAction(new UndoEvent(menuComponent.getMainController()));
        undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        redo = new MenuItem("Redo");
        redo.setOnAction(new RedoEvent(menuComponent.getMainController()));
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
    public void update(Object args) {
        Platform.runLater(()->{
            if (!(args instanceof ModifiableTabItem)) {
                undo.setDisable(true);
                redo.setDisable(true);
            } else {
                ModifiableTabItem modifiableTabItem = (ModifiableTabItem) args;
                if (modifiableTabItem.getRedo() != null) {
                    setMenuItem(redo, "Redo " + modifiableTabItem.getRedo(), false);
                } else {
                    setMenuItem(redo, "Redo", true);
                }

                if (modifiableTabItem.getUndo() != null) {
                    setMenuItem(undo, "Undo " + modifiableTabItem.getUndo(), false);
                } else {
                    setMenuItem(undo, "Undo", true);
                }
            }

        });
    }

    private void setMenuItem(MenuItem item, String text, boolean disabled) {
        item.setText(text);
        item.setDisable(disabled);
    }
}
