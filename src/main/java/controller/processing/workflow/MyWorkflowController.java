package controller.processing.workflow;

import controller.UserManager;
import controller.cell.WorkflowListViewCellController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import model.list.ProductListDTO;
import model.preprocessing.workflow.GeneralWorkflowDTO;
import model.preprocessing.workflow.WorkflowDTO;
import model.preprocessing.workflow.WorkflowType;
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
    private List<WorkflowController> openWorkflows;
    private ProductListDTO productList;
    private UserManager userManager;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initMap();
        productList = null;
        openWorkflows = new ArrayList<>();
        workflowList.setCellFactory(e->new WorkflowListViewCellController(this));
        //workflowList.getItems().add(new Sentinel1GRDDefaultWorkflow());
        //setWorkflow(new Sentinel1GRDDefaultWorkflow());
        addWorkflow.setText("");
        removeWorkflow.setText("");
        GlyphsDude.setIcon(addWorkflow, MaterialDesignIcon.PLUS);
        GlyphsDude.setIcon(removeWorkflow, MaterialDesignIcon.MINUS);

        onAddWorkflowCreateNewWorkflow();

        onSaveWorkflowSaveParameters();

        onActionInAssignToListAddSelectedWorkflowsToSelectedLists();

        onRemoveWorkflowDeleteWorkflow();

        onSelectInWorkflowListViewLoadWorkflowParameters();
    }

    private void onSelectInWorkflowListViewLoadWorkflowParameters() {
        workflowList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            if (oldValue != null && !oldValue.equals(newValue) && workflowList.getSelectionModel().getSelectedItem() != null)
                loadWorkflow(workflowList.getSelectionModel().getSelectedItem());
        });
    }

    private void onRemoveWorkflowDeleteWorkflow() {
        removeWorkflow.setOnAction(e->{
            if (workflowList.getSelectionModel().getSelectedItem() != null) {
                if (productList!=null)
                    productList.removeWorkflow(workflowList.getSelectionModel().getSelectedItem());
                else
                    userManager.removeWorkflow(workflowList.getSelectionModel().getSelectedItem());

                workflowList.getItems().remove(workflowList.getSelectionModel().getSelectedItem());
            }
        });
    }

    private void onAddWorkflowCreateNewWorkflow() {
        addWorkflow.setOnAction(e-> {
            GeneralWorkflowDTO aDefault = new GeneralWorkflowDTO(
                    new SimpleStringProperty("default"),
                    new SimpleObjectProperty<>(WorkflowType.GRD));

            workflowList.getItems().add(aDefault);
            userManager.addNewWorkflow(aDefault);
            if (productList!=null)
                productList.addWorkflow(aDefault);
        });
    }

    private void onSaveWorkflowSaveParameters() {
        saveWorkflow.setOnAction(e->{
            ObservableList<WorkflowDTO> workflows = FXCollections.observableArrayList();
            openWorkflows.forEach(w->{
                workflows.add(w.getWorkflow());
                if (productList != null && !productList.getWorkflows().contains(w.getWorkflow()))
                    productList.addWorkflow(w.getWorkflow());
            });
            userManager.updateUserWorkflows(workflows);
            //mainController.getUserManager().updateUserWorkflows(workflowList.getItems());
            AlertFactory.showSuccessDialog("Workflows updated", "Workflows updated","Workflows updated successfully");
        });
    }

    private void onActionInAssignToListAddSelectedWorkflowsToSelectedLists() {
        assignToList.setOnAction(e->{
            ObservableList<WorkflowDTO> workflows = workflowList.getSelectionModel().getSelectedItems();
            if (workflows.isEmpty()) {
                AlertFactory.showErrorDialog("Workflow","No workflows selected!","Select one or more workflows to add to list");
                return;
            }
            List<ProductListDTO> productListDTOS = ProductListDTOUtil.dialogToSelectList(userManager.getUser().getProductListsDTO(), accordion.getScene().getWindow(), SelectionMode.MULTIPLE, "Select the list to add Workflows");
            if (productListDTOS.isEmpty() || productListDTOS.get(0) == null) {
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
        workflowControllerMap.put(WorkflowType.SLC, "/fxml/Sentinel1SLCWorkflowView.fxml");
        workflowControllerMap.put(WorkflowType.S2MSI1C, "/fxml/Sentinel2MSILWorkflowView.fxml");
        workflowControllerMap.put(WorkflowType.S2MSI2A, "/fxml/Sentinel2MSILWorkflowView.fxml");
    }

    public void loadWorkflow(WorkflowDTO workflow) {
        accordion.getChildren().clear();
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
        openWorkflows.add(activeWorkflowController);
    }

    public void loadWorkflows() {
        if (productList == null)
            workflowList.setItems(FXCollections.observableArrayList(userManager.getUser().getWorkflows()));
        else
            workflowList.setItems(FXCollections.observableArrayList(productList.getWorkflows()));

        if (workflowList.getItems().size()>0) {
            workflowList.getSelectionModel().select(workflowList.getItems().get(0));
            loadWorkflow(workflowList.getItems().get(0));
        }
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void setVisibleAssignToList(boolean b) {
        assignToList.setVisible(b);
    }

    public void setProductListDTO(ProductListDTO productListDTO) {
        this.productList = productListDTO;
        setVisibleAssignToList(productList==null);
    }
}
