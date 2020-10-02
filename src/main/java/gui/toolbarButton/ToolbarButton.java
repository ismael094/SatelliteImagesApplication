package gui.toolbarButton;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import gui.components.ToolBarComponent;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import model.ProductList;

import java.util.ArrayList;
import java.util.List;

public abstract class ToolbarButton extends Button implements EventHandler<ActionEvent> {
    protected ToolBarComponent toolBar;

    public ToolbarButton(ToolBarComponent toolBar) {
        super();
        this.toolBar = toolBar;
    }

    public abstract void init();

    protected List<ProductList> getProductList() {
        List<ProductList> productList = new ArrayList<>();
        if (toolBar.getMainController().getUserProductList().size() == 0) {
            ProductList productList1 = new ProductList(new SimpleStringProperty("Default"),
                    new SimpleStringProperty("Default list"));
            toolBar.getMainController().getUserProductList().add(productList1);
            productList.add(productList1);

        } else if (toolBar.getMainController().getUserProductList().size() == 1) {
            productList.add(toolBar.getMainController().getUserProductList().get(0));
        } else {
            productList = showAndGetList();
        }
        return productList;
    }

    private ObservableList<ProductList> showAndGetList() {
        ListView<ProductList> productListListView = new ListView<>(toolBar.getMainController().getUserProductList());
        productListListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        JFXAlert alert = new JFXAlert(this.getScene().getWindow());
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setOverlayClose(true);
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label("Choose one or more list to add"));
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
