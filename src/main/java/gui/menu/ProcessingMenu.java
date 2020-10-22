package gui.menu;

import controller.MainController;
import controller.workflow.MyWorkflowController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.processing.Sentinel1GRDDefaultWorkflowDTO;
import model.processing.WorkflowDTO;
import utils.ThemeConfiguration;
import utils.gui.WorkflowUtil;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ProcessingMenu extends Menu implements SatInfMenuItem{
    private final MainController mainController;

    public ProcessingMenu(MainController mainController) {
        super("Processing");
        this.mainController = mainController;
        init();
    }

    private void init() {
        MenuItem listProcessing = new MenuItem("Process list");
        MenuItem myWorkflows = new MenuItem("My Workflows");
        Menu sentinel = new Menu("Sentinel");
        Menu sentinel1 = new Menu("Sentinel 1");
        MenuItem grd = new MenuItem("GRD Workflow");
        MenuItem slc = new MenuItem("SLC Workflow");

        sentinel1.getItems().addAll(grd,slc);

        Menu sentinel2 = new Menu("Sentinel 2");
        MenuItem c1 = new MenuItem("1C Workflow");
        MenuItem a2 = new MenuItem("2A Workflow");
        sentinel2.getItems().addAll(c1,a2);

        sentinel.getItems().addAll(sentinel1,sentinel2);

        myWorkflows.setOnAction(e-> {
            ObservableList<WorkflowDTO> objects = FXCollections.observableArrayList();
            objects.add(new Sentinel1GRDDefaultWorkflowDTO());
            WorkflowUtil.loadMyWorkflowView(mainController,objects);
        });
        listProcessing.setOnAction(e->mainController.process());
        getItems().addAll(myWorkflows,listProcessing,sentinel);
    }

    private void loadView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MyWorkflowsView.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        JMetro jMetro;
        if (ThemeConfiguration.getThemeMode().equals("light"))
            jMetro = new JMetro(Style.LIGHT);
        else
            jMetro = new JMetro(Style.DARK);

        MyWorkflowController controller = fxmlLoader.getController();
        List<WorkflowDTO> objects = new LinkedList<>();
        objects.add(new Sentinel1GRDDefaultWorkflowDTO());
        controller.setMainController(mainController);
        controller.setWorkflows(objects);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);
        jMetro.setScene(scene);
        stage.show();
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
