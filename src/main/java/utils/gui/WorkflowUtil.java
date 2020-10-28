package utils.gui;

import controller.MainController;
import controller.processing.workflow.MyWorkflowController;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.list.ProductListDTO;
import model.processing.workflow.WorkflowDTO;
import services.database.ProductListDBDAO;
import utils.ThemeConfiguration;

import java.io.IOException;

public class WorkflowUtil {

    public static void loadMyWorkflowView(MainController mainController, ProductListDTO productListDTO) {
        loadMyWorkflowView(mainController,productListDTO.getWorkflows(),true);
        productListDTO.getWorkflows().addListener((ListChangeListener<WorkflowDTO>) c -> {
            while (c.next()) {
                if (c.wasAdded())
                    c.getAddedSubList().forEach(l->mainController.getUser().addWorkflow(l));
            }
            ProductListDBDAO.getInstance().save(productListDTO);
        });
    }

    public static void loadMyWorkflowView(MainController mainController, ObservableList<WorkflowDTO> workflows, boolean isList) {
        FXMLLoader fxmlLoader = new FXMLLoader(WorkflowUtil.class.getResource("/fxml/MyWorkflowsView.fxml"));
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
        controller.setVisibleAssignToList(!isList);
        controller.setMainController(mainController);
        controller.setWorkflows(workflows);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);
        jMetro.setScene(scene);
        stage.show();
    }
}
