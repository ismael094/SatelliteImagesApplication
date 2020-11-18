package gui.dialog;

import controller.list.ListCreateAndEditController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import java.io.IOException;
import java.net.URL;

public class ListDialog {
    private final String title;
    private Stage stage;

    public ListDialog(String title) {
        this.title = title;
    }

    /**
     * Load CreateListController
     * @return ListCreateAndEditController
     */
    public ListCreateAndEditController load() {
        URL location = getClass().getResource("/fxml/CreateListView.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        new JMetro(Style.LIGHT).setScene(scene);
        ListCreateAndEditController controller = fxmlLoader.getController();
        stage = new Stage();
        stage.setTitle(title);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        return controller;
    }

    public void showAndWait() {
        stage.showAndWait();
    }
}
