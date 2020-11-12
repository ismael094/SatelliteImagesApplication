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

public abstract class ToolbarButton extends Button {
    protected ToolBarComponent toolBar;

    public ToolbarButton(ToolBarComponent toolBar) {
        super();
        this.toolBar = toolBar;
        //getStyleClass().add("toolbarIcon");
    }

    public abstract void init();

    public abstract void update(Object args);

    protected void setIcon(GlyphIcons icon, String size) {
        GlyphsDude.setIcon(this,icon,size);
    }

    protected void setTooltip(String name) {
        Tooltip tooltip = new Tooltip(name);
        setTooltip(tooltip);
        Tooltip.install(this, tooltip);
    }






}
