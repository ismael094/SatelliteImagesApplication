package gui.events;

import controller.MainController;
import gui.ExecutedEvent;
import javafx.event.ActionEvent;
import javafx.scene.control.SelectionMode;
import model.events.EventType;
import model.list.ProductListDTO;
import services.database.ProductListDBDAO;

import java.util.List;

public class DeleteListEvent extends Event {
    public DeleteListEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        //Select product list and delete it from database
        List<ProductListDTO> productList = showAndGetList(SelectionMode.MULTIPLE,"Delete list");
        if (!productList.isEmpty()) {
            ProductListDBDAO.getInstance().delete(productList);
            mainController.getUserManager().getUser().getProductListsDTO().removeAll(productList);
            mainController.fireEvent(new ExecutedEvent(this, EventType.LIST,"Lists successfully deleted!"));
        }

    }
}
