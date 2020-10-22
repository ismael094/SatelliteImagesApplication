package controller.workflow;

import controller.MainController;
import controller.cell.WorkflowListViewCellController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import model.list.ProductListDTO;
import model.processing.*;
import utils.AlertFactory;
import utils.gui.ProductListDTOUtil;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MyWorkflowController implements Initializable {
    @FXML
    private ListView<WorkflowDTO> workflowList;
    @FXML
    private Button assignToList;
    @FXML
    private Button addWorkflow;
    @FXML
    private Button removeWorkflow;
    @FXML
    private Button saveWorkflow;
    @FXML
    private GridPane workflowGrid;
    @FXML
    private AnchorPane accordion;

    private WorkflowDTO workflow;
    private Map<WorkflowType, String> workflowControllerMap;
    private WorkflowController activeWorkflowController;
    private MainController mainController;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initMap();
        workflowList.setCellFactory(e->new WorkflowListViewCellController());
        //workflowList.getItems().add(new Sentinel1GRDDefaultWorkflow());
        //setWorkflow(new Sentinel1GRDDefaultWorkflow());
        addWorkflow.setOnAction(e-> {
            GeneralWorkflowDTO aDefault = new GeneralWorkflowDTO(new SimpleStringProperty("default"), new SimpleObjectProperty<>(WorkflowType.GRD));
            aDefault.addOperation(new Operation(Operator.APPLY_ORBIT_FILE, new HashMap<>()));
            aDefault.addOperation(new Operation(Operator.CALIBRATION, new HashMap<>()));
            aDefault.addOperation(new Operation(Operator.WRITE_AND_READ, new HashMap<>()));
            aDefault.addOperation(new Operation(Operator.TERRAIN_CORRECTION, new HashMap<>()));
            aDefault.addOperation(new Operation(Operator.WRITE, new HashMap<>()));
            workflowList.getItems().add(aDefault);
        });

        saveWorkflow.setOnAction(e->{
            activeWorkflowController.getWorkflow();
            mainController.updateUserWorkflows();
            AlertFactory.showSuccessDialog("Workflows updated", "Workflows updated","Workflows updated successfully");
        });

        onActionInAssignToListAddSelectedWorkflowsToSelectedLists();

        removeWorkflow.setOnAction(e->{
            workflowList.getItems().remove(workflowList.getSelectionModel().getSelectedItem());
        });

        workflowList.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            loadWorkflow(workflowList.getSelectionModel().getSelectedItem());
        });
    }

    private void onActionInAssignToListAddSelectedWorkflowsToSelectedLists() {
        assignToList.setOnAction(e->{
            ObservableList<WorkflowDTO> workflows = workflowList.getSelectionModel().getSelectedItems();
            if (workflows.isEmpty()) {
                AlertFactory.showErrorDialog("Workflow","No workflows selected!","Select one or more workflows to add to list");
                return;
            }
            List<ProductListDTO> productListDTOS = ProductListDTOUtil.dialogToSelectList(mainController.getUserProductList(), mainController.getRoot().getScene().getWindow(), SelectionMode.MULTIPLE, "Select the list to add Workflows");
            if (workflows.isEmpty()) {
                AlertFactory.showErrorDialog("Workflow","No lists selected!","Select one or more lists to add workflows");
                return;
            }
            productListDTOS.forEach(p->p.addWorkflow(workflows));
            AlertFactory.showSuccessDialog("Workflows added","Workflows added", "All workflows added to selected lists!");
        });
    }

    private void initMap() {
        workflowControllerMap = new HashMap<>();
        workflowControllerMap.put(WorkflowType.GRD, "/fxml/Sentinel1GRDWorkflowView.fxml");
    }

    public void setWorkflows(ObservableList<WorkflowDTO> workflows) {
        workflowList.setItems(workflows);
        if (workflows.size()>0) {
            loadWorkflow(workflows.get(0));
        }
    }

    private void loadWorkflow(WorkflowDTO workflow) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(workflowControllerMap.get(workflow.getType())));
        Parent parent = null;
        try {
            parent = fxmlLoader.load();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        activeWorkflowController = fxmlLoader.getController();
        accordion.getChildren().add(parent);
        AnchorPane.setTopAnchor(parent,0.0);
        AnchorPane.setBottomAnchor(parent,0.0);
        AnchorPane.setLeftAnchor(parent,0.0);
        AnchorPane.setRightAnchor(parent,0.0);
        activeWorkflowController.setWorkflow(workflow);
    }


    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
