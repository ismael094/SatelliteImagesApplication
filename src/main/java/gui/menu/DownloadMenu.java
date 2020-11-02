package gui.menu;

import controller.download.DownloadPreferencesController;
import controller.interfaces.ProductListTabItem;
import gui.components.MenuComponent;
import gui.components.TabPaneComponent;
import gui.events.DownloadProductListEvent;
import gui.events.DownloadSelectedProductEvent;
import gui.events.OpenDownloadPreferences;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import jfxtras.styles.jmetro.JMetro;
import model.list.ProductListDTO;
import utils.gui.Observer;
import utils.gui.ProductListDTOUtil;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import static utils.ThemeConfiguration.getJMetroStyled;

public class DownloadMenu extends Menu implements SatInfMenuItem {
    private final MenuComponent menuComponent;
    private MenuItem downloadList;
    private MenuItem downloadProducts;
    private MenuItem preferences;

    public DownloadMenu(MenuComponent menuComponent) {
        super("Downloads");
        this.menuComponent = menuComponent;
        init();
    }

    private void init() {
        downloadList = new MenuItem("Download current list");
        downloadList.setOnAction(new DownloadProductListEvent(menuComponent.getMainController()));

        downloadProducts = new MenuItem("Download selected products");
        downloadProducts.setOnAction(new DownloadSelectedProductEvent(menuComponent.getMainController()));

        preferences = new MenuItem("Preferences");
        preferences.setOnAction(new OpenDownloadPreferences(menuComponent.getMainController()));

        getItems().addAll(downloadList,downloadProducts,preferences);
    }

    @Override
    public Menu getMenu() {
        return this;
    }

    @Override
    public String getName() {
        return this.getText();
    }

    @Override
    public void update(Object args) {
        Platform.runLater(()->{
            downloadList.setDisable(!(args instanceof ProductListTabItem));
            downloadProducts.setDisable(!(args instanceof ProductListTabItem));
        });
    }
}
