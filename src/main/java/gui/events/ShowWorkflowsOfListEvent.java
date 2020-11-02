package gui.events;

import controller.MainController;
import javafx.event.ActionEvent;
import model.list.ProductListDTO;
import utils.gui.WorkflowUtil;

public class ShowWorkflowsOfListEvent extends Event{

    public ShowWorkflowsOfListEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        ProductListDTO singleProductList = getSingleProductList();
        WorkflowUtil.loadMyWorkflowView(mainController.getUserManager(),singleProductList);
    }
}
