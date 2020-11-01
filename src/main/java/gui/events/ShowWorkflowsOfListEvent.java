package gui.events;

import controller.MainController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import model.list.ProductListDTO;
import model.processing.workflow.WorkflowDTO;
import utils.gui.WorkflowUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
