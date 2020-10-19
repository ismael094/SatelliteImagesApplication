package gui.menu;

import controller.MainController;
import controller.download.DownloadPreferencesController;
import gui.events.DownloadProductListEvent;
import gui.events.DownloadSelectedProductEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import jfxtras.styles.jmetro.JMetro;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import static utils.ThemeConfiguration.getJMetroStyled;

public class DownloadMenu extends Menu implements SatInfMenuItem {
    private final MainController mainController;

    public DownloadMenu(MainController mainController) {
        super("Downloads");
        this.mainController = mainController;
        init();
    }

    private void init() {
        MenuItem downloadList = new MenuItem("Download current list");
        downloadList.setOnAction(new DownloadProductListEvent(mainController));

        MenuItem downloadProducts = new MenuItem("Download selected products");
        downloadProducts.setOnAction(new DownloadSelectedProductEvent(mainController));

        MenuItem preferences = new MenuItem("Preferences");
        preferences.setOnAction(e->openDownloadPreferences());

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

    private void openDownloadPreferences() {
        URL location = getClass().getResource("/fxml/DownloadPreferences.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Dialog<ButtonType> dialog = new Dialog<>();
        try {
            dialog.setDialogPane(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        JMetro jMetro = getJMetroStyled();
        jMetro.setScene(dialog.getDialogPane().getScene());
        DownloadPreferencesController controller = fxmlLoader.getController();

        dialog.setOnCloseRequest(e->{
            dialog.hide();
        });
        Optional<ButtonType> buttonType = dialog.showAndWait();
        buttonType.filter(DownloadPreferencesController.APPLY::equals).ifPresent(e-> controller.applyChanges());


    }
}
