package gui.components;

import controller.MainController;
import gui.components.listener.ComponentEvent;
import javafx.application.Platform;
import model.events.EventType;
import model.listeners.ComponentChangeListener;
import gui.toolbarButton.*;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.gui.Observer;

import java.util.*;

public class ToolBarComponent extends ToolBar implements Component, Observer{

    private final MainController mainController;
    private Map<String, ToolbarButton> buttonListMap;
    private Map<String, ToolbarButton> buttonProductListMap;
    private Map<String, ToolbarButton> buttonDownloadMap;
    private Map<String, ToolbarButton> buttonProcessingMap;
    private List<Map<String, ToolbarButton>> buttonList;
    private final List<Observer> observers;
    private final List<ComponentChangeListener> listeners;

    static final Logger logger = LogManager.getLogger(ToolBarComponent.class.getName());

    public ToolBarComponent(MainController mainController) {
        super();
        this.mainController = mainController;
        this.listeners = new ArrayList<>();
        initButtonMap();
        observers = new ArrayList<>();
    }

    private void initButtonMap() {
        mainController.getTabComponent().addObserver(this);
        this.buttonListMap = new LinkedHashMap<>();
        this.buttonProductListMap = new LinkedHashMap<>();
        this.buttonDownloadMap = new LinkedHashMap<>();
        this.buttonProcessingMap = new LinkedHashMap<>();
        buttonList = new LinkedList<>();
        buttonList.add(buttonListMap);
        buttonList.add(buttonProductListMap);
        buttonList.add(buttonDownloadMap);
        buttonList.add(buttonProcessingMap);

        loadListButtons();
        loadProductListButton();
        loadDownloadButtons();
        loadProcessingButtons();
    }

    private void loadProcessingButtons() {
        buttonProcessingMap.put("processList",new ProcessListToolbarButton(this));
    }

    private void loadDownloadButtons() {
        buttonDownloadMap.put("downloadAll",new DownloadProductListToolbarButton(this));
        buttonDownloadMap.put("downloadSingle",new DownloadSelectedProductToolbarButton(this));
    }

    private void loadProductListButton() {
        buttonProductListMap.put("selectAll",new AddAllToListToolbarButton(this));
        buttonProductListMap.put("addToList", new AddSelectedToListToolbarButton(this));
        buttonProductListMap.put("deleteSelected",new DeleteSelectedFromListToolbarButton(this));
        buttonProductListMap.put("addGroundToList",new AddReferenceImageToListToolbarButton(this));
    }

    private void loadListButtons() {
        buttonListMap.put("createList", new CreateListToolbarButton(this));
        buttonListMap.put("deleteList", new DeleteListToolbarButton(this));
        buttonListMap.put("editList", new EditListToolbarButton(this));
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
    public void addComponentListener(ComponentChangeListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void fireEvent(ComponentEvent event) {
        listeners.forEach(l->l.onComponentChange(event));
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void updateObservers(Object args) {
        observers.forEach(o->o.update(args));
    }

    @Override
    public void update(Object args) {
        Platform.runLater(()->{
            buttonList.forEach(typeButton->{
                typeButton.forEach((key,value)->{
                    value.update(args);
                });
            });
        });
    }
}
