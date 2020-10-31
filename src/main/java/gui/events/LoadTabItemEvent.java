package gui.events;

import controller.MainController;
import controller.interfaces.TabItem;
import javafx.event.ActionEvent;

public class LoadTabItemEvent extends Event {
    private TabItem tabItem;

    public LoadTabItemEvent(MainController controller, TabItem tabItem) {
        super(controller);
        this.tabItem = tabItem;
    }

    @Override
    public void handle(ActionEvent event) {
        mainController.getTabComponent().load(tabItem);
    }
}
