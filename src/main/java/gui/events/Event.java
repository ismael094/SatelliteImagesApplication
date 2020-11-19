package gui.events;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import controller.MainController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import model.list.ProductListDTO;
import model.user.UserDTO;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static utils.ThemeConfiguration.getJMetroStyled;

public abstract class Event implements EventHandler<ActionEvent> {

    protected final MainController mainController;
    protected UserDTO user;

    public Event(MainController controller) {
        this.mainController = controller;
        user = mainController.getUserManager().getUser();
    }

    protected List<ProductListDTO> getProductLists() {
        List<ProductListDTO> productListDTO = new ArrayList<>();
        if (mainController.getUserManager().getUser().getProductListsDTO().size() == 0) {
            ProductListDTO productListDTO1 = new ProductListDTO(new SimpleStringProperty("Default"),
                    new SimpleStringProperty("Default list"));
            user.getProductListsDTO().add(productListDTO1);
            productListDTO.add(productListDTO1);

        } else if (user.getProductListsDTO().size() == 1) {
            productListDTO.add(user.getProductListsDTO().get(0));
        } else {
            productListDTO = showAndGetList(SelectionMode.MULTIPLE,"Choose one or more list to add");
        }
        return productListDTO;
    }

    protected ProductListDTO getSingleProductList() {
        if (user.getProductListsDTO().size() > 0) {
            if (mainController.getListTreeViewComponent().getSelected() != null)
                return mainController.getListTreeViewComponent().getSelected();
            else {
                ObservableList<ProductListDTO> productListDTOS = showAndGetList(SelectionMode.SINGLE, "Choose one or more list to add");
                return productListDTOS.size() > 0 ? productListDTOS.get(0) : null;
            }
        }
        return null;
    }

    protected ObservableList<ProductListDTO> showAndGetList(SelectionMode selectionMode,String title) {
        ListView<ProductListDTO> productListListView = new ListView<>(user.getProductListsDTO());
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

    /**
     * Show view without controller
     * @param fxml Name of the view to load
     */
    protected void showInformationView(String fxml) {
        URL location = getClass().getResource(fxml);
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        JMetro jMetro = getJMetroStyled();
        jMetro.setScene(scene);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setOnCloseRequest(e->{
            stage.hide();
        });
        stage.showAndWait();
    }
}
