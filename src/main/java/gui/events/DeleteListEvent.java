package gui.events;

import controller.MainController;
import javafx.event.ActionEvent;
import javafx.scene.control.SelectionMode;
import model.events.EventType;
import model.events.ToolbarComponentEvent;
import model.list.ProductListDTO;
import services.database.ProductListDBDAO;

import java.util.List;

public class DeleteListEvent extends Event {
    public DeleteListEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        List<ProductListDTO> productList = showAndGetList(SelectionMode.MULTIPLE,"Delete list");
        if (!productList.isEmpty()) {
            ProductListDBDAO.getInstance().delete(productList);
            mainController.getUserProductList().removeAll(productList);
            mainController.getToolBarComponent().fireEvent(new ToolbarComponentEvent<>(this, EventType.ComponentEventType.LIST_DELETED,"Lists successfully deleted!"));
        }

    }
}
