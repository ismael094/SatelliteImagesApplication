package controller;

import controller.interfaces.TabItem;
import gui.components.TabPaneComponent;
import gui.components.tabcomponent.SatInfTabPaneComponent;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class InformationController implements TabItem {
    private Parent parent;
    private TabPaneComponent setTabPaneComponent;

    public InformationController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/InformationView.fxml"));
        loader.setController(this);
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setTabPaneComponent(TabPaneComponent component) {
        this.setTabPaneComponent = component;
    }

    @Override
    public Parent getView() {
        return parent;
    }

    @Override
    public Task<Parent> start() {
        return new Task<Parent>() {
            @Override
            protected Parent call() throws Exception {
                return parent;
            }
        };
    }

    @Override
    public String getName() {
        return "Information";
    }

    @Override
    public String getItemId() {
        return "InformationID";
    }
}
