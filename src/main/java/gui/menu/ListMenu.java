package gui.menu;

import controller.MainController;
import controller.interfaces.TabItem;
import controller.search.SearchController;
import gui.components.TabPaneComponent;
import gui.events.*;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import model.list.ProductListDTO;
import utils.gui.Observer;
import utils.gui.ProductListDTOUtil;
import utils.gui.WorkflowUtil;

public class ListMenu extends Menu implements SatInfMenuItem, Observer {

    private final MainController mainController;
    private MenuItem create;
    private MenuItem editList;
    private MenuItem delete;
    private MenuItem addSel;
    private MenuItem addAll;
    private MenuItem deselect;
    private MenuItem preferenceImg;
    private MenuItem workflows;

    public ListMenu(MainController mainController) {
        super("Lists");
        this.mainController = mainController;
        init();
    }

    private void init() {
        create = new MenuItem("Create new list");
        create.setOnAction(new CreateListEvent(mainController));

        editList = new MenuItem("Edit list");
        editList.setOnAction(new EditListEvent(mainController));

        delete = new MenuItem("Delete list");
        delete.setOnAction(new DeleteListEvent(mainController));

        addSel = new MenuItem("Add selected products to list");
        addSel.setOnAction(new AddSelectedToListEvent(mainController));

        addAll = new MenuItem("Add all product to list");
        addAll.setOnAction(new AddAllToListEvent(mainController));

        deselect = new MenuItem("Delete selected products from list");
        deselect.setOnAction(new DeleteSelectedFromListEvent(mainController));

        preferenceImg = new MenuItem("Add image as reference image");
        preferenceImg.setOnAction(new AddReferenceImageEvent(mainController));


        workflows = new MenuItem("Workflows");
        workflows.setOnAction(new ShowWorkflowsOfListEvent(mainController));

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
    public void update() {
        TabPaneComponent tabController = mainController.getTabController();
        Platform.runLater(()->{
            ProductListDTO currentList = ProductListDTOUtil.getCurrentList(tabController);
            deselect.setDisable(currentList == null);
            workflows.setDisable(currentList == null);
            TabItem controllerOf = tabController.getControllerOf(tabController.getActive());
            addSel.setDisable(!(controllerOf instanceof SearchController<?>));
            addAll.setDisable(!(controllerOf instanceof SearchController<?>));
        });

    }
}
