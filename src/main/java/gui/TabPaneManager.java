package gui;

import com.jfoenix.controls.JFXTextArea;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import jfxtras.styles.jmetro.JMetroStyleClass;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TabPaneManager extends TabPane {

    static final Logger logger = LogManager.getLogger(TabPaneManager.class.getName());

    private static TabPaneManager tabPaneManager;

    public static TabPaneManager getTabPaneManager() {
        if (tabPaneManager == null) {
            logger.atLevel(Level.INFO).log("TabPaneManager initiated");
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

    public Tab addTab(String title, Parent node) {
        logger.atLevel(Level.INFO).log("Added tab {}",title);
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
