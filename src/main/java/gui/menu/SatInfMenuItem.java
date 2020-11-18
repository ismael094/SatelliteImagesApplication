package gui.menu;

import javafx.scene.control.Menu;

public interface SatInfMenuItem {
    /**
     * Get Menu
     * @return menu
     */
    Menu getMenu();

    /**
     * Get Menu name
     * @return Name of menu
     */
    String getName();

    /**
     * Update state
     * @param args Element changed
     */
    void update(Object args);
}
