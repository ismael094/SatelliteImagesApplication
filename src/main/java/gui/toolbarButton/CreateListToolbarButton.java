package gui.toolbarButton;

import controller.list.ListCreateAndEditController;
import de.jensd.fx.glyphs.GlyphIcons;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import gui.components.ToolBarComponent;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import model.events.EventType;
import model.events.ToolbarComponentEvent;
import gui.dialog.ListDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import model.list.ProductListDTO;
import utils.AlertFactory;

import static java.lang.System.currentTimeMillis;

public class CreateListToolbarButton extends ToolbarButton {

    public CreateListToolbarButton(ToolBarComponent toolBarComponent) {
        super(toolBarComponent);
        setId("createList");
    }

    @Override
    public void init() {
        setOnAction(this);

        setIcon(MaterialDesignIcon.FOLDER_PLUS,"1.5em");
        setTooltip("Create new list");
    }


    @Override
    public void handle(ActionEvent event) {
        ListDialog createList = new ListDialog("Create list");
        ListCreateAndEditController controller = createList.load();
        controller.setProductList(new ProductListDTO(new SimpleStringProperty(), new SimpleStringProperty()));
        createList.showAndWait();
        ProductListDTO productListDTO = controller.getProductList();
        if (productListDTO !=null) {
            toolBar.getMainController().getListTreeViewController().setDisable(true);
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    long start = currentTimeMillis();
                    toolBar.getMainController().getUserProductList().add(productListDTO);
                    System.out.println((currentTimeMillis()-start)/1000.0 + "s");
                    return null;
                }
            };
            task.setOnSucceeded(event1 -> {
                long start = currentTimeMillis();
                toolBar.fireEvent(
                        new ToolbarComponentEvent<>(this, EventType.ComponentEventType.LIST_CREATED,
                                "List "+productListDTO.getName()+" created"));

                System.out.println((currentTimeMillis()-start)/1000.0 + "s fire event");
                start = currentTimeMillis();
                toolBar.getMainController().getListTreeViewController().setDisable(false);
                System.out.println((currentTimeMillis()-start)/1000.0 + "s reload treeview");

            });
            task.setOnFailed(event1 -> {
                Platform.runLater(()-> AlertFactory.showErrorDialog("Create list","Error when creatin list","An error has occurred while creating list named" + productListDTO.getName()));
                toolBar.getMainController().getListTreeViewController().setDisable(false);
            });

            new Thread(task).start();
        }

    }
}
