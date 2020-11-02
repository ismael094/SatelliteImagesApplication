package gui.menu;

import controller.identification.UserDataEditController;
import gui.components.MenuComponent;
import gui.events.AppCloseEvent;
import gui.events.OpenFileChooserDialogEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;

import static utils.ThemeConfiguration.getJMetroStyled;
import static utils.ThemeConfiguration.setThemeMode;

public class FileMenu extends Menu implements SatInfMenuItem{
    private final MenuComponent menuComponent;
    static final Logger logger = LogManager.getLogger(FileMenu.class.getName());

    public FileMenu(MenuComponent menuComponent) {
        super("File");
        this.menuComponent = menuComponent;
        init();
    }

    private void init() {
        MenuItem loadAlgorithm = new MenuItem("Load algorithm");
        loadAlgorithm.setOnAction(new OpenFileChooserDialogEvent(menuComponent.getMainController()));
        MenuItem close = new MenuItem("Exit");
        Menu theme = new Menu("Themes");
        MenuItem dark = new MenuItem("Dark mode");
        dark.setOnAction(event -> {
            setThemeMode("dark");
            JMetro jMetro = new JMetro(Style.DARK);
            jMetro.setScene(menuComponent.getMainController().getRoot().getScene());
        });
        MenuItem light = new MenuItem("Light mode");
        light.setOnAction(event -> {
            setThemeMode("light");
            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(menuComponent.getMainController().getRoot().getScene());
        });
        theme.getItems().addAll(dark,light);
        MenuItem userData = new MenuItem("Edit user data");
        userData.setOnAction(event -> {
            loadUserDataEdit();
        });
        close.setOnAction(new AppCloseEvent(menuComponent.getMainController()));

        close.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));

        getItems().addAll(loadAlgorithm,theme,userData,close);
    }

    private void loadUserDataEdit() {
        URL location = getClass().getResource("/fxml/UserDataEditView.fxml");
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
        UserDataEditController controller = fxmlLoader.getController();
        controller.setUser(menuComponent.getMainController().getUserManager().getUser());
        stage.showAndWait();
        if (controller.isUserDeleted()) {
            new AppCloseEvent(menuComponent.getMainController()).handle(null);
        }
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

    }
}
