package gui.events;

import controller.MainController;
import controller.processing.ProcessingPreviewController;
import controller.search.CopernicusOpenSearchController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import model.list.ProductListDTO;
import utils.ThemeConfiguration;
import utils.gui.ProductListDTOUtil;

import java.io.IOException;
import java.util.List;

public class ShowPreviewViewEvent extends Event {

    public ShowPreviewViewEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        List<ProductListDTO> preview = ProductListDTOUtil.dialogToSelectList(mainController.getUserProductList(), mainController.getRoot().getScene().getWindow(), SelectionMode.SINGLE, "Preview");
        if (!preview.isEmpty())
            mainController.getTabController().load(new ProcessingPreviewController(preview.get(0),mainController.getProcessor()));
    }

    private void loadView(ProductListDTO productListDTO) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ProcessingPreviewView.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        JMetro jMetro = ThemeConfiguration.getJMetroStyled();

        ProcessingPreviewController controller = fxmlLoader.getController();
        controller.setProductList(productListDTO);
        controller.setProcessing(mainController.getProcessor());

        Stage stage = new Stage();
        stage.initOwner(mainController.getRoot().getScene().getWindow());
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        jMetro.setScene(scene);
        stage.show();
    }
}
