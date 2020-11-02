package gui.menu;

import javafx.scene.control.Menu;

public interface SatInfMenuItem {
    Menu getMenu();
    String getName();
    void update(Object args);
}
