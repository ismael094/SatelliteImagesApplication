package utils.gui;

import controller.UserManager;
import controller.processing.workflow.MyWorkflowController;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import model.list.ProductListDTO;
import model.processing.workflow.WorkflowDTO;
import utils.ThemeConfiguration;

import java.io.IOException;

public class WorkflowUtil {

    public static void loadMyWorkflowView(UserManager userManager, ProductListDTO productListDTO) {
        FXMLLoader fxmlLoader = new FXMLLoader(WorkflowUtil.class.getResource("/fxml/MyWorkflowsView.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        JMetro jMetro = ThemeConfiguration.getJMetroStyled();
        MyWorkflowController controller = fxmlLoader.getController();
        controller.setProductListDTO(productListDTO);
        controller.setUserManager(userManager);
        controller.loadWorkflows();
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);
        jMetro.setScene(scene);
        stage.show();
    }
}
