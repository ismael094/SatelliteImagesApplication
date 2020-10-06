package gui.components;

import controller.SatelliteApplicationController;
import gui.toolbarButton.*;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;

import java.util.HashMap;

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
        buttonMap.put("createList", new CreateListToolbarButton(this));
        buttonMap.put("addToList", new AddSelectedToListToolbarButton(this));
        buttonMap.put("editList", new EditListToolbarButton(this));
        buttonMap.put("selectAll",new AddAllToListToolbarButton(this));
        buttonMap.put("deleteSelected",new DeleteSelectedFromListToolbarButton(this));
    }

    @Override
    public void init() {
        buttonMap.forEach((key,value)->{
            System.out.println(key);
            value.init();
            getItems().add(value);
        });
        getItems().add(new Separator());
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
