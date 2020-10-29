package controller.interfaces;

import gui.components.SatInfTabPaneComponent;
import javafx.concurrent.Task;
import javafx.scene.Parent;

public interface TabItem {
    void setTabPaneComponent(SatInfTabPaneComponent component);
    Parent getView();
    Task<Parent> start();
    String getName();
    String getItemId();
}
