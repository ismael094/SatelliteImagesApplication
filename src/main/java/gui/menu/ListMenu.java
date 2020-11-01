package gui.menu;

import controller.interfaces.ProductListTabItem;
import controller.interfaces.TabItem;
import controller.search.SearchController;
import gui.components.MenuComponent;
import gui.components.TabPaneComponent;
import gui.events.*;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import model.list.ProductListDTO;
import utils.gui.Observer;
import utils.gui.ProductListDTOUtil;

public class ListMenu extends Menu implements SatInfMenuItem, Observer {

    private final MenuComponent menuComponent;
    private MenuItem create;
    private MenuItem editList;
    private MenuItem delete;
    private MenuItem addSel;
    private MenuItem addAll;
    private MenuItem deselect;
    private MenuItem preferenceImg;
    private MenuItem workflows;

    public ListMenu(MenuComponent menuComponent) {
        super("Lists");
        this.menuComponent = menuComponent;
        init();
    }

    private void init() {
        create = new MenuItem("Create new list");
        create.setOnAction(new CreateListEvent(menuComponent.getMainController()));

        editList = new MenuItem("Edit list");
        editList.setOnAction(new EditListEvent(menuComponent.getMainController()));

        delete = new MenuItem("Delete list");
        delete.setOnAction(new DeleteListEvent(menuComponent.getMainController()));

        addSel = new MenuItem("Add selected products to list");
        addSel.setOnAction(new AddSelectedToListEvent(menuComponent.getMainController()));

        addAll = new MenuItem("Add all product to list");
        addAll.setOnAction(new AddAllToListEvent(menuComponent.getMainController()));

        deselect = new MenuItem("Delete selected products from list");
        deselect.setOnAction(new DeleteSelectedFromListEvent(menuComponent.getMainController()));

        preferenceImg = new MenuItem("Add image as reference image");
        preferenceImg.setOnAction(new AddReferenceImageEvent(menuComponent.getMainController()));


        workflows = new MenuItem("Workflows");
        workflows.setOnAction(new ShowWorkflowsOfListEvent(menuComponent.getMainController()));

        menuComponent.getMainController().getTabComponent().addObserver(this);
        getItems().addAll(create,editList, delete,addSel,addAll,deselect,new SeparatorMenuItem(),preferenceImg,new SeparatorMenuItem(),workflows);
    }

    @Override
    public Menu getMenu() {
        return this;
    }

    @Override
    public String getName() {
        return this.getText();
    }

    @Override
    public void update(Object args) {
        Platform.runLater(()->{
            deselect.setDisable(!(args instanceof ProductListTabItem));
            workflows.setDisable(!(args instanceof ProductListTabItem));
            addSel.setDisable(!(args instanceof SearchController<?>));
            addAll.setDisable(!(args instanceof SearchController<?>));
        });

    }
}
