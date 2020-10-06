package gui.toolbarButton;

import controller.interfaces.TabItem;
import controller.list.ListController;
import gui.components.ToolBarComponent;
import javafx.event.ActionEvent;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import model.list.ProductListDTO;

public class EditListToolbarButton extends ToolbarButton {

    public EditListToolbarButton(ToolBarComponent toolBar) {
        super(toolBar);
        setId("editList");
    }

    @Override
    public void init() {
        setOnAction(this);
        Tooltip tooltip = new Tooltip("Edit selected list");
        tooltip.setShowDelay(new Duration(0.1));
        tooltip.setHideDelay(new Duration(0.5));
        setTooltip(tooltip);
    }

    @Override
    public void handle(ActionEvent event) {
        Tab active = toolBar.getMainController().getTabController().getActive();
        if (active instanceof TabItem) {
            TabItem listController = (TabItem)active;
            ProductListDTO productListDTO = toolBar.getMainController().getUserProductList().stream().filter(pL -> pL.getName().equals(listController.getName()))
                    .findAny()
                    .orElse(null);
        }

        return;
    }
}
