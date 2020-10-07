package gui.toolbarButton;

import gui.components.ToolBarComponent;
import gui.components.listener.ComponentEventType;
import gui.components.listener.ToolbarComponentEvent;
import javafx.event.ActionEvent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import model.list.ProductListDTO;
import services.database.ProductListDBDAO;

import java.util.List;

public class DeleteListToolbarButton extends ToolbarButton {

    public DeleteListToolbarButton(ToolBarComponent toolBar) {
        super(toolBar);
        setId("deleteList");
    }

    @Override
    public void init() {
        setOnAction(this);
        Tooltip tooltip = new Tooltip("Delete list");
        tooltip.setShowDelay(new Duration(0.1));
        tooltip.setHideDelay(new Duration(0.5));
        setTooltip(tooltip);
    }

    @Override
    public void handle(ActionEvent event) {
        List<ProductListDTO> productList = showAndGetList(SelectionMode.MULTIPLE,"Delete list");
        ProductListDBDAO.getInstance().delete(productList);
        toolBar.getMainController().getUserProductList().removeAll(productList);
        toolBar.fireEvent(new ToolbarComponentEvent(this, ComponentEventType.LIST_DELETED));
    }
}
