package controller.workflow;

import controller.cell.WorkflowListViewCellController;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import model.processing.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MyWorkflowController implements Initializable {
    @FXML
    public ListView<Workflow> workflowList;
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

    private Workflow workflow;
    private Map<WorkflowType, String> workflowControllerMap;
    private WorkflowController activeWorkflowController;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initMap();
        workflowList.setCellFactory(e->new WorkflowListViewCellController());
        //workflowList.getItems().add(new Sentinel1GRDDefaultWorkflow());
        //setWorkflow(new Sentinel1GRDDefaultWorkflow());
        addWorkflow.setOnAction(e-> {
            SentinelWorkflow aDefault = new SentinelWorkflow(new SimpleStringProperty("default"), new SimpleObjectProperty<>(WorkflowType.GRD));
            aDefault.addOperation(new Operation(Operator.APPLY_ORBIT_FILE, new HashMap<>()));
            aDefault.addOperation(new Operation(Operator.CALIBRATION, new HashMap<>()));
            aDefault.addOperation(new Operation(Operator.WRITE_AND_READ, new HashMap<>()));
            aDefault.addOperation(new Operation(Operator.TERRAIN_CORRECTION, new HashMap<>()));
            aDefault.addOperation(new Operation(Operator.WRITE, new HashMap<>()));
            workflowList.getItems().add(aDefault);

        });

        saveWorkflow.setOnAction(e->{
            Workflow workflow = activeWorkflowController.getWorkflow();
            System.out.println(workflow);
        });

        removeWorkflow.setOnAction(e->workflowList.getItems().remove(1));

        workflowList.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            loadWorkflow(workflowList.getSelectionModel().getSelectedItem());
        });
    }

    private void initMap() {
        workflowControllerMap = new HashMap<>();
        workflowControllerMap.put(WorkflowType.GRD, "/fxml/GRDWorkflowVi.fxml");
    }

    public void setWorkflows(List<Workflow> workflows) {
        workflowList.getItems().addAll(workflows);
        if (workflows.size()>0) {
            loadWorkflow(workflows.get(0));
        }
    }

    private void loadWorkflow(Workflow workflow) {
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


}
