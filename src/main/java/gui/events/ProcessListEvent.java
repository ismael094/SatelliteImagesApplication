package gui.events;

import controller.MainController;
import controller.interfaces.ProductListTabItem;
import controller.interfaces.TabItem;
import controller.postprocessing.ProductListProcessingResultsController;
import gui.ExecutedEvent;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import model.events.EventType;
import model.list.ProductListDTO;
import utils.AlertFactory;

public class ProcessListEvent extends Event {
    public ProcessListEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        Tab active = mainController.getTabComponent().getActive();
        TabItem tabItem = mainController.getTabComponent().getControllerOf(active);
        //If active tab is ProductListTabItem, process list
        if (tabItem instanceof ProductListTabItem) {
            ProductListDTO productList = ((ProductListTabItem) tabItem).getProductList();
            Task<Boolean> task = null;
            try {
                AlertFactory.showInfoDialog("Processing", "Processing", "Processing has started! " +
                        "This could last several minutes, even hour. You´ll receive a " +
                        "message when all products had been processed! ");
                task = mainController.getProductProcessor().process(productList);
                task.setOnFailed(e -> {
                    AlertFactory.showErrorDialog("Error", "ERROR", "Error while processing products " + e.getSource().getException().getLocalizedMessage());
                    e.getSource().getException().printStackTrace();
                });

                task.setOnSucceeded(e -> {
                    AlertFactory.showSuccessDialog("Processing", "Processing", "Processing completed!");
                });

                new Thread(task).start();
                mainController.fireEvent(new ExecutedEvent(this, EventType.PROCESSING,"Processing list "+productList.getName()));

            } catch (Exception e) {
                e.printStackTrace();
            }
            mainController.getTabComponent().load(new ProductListProcessingResultsController(productList));


        } else
            AlertFactory.showErrorDialog("Error", "Not in a list!", "Open a lists to process it!");
    }
}
