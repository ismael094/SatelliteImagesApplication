package controller.cell;

import com.google.common.collect.Lists;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.processing.Workflow;
import model.processing.WorkflowType;
import org.jfree.fx.FXHints;
import services.download.DownloadItem;
import services.download.Downloader;

import java.io.IOException;
import java.util.Arrays;

public class WorkflowListViewCellController extends ListCell<Workflow>  {
    private final BooleanProperty startEdit;
    private FXMLLoader loader;

    @FXML
    private AnchorPane root;
    @FXML
    private Label type;
    @FXML
    private Label name;
    @FXML
    private ChoiceBox<WorkflowType> typeBox;
    @FXML
    private TextField nameField;
    private Workflow workflow;

    public WorkflowListViewCellController() {
        startEdit = new SimpleBooleanProperty(false);
    }

    @Override
    protected void updateItem(Workflow item, boolean empty) {
        super.updateItem(item, empty);
        this.workflow = item;
        if(empty || item == null) {
            setText(null);
            setGraphic(null);

        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/fxml/WorkflowListViewCell.fxml"));
                loader.setController(this);
                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            name.textProperty().bind(workflow.nameProperty());
            type.textProperty().bind(workflow.typeProperty().asString());

            nameField.visibleProperty().bind(startEdit);
            typeBox.visibleProperty().bind(startEdit);

            prefWidthProperty().bind(getListView().widthProperty().subtract(2));
            setMaxWidth(Control.USE_PREF_SIZE);


            setText(null);
            setGraphic(root);

            setOnMouseClicked(event -> {
                System.out.println("Hoe");
                if (event.getClickCount() > 1)
                    startEdit();
            });

            nameField.addEventHandler(KeyEvent.KEY_RELEASED, this::handleEvent);

            typeBox.addEventHandler(KeyEvent.KEY_RELEASED, this::handleEvent);
        }

    }

    private void handleEvent(KeyEvent event) {
        if (!startEdit.get()) {
            System.out.println("No editing");
            return;
        }
        if (event.getCode() == KeyCode.ESCAPE) {
            cancelEdit();
        } else if (event.getCode() == KeyCode.ENTER) {
            workflow.setName(nameField.getText());
            workflow.setType(typeBox.getValue());

            cancelEdit();
        }
    }

    @Override
    public void startEdit() {
        nameField.setText(workflow.getName());
        typeBox.setItems(FXCollections.observableArrayList(Arrays.asList(WorkflowType.values())));
        typeBox.setValue(workflow.getType());
        startEdit.set(true);
    }

    @Override
    public void cancelEdit() {
        System.out.println("CANCEL");
        startEdit.set(false);
    }
}
