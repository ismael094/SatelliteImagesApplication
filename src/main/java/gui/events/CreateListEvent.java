package gui.events;

import controller.MainController;
import controller.list.ListCreateAndEditController;
import gui.dialog.ListDialog;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import model.events.EventType;
import model.events.ToolbarComponentEvent;
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
            mainController.getListTreeViewController().setDisable(true);
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    long start = currentTimeMillis();
                    mainController.getUserProductList().add(productListDTO);
                    System.out.println((currentTimeMillis()-start)/1000.0 + "s");
                    return null;
                }
            };
            task.setOnSucceeded(event1 -> {
                long start = currentTimeMillis();
                mainController.getToolBarComponent().fireEvent(
                        new ToolbarComponentEvent<>(this, EventType.ComponentEventType.LIST_CREATED,
                                "List "+productListDTO.getName()+" created"));

                System.out.println((currentTimeMillis()-start)/1000.0 + "s fire event");
                start = currentTimeMillis();
                mainController.getListTreeViewController().setDisable(false);
                System.out.println((currentTimeMillis()-start)/1000.0 + "s reload treeview");

            });
            task.setOnFailed(event1 -> {
                Platform.runLater(()-> AlertFactory.showErrorDialog("Create list","Error when creatin list","An error has occurred while creating list named" + productListDTO.getName()));
                mainController.getListTreeViewController().setDisable(false);
            });

            new Thread(task).start();
        }
    }
}
