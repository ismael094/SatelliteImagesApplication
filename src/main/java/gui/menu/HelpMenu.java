package gui.menu;

import gui.components.MenuComponent;
import gui.events.*;
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

public class HelpMenu extends Menu implements SatInfMenuItem {
    private final MenuComponent menuComponent;
    static final Logger logger = LogManager.getLogger(FileMenu.class.getName());
    private MenuItem save;

    public HelpMenu(MenuComponent menuComponent) {
        super("Help");
        this.menuComponent = menuComponent;
        init();
    }

    private void init() {
        MenuItem manual = new MenuItem("Download manual [ES]");
        manual.setOnAction(new DownloadManualEvent(menuComponent.getMainController()));


        MenuItem help = new MenuItem("Information");
        help.setOnAction(new ShowInformationHelpEvent(menuComponent.getMainController()));


        MenuItem about = new MenuItem("About SatInf Manager");
        about.setOnAction(new AboutApplicationEvent(menuComponent.getMainController()));

        help.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN));

        getItems().addAll(manual,help,about);
    }

    @Override
    public Menu getMenu() {
        return this;
    }

    @Override
    public String getName() {
        return "Help";
    }

    @Override
    public void update(Object args) {

    }
}
