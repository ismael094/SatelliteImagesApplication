package gui;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import jfxtras.styles.jmetro.JMetroStyleClass;

public class TabPaneManager extends TabPane {

    private static TabPaneManager tabPaneManager;

    public static TabPaneManager getTabPaneManager() {
        if (tabPaneManager == null) {
            tabPaneManager = new TabPaneManager();
        }
        return tabPaneManager;
    }

    public TabPaneManager() {
        super();
        getStyleClass().add(JMetroStyleClass.UNDERLINE_TAB_PANE);
        setId("myTab");
        setPrefHeight(576.0);
        setPrefWidth(1291.0);
    }

    public Tab addTab(String title, Node node) {
        Tab tab = new Tab(title, node);
        tab.getStyleClass().add(JMetroStyleClass.BACKGROUND);
        getTabs().add(tab);
        getSelectionModel().select(tab);
        return tab;
    }

    public Tab getTab(Tab tab) {
        int index = getTabs().indexOf(tab);
        return getTabs().get(index);
    }



}
