package utils.gui;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import controller.interfaces.ProductListTabItem;
import controller.interfaces.TabItem;
import gui.components.TabPaneComponent;
import gui.components.ToolBarComponent;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.stage.Modality;
import jfxtras.styles.jmetro.JMetro;
import model.list.ProductListDTO;

import java.util.ArrayList;
import java.util.List;

import static utils.ThemeConfiguration.getJMetroStyled;

public class ProductListDTOUtil {

    public static ProductListDTO getCurrentList(TabPaneComponent tabPaneComponent) {
        Tab active = tabPaneComponent.getActive();
        TabItem controller = tabPaneComponent.getControllerOf(active);
        ProductListDTO list;
        if (controller instanceof ProductListTabItem) {
            return ((ProductListTabItem)controller).getProductList();
        }
        return null;
    }

    public static List<ProductListDTO> getProductLists(ToolBarComponent toolBarComponent) {
        List<ProductListDTO> productListDTO = new ArrayList<>();
        if (toolBarComponent.getMainController().getUserProductList().size() == 0) {
            ProductListDTO productListDTO1 = createDefaultList();
            toolBarComponent.getMainController().getUserProductList().add(productListDTO1);
            productListDTO.add(productListDTO1);
        } else if (toolBarComponent.getMainController().getUserProductList().size() == 1) {
            productListDTO.add(toolBarComponent.getMainController().getUserProductList().get(0));
        } else {
            productListDTO = dialogToSelectList(toolBarComponent.getMainController().getTabController(), SelectionMode.MULTIPLE,"Choose one or more list to add");
        }
        return productListDTO;
    }

    public static List<ProductListDTO> dialogToSelectList(TabPaneComponent tabPaneComponent, SelectionMode selectionMode, String title) {
        ListView<ProductListDTO> productListListView = new ListView<>(tabPaneComponent.getMainController().getUserProductList());
        productListListView.getSelectionModel().setSelectionMode(selectionMode);
        JFXAlert alert = new JFXAlert(tabPaneComponent.getScene().getWindow());
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

    public static ProductListDTO createDefaultList() {
        return new ProductListDTO(new SimpleStringProperty("Default"),
                new SimpleStringProperty("Default list"));
    }
}
