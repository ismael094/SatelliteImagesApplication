package gui.events;

import controller.MainController;
import javafx.event.ActionEvent;
import utils.gui.WorkflowUtil;

public class ShowMyWorkflowsOfUserEvent extends Event{

    public ShowMyWorkflowsOfUserEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        WorkflowUtil.loadMyWorkflowView(mainController.getUserManager(), null);
    }
}
