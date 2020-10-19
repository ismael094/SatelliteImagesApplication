package gui.components;

import controller.MainController;
import model.events.EventType;
import model.listeners.ComponentChangeListener;
import model.events.ToolbarComponentEvent;
import gui.toolbarButton.*;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;

import java.util.*;

public class ToolBarComponent extends ToolBar implements Component{

    private final MainController mainController;
    private Map<String, ToolbarButton> buttonListMap;
    private Map<String, ToolbarButton> buttonProductListMap;
    private Map<String, ToolbarButton> buttonDownloadMap;
    private List<Map<String, ToolbarButton>> buttonList;
    //private final List<ComponentChangeListener> toolBarListener;
    private final Map<EventType.ComponentEventType, ComponentChangeListener> toolBarListener;

    public ToolBarComponent(MainController mainController) {
        super();
        this.mainController = mainController;
        this.toolBarListener = new LinkedHashMap<>();
        initButtonMap();
    }

    private void initButtonMap() {

        this.buttonListMap = new LinkedHashMap<>();
        this.buttonProductListMap = new LinkedHashMap<>();
        this.buttonDownloadMap = new LinkedHashMap<>();
        buttonList = new LinkedList<>();
        buttonList.add(buttonListMap);
        buttonList.add(buttonProductListMap);
        buttonList.add(buttonDownloadMap);

        buttonListMap.put("createList", new CreateListToolbarButton(this));
        buttonListMap.put("deleteList", new DeleteListToolbarButton(this));
        buttonListMap.put("editList", new EditListToolbarButton(this));
        buttonProductListMap.put("selectAll",new AddAllToListToolbarButton(this));
        buttonProductListMap.put("addToList", new AddSelectedToListToolbarButton(this));
        buttonProductListMap.put("deleteSelected",new DeleteSelectedFromListToolbarButton(this));
        buttonProductListMap.put("addGroundToList",new AddGroundTruthToListToolbarButton(this));
        buttonDownloadMap.put("downloadAll",new DownloadProductListToolbarButton(this));
        buttonDownloadMap.put("downloadSingle",new DownloadSelectedProductToolbarButton(this));
    }

    @Override
    public void init() {
        buttonList.forEach(map->{
            map.forEach((key,value)->{
                value.init();
                getItems().add(value);
            });
            getItems().add(new Separator());
        });
        //createButton("createList","");
        //createButton("addToList","");
        //createButton("selectAll","");
        //createButton("downloadSingle","");
        //createButton("downloadAll","");

    }

    private void createButton(String id, String text) {
        Button button = new Button(text);
        button.setId(id);
        //buttonMap.put(id,button);
        getItems().add(button);
    }

    public Button get(String id) {
        return buttonList.stream()
                .filter(m -> m.containsKey(id))
                .map(m -> m.get(id))
                .findAny()
                .orElse(null);
    }

    @Override
    public Parent getView() {
        return this;
    }

    @Override
    public MainController getMainController() {
        return mainController;
    }

    @Override
    public void addComponentListener(EventType.ComponentEventType type, ComponentChangeListener listener) {
        this.toolBarListener.put(type,listener);
    }

    @Override
    public void fireEvent(ToolbarComponentEvent event) {
        ComponentChangeListener orDefault = this.toolBarListener.getOrDefault(event.getToolbarEvent(), null);
        if (orDefault != null)
            orDefault.onComponentChange(event);
    }
}
