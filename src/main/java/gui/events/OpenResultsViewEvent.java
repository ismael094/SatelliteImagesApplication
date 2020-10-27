package gui.events;

import controller.MainController;
import controller.interfaces.ProductListTabItem;
import controller.interfaces.TabItem;
import controller.results.ProductListProcessingResultsController;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import model.list.ProductListDTO;
import utils.AlertFactory;

public class OpenResultsViewEvent extends Event {
    public OpenResultsViewEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        Tab active = mainController.getTabController().getActive();
        TabItem tabItem = mainController.getTabController().getControllerOf(active);
        if (tabItem instanceof ProductListTabItem) {
            ProductListDTO productList = ((ProductListTabItem) tabItem).getProductList();
            mainController.getTabController().load(new ProductListProcessingResultsController(productList));
        } else {
            AlertFactory.showErrorDialog("Not in a list tab", "Not in a list tab", "You are not in a list tab to execute this action!");
        }
    }
}
