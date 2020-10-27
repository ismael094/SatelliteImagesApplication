package gui.events;

import controller.MainController;
import controller.interfaces.ProductListTabItem;
import controller.interfaces.TabItem;
import controller.results.ProductListProcessingResultsController;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import model.list.ProductListDTO;
import utils.AlertFactory;

import java.awt.image.BufferedImage;

public class ProcessListEvent extends Event {
    public ProcessListEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        Tab active = mainController.getTabController().getActive();
        TabItem tabItem = mainController.getTabController().getControllerOf(active);
        if (tabItem instanceof ProductListTabItem) {
            ProductListDTO productList = ((ProductListTabItem) tabItem).getProductList();
            Task<Boolean> task = null;
            try {
                task = mainController.getProcessor().process(productList);
                task.setOnFailed(e -> {
                    AlertFactory.showErrorDialog("Error", "ERROR", "Error while processing products " + e.getSource().getException().getLocalizedMessage());
                    e.getSource().getException().printStackTrace();
                });

                new Thread(task).start();
            } catch (Exception e) {
                e.printStackTrace();
            }



            mainController.getTabController().load(new ProductListProcessingResultsController(productList));


        } else
            AlertFactory.showErrorDialog("Error", "Not in a list!", "Open a lists to process it!");
    }
}
