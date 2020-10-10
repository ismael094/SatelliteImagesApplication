package gui.toolbarButton;

import controller.interfaces.TabItem;
import controller.list.ListCreateAndEditController;
import controller.list.ListInformationController;
import gui.components.ToolBarComponent;
import model.events.EventType;
import model.events.ToolbarComponentEvent;
import gui.dialog.ListDialog;
import javafx.event.ActionEvent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import model.list.ProductListDTO;

import java.util.List;

public class EditListToolbarButton extends ToolbarButton {

    public EditListToolbarButton(ToolBarComponent toolBar) {
        super(toolBar);
        setId("editList");
    }

    @Override
    public void init() {
        setOnAction(this);
        Tooltip tooltip = new Tooltip("Edit selected list");
        tooltip.setShowDelay(new Duration(0.1));
        tooltip.setHideDelay(new Duration(0.5));
        setTooltip(tooltip);
    }

    @Override
    public void handle(ActionEvent event) {
        Tab active = toolBar.getMainController().getTabController().getActive();
        TabItem controllerOf = toolBar.getMainController().getTabController().getControllerOf(active);
        ProductListDTO list;
        if (controllerOf instanceof ListInformationController) {
            ListInformationController listController = (ListInformationController)controllerOf;
            list = listController.getProductList();

        } else {
            List<ProductListDTO> productList = showAndGetList(SelectionMode.SINGLE,"Select list to edit");
            if (productList.size() == 0)
                return;
            else
                list = productList.get(0);
        }
        ListDialog edit_list = new ListDialog("Edit list");
        ListCreateAndEditController load = edit_list.load();
        load.setProductList(list);
        edit_list.showAndWait();
        load.getProductList();
        toolBar.fireEvent(new ToolbarComponentEvent(this, EventType.ComponentEventType.LIST_UPDATED));
    }
}
