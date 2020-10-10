package gui.toolbarButton;

import controller.list.ListCreateAndEditController;
import gui.components.ToolBarComponent;
import model.events.EventType;
import model.events.ToolbarComponentEvent;
import gui.dialog.ListDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import model.list.ProductListDTO;

public class CreateListToolbarButton extends ToolbarButton {

    public CreateListToolbarButton(ToolBarComponent toolBarComponent) {
        super(toolBarComponent);
        setId("createList");
    }

    @Override
    public void init() {
        setOnAction(this);
        Tooltip tooltip = new Tooltip("Create new list");
        tooltip.setShowDelay(new Duration(0.1));
        tooltip.setHideDelay(new Duration(0.5));
        setTooltip(tooltip);
    }


    @Override
    public void handle(ActionEvent event) {
        ListDialog createList = new ListDialog("Create list");
        ListCreateAndEditController controller = createList.load();
        controller.setProductList(new ProductListDTO(new SimpleStringProperty(), new SimpleStringProperty()));
        createList.showAndWait();
        ProductListDTO productListDTO = controller.getProductList();
        if (productListDTO !=null)
            toolBar.getMainController().getUserProductList().add(productListDTO);
        toolBar.fireEvent(new ToolbarComponentEvent(this, EventType.ComponentEventType.LIST_CREATED));

    }
}
