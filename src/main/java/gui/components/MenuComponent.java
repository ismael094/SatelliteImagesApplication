package gui.components;

import controller.MainController;
import gui.components.listener.ComponentEvent;
import gui.menu.*;
import javafx.scene.control.*;
import model.listeners.ComponentChangeListener;
import javafx.scene.Parent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.gui.Observer;

import java.util.ArrayList;
import java.util.List;


public class MenuComponent extends MenuBar implements Component{

    private final MainController mainController;
    static final Logger logger = LogManager.getLogger(MainController.class.getName());
    private final List<SatInfMenuItem> menus;
    private final List<ComponentChangeListener> listeners;
    private List<Observer> observers;

    public MenuComponent(MainController mainController) {
        super();
        this.listeners = new ArrayList<>();
        this.mainController = mainController;
        observers = new ArrayList<>();
        this.menus = new ArrayList<>();
        this.menus.add(new FileMenu(this));
        this.menus.add(new EditMenu(this));
        this.menus.add(new SearcherMenu(this));
        this.menus.add(new ListMenu(this));
        this.menus.add(new DownloadMenu(this));
        this.menus.add(new ProcessingMenu(this));
        this.menus.add(new PostProcessingMenu(this));
    }

    @Override
    public void init() {
        this.menus.forEach(m->{
            logger.atInfo().log("Menu {} loaded", m.getName());
            if (m instanceof Observer)
                mainController.getTabComponent().addObserver((Observer) m);
            getMenus().add(m.getMenu());
        });
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
        listeners.add(listener);
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

    public void initSearchView(String id) {
        ;
    }
}
