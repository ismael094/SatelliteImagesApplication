package gui.menu;

import controller.MainController;
import gui.events.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import utils.gui.WorkflowUtil;

public class ListMenu extends Menu implements SatInfMenuItem{

    private final MainController mainController;

    public ListMenu(MainController mainController) {
        super("Lists");
        this.mainController = mainController;
        init();
    }

    private void init() {
        MenuItem create = new MenuItem("Create new list");
        create.setOnAction(new CreateListEvent(mainController));

        MenuItem editList = new MenuItem("Edit list");
        editList.setOnAction(new EditListEvent(mainController));

        MenuItem delete = new MenuItem("Delete list");
        delete.setOnAction(new DeleteListEvent(mainController));

        MenuItem addSel = new MenuItem("Add selected products to list");
        addSel.setOnAction(new AddSelectedToListEvent(mainController));

        MenuItem addAll = new MenuItem("Add all product to list");
        addAll.setOnAction(new AddAllToListEvent(mainController));

        MenuItem deselect = new MenuItem("Delete selected products from list");
        deselect.setOnAction(new DeleteSelectedFromListEvent(mainController));

        MenuItem preferenceImg = new MenuItem("Add image as reference image");
        preferenceImg.setOnAction(new AddReferenceImageEvent(mainController));


        MenuItem workflows = new MenuItem("List workflows");


        getItems().addAll(create,editList, delete,addSel,addAll,deselect,new SeparatorMenuItem(),preferenceImg,workflows);
    }

    @Override
    public Menu getMenu() {
        return this;
    }

    @Override
    public String getName() {
        return this.getText();
    }
}
