package gui.menu;

import controller.MainController;
import gui.events.AppCloseEvent;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static utils.ThemeConfiguration.setThemeMode;

public class FileMenu extends Menu implements SatInfMenuItem{
    private final MainController mainController;
    static final Logger logger = LogManager.getLogger(FileMenu.class.getName());

    public FileMenu(MainController mainController) {
        super("File");
        this.mainController = mainController;
        init();
    }

    private void init() {
        MenuItem close = new MenuItem("Exit");
        MenuItem dark = new MenuItem("Dark mode");
        dark.setOnAction(event -> {
            setThemeMode("dark");
            JMetro jMetro = new JMetro(Style.DARK);
            jMetro.setScene(mainController.getRoot().getScene());
        });
        MenuItem light = new MenuItem("Light mode");
        light.setOnAction(event -> {
            setThemeMode("light");
            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(mainController.getRoot().getScene());
        });
        close.setOnAction(new AppCloseEvent(mainController));

        close.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
        getItems().addAll(dark,light,close);
    }

    @Override
    public Menu getMenu() {
        return this;
    }

    @Override
    public String getName() {
        return this.getText();
    }
}
