package gui.toolbarButton;

import controller.list.CreateListController;
import gui.components.ToolBarComponent;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.ProductList;

import java.io.IOException;
import java.net.URL;

public class CreateListToolbarButton extends ToolbarButton {

    public CreateListToolbarButton(ToolBarComponent toolBarComponent) {
        super(toolBarComponent);
        setId("createList");
    }

    @Override
    public void init() {
        setOnAction(this);
        Tooltip tooltip = new Tooltip("Create new list");
        tooltip.setShowDelay(new Duration(0.1));
        tooltip.setHideDelay(new Duration(0.5));
        setTooltip(tooltip);
    }


    @Override
    public void handle(ActionEvent event) {
        URL location = getClass().getResource("/fxml/CreateListView.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        new JMetro(Style.LIGHT).setScene(scene);
        CreateListController controller = fxmlLoader.getController();
        Stage stage = new Stage();
        stage.setTitle("Create list");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        ProductList productList = controller.getProductList();
        if (productList!=null)
            toolBar.getMainController().getUserProductList().add(productList);
        toolBar.getMainController().getListTreeViewController().reloadTree();

    }
}
