package gui.components;

import controller.MainController;
import controller.download.DownloadPreferencesController;
import controller.interfaces.TabItem;
import controller.search.CopernicusOpenSearchController;
import gui.events.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.events.EventType;
import model.listeners.ComponentChangeListener;
import model.events.ToolbarComponentEvent;
import javafx.application.Platform;
import javafx.scene.Parent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import static utils.ThemeConfiguration.getJMetroStyled;
import static utils.ThemeConfiguration.setThemeMode;

public class MenuComponent extends MenuBar implements Component{

    private final MainController mainController;
    static final Logger logger = LogManager.getLogger(MainController.class.getName());

    public MenuComponent(MainController mainController) {
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

        close.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
        file.getItems().addAll(dark,light,close);
        Menu edit = new Menu("Edit");
        MenuItem undo = new MenuItem("Undo");
        undo.setOnAction(e->{
            Tab active = mainController.getTabController().getActive();
            TabItem controllerOf = mainController.getTabController().getControllerOf(active);
            controllerOf.undo();
        });
        undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        MenuItem redo = new MenuItem("Redo");
        redo.setOnAction(e->{
            Tab active = mainController.getTabController().getActive();
            mainController.getTabController().getControllerOf(active).redo();
        });
        redo.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        edit.getItems().addAll(undo,redo);
        Menu search = new Menu("Searchers");
        Menu lists = new Menu("List");

        MenuItem create = new MenuItem("Create new list");
        create.setOnAction(new CreateListEvent(mainController));

        MenuItem editList = new MenuItem("Edit list");
        editList.setOnAction(new EditListEvent(mainController));

        MenuItem delete = new MenuItem("Delete list");
        delete.setOnAction(new DeleteListEvent(mainController));

        MenuItem addSel = new MenuItem("Add selected products to list");
        addSel.setOnAction(new AddSelectedToListEvent(mainController));

        MenuItem addAll = new MenuItem("Add all product to list");
        addAll.setOnAction(new AddAllToListEvent(mainController));

        MenuItem deselect = new MenuItem("Delete selected products from list");
        deselect.setOnAction(new DeleteSelectedFromListEvent(mainController));

        MenuItem preferenceImg = new MenuItem("Preference image...");
        preferenceImg.setOnAction(new AddReferenceImageEvent(mainController));


        lists.getItems().addAll(create,editList, delete,addSel,addAll,deselect,new SeparatorMenuItem(),preferenceImg);

        MenuItem searcher = new MenuItem("Copernicus Open Search");
        Menu downloads = new Menu("Downloads");

        MenuItem downloadList = new MenuItem("Download current list");
        downloadList.setOnAction(new DownloadProductListEvent(mainController));

        MenuItem downloadProducts = new MenuItem("Download selected products");
        downloadProducts.setOnAction(new DownloadSelectedProductEvent(mainController));

        MenuItem preferences = new MenuItem("Preferences");

        downloads.getItems().addAll(downloadList,downloadProducts,preferences);

        Menu processing = new Menu("Processing");
        MenuItem listProcessing = new MenuItem("Process list");
        listProcessing.setOnAction(e->mainController.process());
        processing.getItems().add(listProcessing);
        preferences.setOnAction(e->openDownloadPreferences());
        searcher.setOnAction(e -> mainController.getTabController().load(new CopernicusOpenSearchController("id")));
        search.getItems().addAll(searcher);
        this.getMenus().addAll(file,edit,search,lists,downloads,processing);
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
    public MainController getMainController() {
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
