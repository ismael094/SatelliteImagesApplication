package gui.events;

import controller.MainController;
import controller.postprocessing.ProductListProcessingResultsController;
import javafx.event.ActionEvent;
import model.list.ProductListDTO;
import utils.AlertFactory;
import utils.gui.ProductListDTOUtil;

public class OpenProcessingResultsOfCurrentProductListViewEvent extends Event {
    public OpenProcessingResultsOfCurrentProductListViewEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        ProductListDTO currentList = ProductListDTOUtil.getCurrentList(mainController.getTabComponent());
        if (currentList != null) {
            ProductListProcessingResultsController controller = new ProductListProcessingResultsController(currentList);
            if (controller.haveBeenProcessed())
                mainController.getTabComponent().load(controller);
            else
                AlertFactory.showErrorDialog("Not processed", "Not processed", "The current list have not been processed");

        } else {
            AlertFactory.showErrorDialog("Not in a list tab", "Not in a list tab", "You are not in a list tab to execute this action!");
        }
    }
}
