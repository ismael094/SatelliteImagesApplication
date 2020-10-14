package gui.toolbarButton;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.GlyphsDude;
import gui.components.ToolBarComponent;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.util.Duration;
import jfxtras.styles.jmetro.JMetro;
import model.list.ProductListDTO;

import java.util.ArrayList;
import java.util.List;

import static utils.ThemeConfiguration.getJMetroStyled;

public abstract class ToolbarButton extends Button implements EventHandler<ActionEvent> {
    protected ToolBarComponent toolBar;

    public ToolbarButton(ToolBarComponent toolBar) {
        super();
        this.toolBar = toolBar;
        //getStyleClass().add("toolbarIcon");
    }

    public abstract void init();

    protected void setIcon(GlyphIcons icon, String size) {
        GlyphsDude.setIcon(this,icon,size);
    }

    protected void setTooltip(String name) {
        Tooltip tooltip = new Tooltip(name);
        //tooltip.setShowDelay(new Duration(0.1));
        //tooltip.setHideDelay(new Duration(0.5));
        setTooltip(tooltip);
        Tooltip.install(this, tooltip);
    }

    protected List<ProductListDTO> getProductLists() {
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

    protected ProductListDTO getSingleProductList() {
        if (toolBar.getMainController().getUserProductList().size() > 0) {
            if (toolBar.getMainController().getListTreeViewController().getSelected() != null)
                return toolBar.getMainController().getListTreeViewController().getSelected();
            else {
                ObservableList<ProductListDTO> productListDTOS = showAndGetList(SelectionMode.SINGLE, "Choose one or more list to add");
                return productListDTOS.size() > 0 ? productListDTOS.get(0) : null;
            }
        }
        return null;
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
        JMetro jMetro = getJMetroStyled();

        jMetro.setScene(alert.getDialogPane().getScene());
        alert.showAndWait();
        return productListListView.getSelectionModel().getSelectedItems();
    }
}
