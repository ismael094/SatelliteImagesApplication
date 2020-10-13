package gui.eventhandlers;

import controller.SatelliteApplicationController;
import controller.interfaces.TabItem;
import controller.list.ListInformationController;
import gui.toolbarButton.ToolbarButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import model.list.ProductListDTO;

import java.util.List;

public class GetCurrentProductListHandler implements EventHandler<ActionEvent> {


    private final SatelliteApplicationController controller;

    public GetCurrentProductListHandler(SatelliteApplicationController controller) {
        this.controller = controller;
    }

    @Override
    public void handle(ActionEvent event) {
        Tab active = controller.getTabController().getActive();
        TabItem controllerOf = controller.getTabController().getControllerOf(active);
        ProductListDTO list;
        if (controllerOf instanceof ListInformationController) {
            ListInformationController listController = (ListInformationController)controllerOf;
            list = listController.getProductList();

        } else {
            /*List<ProductListDTO> productList = showAndGetList(SelectionMode.SINGLE,"Select list to edit");
            if (productList.size() == 0)
                return;
            else
                list = productList.get(0);*/
        }
    }
}
