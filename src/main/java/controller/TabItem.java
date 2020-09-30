package controller;

import gui.components.TabPaneComponent;
import javafx.concurrent.Task;
import javafx.scene.Parent;

public interface TabItem {
    void setTabPaneComponent(TabPaneComponent component);
    Parent getView();
    Task<Parent> start();
    String getName();

}
