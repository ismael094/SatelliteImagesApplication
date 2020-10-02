package gui.components;

import controller.SatelliteApplicationController;
import controller.TabItem;
import controller.list.CreateListController;
import controller.search.SearchController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gui.toolbarButton.*;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.ToolBar;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.products.Product;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class ToolBarComponent extends ToolBar implements Component{

    private final SatelliteApplicationController mainController;
    private HashMap<String, ToolbarButton> buttonMap;

    public ToolBarComponent(SatelliteApplicationController mainController) {
        super();
        this.mainController = mainController;
        initButtonMap();
    }

    private void initButtonMap() {
        this.buttonMap = new HashMap<>();
        buttonMap.put("selectAll",new AddAllToListToolbarButton(this));
        buttonMap.put("addToList", new AddSelectedToListToolbarButton(this));
        buttonMap.put("createList", new CreateListToolbarButton(this));
        buttonMap.put("deleteSelected",new DeleteSelectedFromListToolbarButton(this));
    }

    @Override
    public void init() {
        buttonMap.forEach((key,value)->{
            value.init();
            getItems().add(value);
        });
        //createButton("createList","");
        //createButton("addToList","");
        //createButton("selectAll","");
        createButton("downloadSingle","");
        createButton("downloadAll","");

    }

    private void createButton(String id, String text) {
        Button button = new Button(text);
        button.setId(id);
        //buttonMap.put(id,button);
        getItems().add(button);
    }

    public Button get(String id) {
        return buttonMap.getOrDefault(id,null);
    }

    @Override
    public Parent getView() {
        return this;
    }

    @Override
    public SatelliteApplicationController getMainController() {
        return mainController;
    }
}
