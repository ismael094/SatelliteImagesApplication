package gui.toolbarButton;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import model.events.EventType;
import model.events.ToolbarComponentEvent;
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
        setIcon(MaterialDesignIcon.FOLDER_REMOVE,"1.5em");
        setTooltip("Delete list");
    }

    @Override
    public void handle(ActionEvent event) {
        List<ProductListDTO> productList = showAndGetList(SelectionMode.MULTIPLE,"Delete list");
        ProductListDBDAO.getInstance().delete(productList);
        toolBar.getMainController().getUserProductList().removeAll(productList);
        toolBar.fireEvent(new ToolbarComponentEvent<>(this,EventType.ComponentEventType.LIST_DELETED,"Lists successfully deleted!"));
    }
}
