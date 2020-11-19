package gui.events;

import controller.MainController;
import controller.list.ListCreateAndEditController;
import gui.ExecutedEvent;
import gui.dialog.ListDialog;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import model.events.EventType;
import model.list.ProductListDTO;
import utils.AlertFactory;

import static java.lang.System.currentTimeMillis;

public class CreateListEvent extends Event {

    public CreateListEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        ListDialog createList = new ListDialog("Create list");
        ListCreateAndEditController controller = createList.load();
        controller.setProductList(new ProductListDTO(new SimpleStringProperty(), new SimpleStringProperty()));
        createList.showAndWait();
        ProductListDTO productListDTO = controller.getProductList();
        if (productListDTO !=null) {
            mainController.getListTreeViewComponent().setDisable(true);

            //Save list in database
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    long start = currentTimeMillis();
                    mainController.getUserManager().getUser().getProductListsDTO().add(productListDTO);
                    return null;
                }
            };

            //On succeed, reload treview of list
            task.setOnSucceeded(event1 -> {
                long start = currentTimeMillis();
                mainController.fireEvent(new ExecutedEvent(this, EventType.LIST,"List "+productListDTO.getName()+" created"));
                System.out.println((currentTimeMillis()-start)/1000.0 + "s fire event");
                start = currentTimeMillis();
                mainController.getListTreeViewComponent().setDisable(false);
                System.out.println((currentTimeMillis()-start)/1000.0 + "s reload treeview");

            });
            //On failed, error show alert
            task.setOnFailed(event1 -> {
                Platform.runLater(()-> AlertFactory.showErrorDialog("Create list","Error when creatin list","An error has occurred while creating list named" + productListDTO.getName()));
                mainController.getListTreeViewComponent().setDisable(false);
            });

            new Thread(task).start();
        }
    }
}
