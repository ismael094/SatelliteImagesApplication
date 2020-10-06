package gui.toolbarButton;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import gui.components.ToolBarComponent;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Modality;
import model.list.ProductListDTO;

import java.util.ArrayList;
import java.util.List;

public abstract class ToolbarButton extends Button implements EventHandler<ActionEvent> {
    protected ToolBarComponent toolBar;

    public ToolbarButton(ToolBarComponent toolBar) {
        super();
        this.toolBar = toolBar;
        getStyleClass().add("toolbarIcon");
    }

    public abstract void init();

    protected List<ProductListDTO> getProductList() {
        List<ProductListDTO> productListDTO = new ArrayList<>();
        if (toolBar.getMainController().getUserProductList().size() == 0) {
            ProductListDTO productListDTO1 = new ProductListDTO(new SimpleStringProperty("Default"),
                    new SimpleStringProperty("Default list"));
            toolBar.getMainController().getUserProductList().add(productListDTO1);
            productListDTO.add(productListDTO1);

        } else if (toolBar.getMainController().getUserProductList().size() == 1) {
            productListDTO.add(toolBar.getMainController().getUserProductList().get(0));
        } else {
            productListDTO = showAndGetList(SelectionMode.MULTIPLE,"Choose one or more list to add");
        }
        return productListDTO;
    }

    protected ObservableList<ProductListDTO> showAndGetList(SelectionMode selectionMode,String title) {
        ListView<ProductListDTO> productListListView = new ListView<>(toolBar.getMainController().getUserProductList());
        productListListView.getSelectionModel().setSelectionMode(selectionMode);
        JFXAlert alert = new JFXAlert(this.getScene().getWindow());
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setOverlayClose(true);
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label(title));
        layout.setBody(productListListView);
        JFXButton closeButton = new JFXButton("Accept");
        closeButton.getStyleClass().add("dialog-accept");
        closeButton.setOnAction(e -> alert.hideWithAnimation());
        layout.setActions(closeButton);
        alert.setContent(layout);
        alert.showAndWait();
        return productListListView.getSelectionModel().getSelectedItems();
    }
}
