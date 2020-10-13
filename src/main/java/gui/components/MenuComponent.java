package gui.components;

import controller.SatelliteApplicationController;
import controller.download.DownloadPreferencesController;
import controller.search.CopernicusOpenSearchController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.events.EventType;
import model.listeners.ComponentChangeListener;
import model.events.ToolbarComponentEvent;
import javafx.application.Platform;
import javafx.scene.Parent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.ThemeConfiguration;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import static utils.ThemeConfiguration.getJMetroStyled;
import static utils.ThemeConfiguration.setThemeMode;

public class MenuComponent extends MenuBar implements Component{

    private final SatelliteApplicationController mainController;
    static final Logger logger = LogManager.getLogger(SatelliteApplicationController.class.getName());

    public MenuComponent(SatelliteApplicationController mainController) {
        super();
        this.mainController = mainController;
    }

    @Override
    public void init() {
        Menu file = new Menu("File");
        MenuItem close = new MenuItem("Exit");
        MenuItem dark = new MenuItem("Dark mode");
        dark.setOnAction(event -> {
            setThemeMode("dark");
            JMetro jMetro = new JMetro(Style.DARK);
            jMetro.setScene(this.getScene());
        });
        MenuItem light = new MenuItem("Light mode");
        light.setOnAction(event -> {
            setThemeMode("light");
            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(this.getScene());
        });
        close.setOnAction(e -> {
            logger.atInfo().log("===Closing Satellite App===");
            Platform.exit();
            mainController.getDownload().cancel();
            System.exit(0);
        });
        file.getItems().addAll(dark,light,close);
        Menu edit = new Menu("Edit");
        Menu search = new Menu("Searchers");
        Menu lists = new Menu("List");
        MenuItem create = new MenuItem("Create new list");
        MenuItem addSel = new MenuItem("Add selected products to list");
        MenuItem addAll = new MenuItem("Add all product to list");
        MenuItem searcher = new MenuItem("Copernicus Open Search");
        Menu downloads = new Menu("Downloads");
        MenuItem preferences = new MenuItem("Preferences");
        downloads.getItems().add(preferences);
        preferences.setOnAction(e->openDownloadPreferences());
        lists.getItems().addAll(create,addSel,addAll);
        searcher.setOnAction(e -> mainController.getTabController().load(new CopernicusOpenSearchController("id")));
        search.getItems().addAll(searcher);
        this.getMenus().addAll(file,edit,search,lists,downloads);
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

        Optional<ButtonType> buttonType = dialog.showAndWait();
        buttonType.filter(DownloadPreferencesController.APPLY::equals).ifPresent(e-> controller.applyChanges());

    }

    @Override
    public Parent getView() {
        return this;
    }

    @Override
    public SatelliteApplicationController getMainController() {
        return mainController;
    }

    @Override
    public void addComponentListener(EventType.ComponentEventType type, ComponentChangeListener listener) {

    }

    @Override
    public void fireEvent(ToolbarComponentEvent event) {

    }

    public void initSearchView(String id) {
        ;
    }
}
