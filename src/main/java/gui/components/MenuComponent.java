package gui.components;

import controller.MainController;
import gui.menu.*;
import javafx.scene.control.*;
import model.events.EventType;
import model.listeners.ComponentChangeListener;
import model.events.ToolbarComponentEvent;
import javafx.scene.Parent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class MenuComponent extends MenuBar implements Component{

    private final MainController mainController;
    static final Logger logger = LogManager.getLogger(MainController.class.getName());
    private final List<SatInfMenuItem> menus;

    public MenuComponent(MainController mainController) {
        super();
        this.mainController = mainController;
        this.menus = new ArrayList<>();
        this.menus.add(new FileMenu(mainController));
        this.menus.add(new EditMenu(mainController));
        this.menus.add(new SearcherMenu(mainController));
        this.menus.add(new ListMenu(mainController));
        this.menus.add(new DownloadMenu(mainController));
        this.menus.add(new ProcessingMenu(mainController));
    }

    @Override
    public void init() {
        this.menus.forEach(m->{
            logger.atInfo().log("Menu {} loaded", m.getName());
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
    public void addComponentListener(EventType.ComponentEventType type, ComponentChangeListener listener) {

    }

    @Override
    public void fireEvent(ToolbarComponentEvent event) {

    }

    public void initSearchView(String id) {
        ;
    }
}
