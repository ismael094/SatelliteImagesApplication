package gui.events;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import controller.MainController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Modality;
import jfxtras.styles.jmetro.JMetro;
import model.list.ProductListDTO;

import java.util.ArrayList;
import java.util.List;

import static utils.ThemeConfiguration.getJMetroStyled;

public abstract class Event implements EventHandler<ActionEvent> {

    protected final MainController mainController;

    public Event(MainController controller) {
        this.mainController = controller;
    }

    protected List<ProductListDTO> getProductLists() {
        List<ProductListDTO> productListDTO = new ArrayList<>();
        if (mainController.getUserProductList().size() == 0) {
            ProductListDTO productListDTO1 = new ProductListDTO(new SimpleStringProperty("Default"),
                    new SimpleStringProperty("Default list"));
            mainController.getUserProductList().add(productListDTO1);
            productListDTO.add(productListDTO1);

        } else if (mainController.getUserProductList().size() == 1) {
            productListDTO.add(mainController.getUserProductList().get(0));
        } else {
            productListDTO = showAndGetList(SelectionMode.MULTIPLE,"Choose one or more list to add");
        }
        return productListDTO;
    }

    protected ProductListDTO getSingleProductList() {
        if (mainController.getUserProductList().size() > 0) {
            if (mainController.getListTreeViewController().getSelected() != null)
                return mainController.getListTreeViewController().getSelected();
            else {
                ObservableList<ProductListDTO> productListDTOS = showAndGetList(SelectionMode.SINGLE, "Choose one or more list to add");
                return productListDTOS.size() > 0 ? productListDTOS.get(0) : null;
            }
        }
        return null;
    }

    protected ObservableList<ProductListDTO> showAndGetList(SelectionMode selectionMode,String title) {
        ListView<ProductListDTO> productListListView = new ListView<>(mainController.getUserProductList());
        productListListView.getSelectionModel().setSelectionMode(selectionMode);
        JFXAlert alert = new JFXAlert(mainController.getRoot().getScene().getWindow());

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
        JMetro jMetro = getJMetroStyled();

        jMetro.setScene(alert.getDialogPane().getScene());
        alert.showAndWait();
        return productListListView.getSelectionModel().getSelectedItems();
    }
}
