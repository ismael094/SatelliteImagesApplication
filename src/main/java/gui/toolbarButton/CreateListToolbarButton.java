package gui.toolbarButton;

import controller.list.ListCreateAndEditController;
import gui.components.ToolBarComponent;
import gui.dialog.ListDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.list.ProductListDTO;

import java.io.IOException;
import java.net.URL;

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
        toolBar.getMainController().getListTreeViewController().reload();

    }
}
