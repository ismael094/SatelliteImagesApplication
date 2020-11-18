package controller.interfaces;

import gui.components.TabPaneComponent;
import javafx.concurrent.Task;
import javafx.scene.Parent;

/**
 * Controllers to show information in the TabPaneComponent must implement this interface
 */
public interface TabItem {
    /**
     * Set the TabPaneComponent
     * @param component TabPaneComponent
     */
    void setTabPaneComponent(TabPaneComponent component);

    /**
     * Get the view of the controller
     * @return Parent view of the controller
     */
    Parent getView();

    /**
     * Start busy services in a different thread
     * @return Task to execute services
     */
    Task<Parent> start();

    /**
     * Return name of the tab
     * @return Name of the tab
     */
    String getName();

    /**
     * Return unique identification of the controller
     * @return Id of controller
     */
    String getItemId();
}
