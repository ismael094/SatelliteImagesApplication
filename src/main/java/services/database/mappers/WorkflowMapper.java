package services.database.mappers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.preprocessing.workflow.GeneralWorkflowDTO;
import model.preprocessing.workflow.WorkflowDTO;
import org.bson.types.ObjectId;
import services.entities.Workflow;

import java.util.ArrayList;
import java.util.List;

public class WorkflowMapper implements DAOMapper<WorkflowDTO, Workflow> {
    @Override
    public ObservableList<WorkflowDTO> toDTO(List<Workflow> toList) {
        if (toList == null)
            return FXCollections.observableArrayList();
        ObservableList<WorkflowDTO> result = FXCollections.observableArrayList();
        toList.forEach(e->result.add(toDTO(e)));
        return result;
    }

    @Override
    public WorkflowDTO toDTO(Workflow workflow) {
        if (workflow == null)
            return null;
        GeneralWorkflowDTO generalWorkflowDTO = new GeneralWorkflowDTO(new SimpleStringProperty(workflow.getName()), new SimpleObjectProperty<>(workflow.getType()));
        generalWorkflowDTO.setOperations(workflow.getOperations());
        generalWorkflowDTO.setId(workflow.getId());
        return generalWorkflowDTO;
    }

    @Override
    public List<Workflow> toEntity(List<WorkflowDTO> workflowsDTO) {
        if (workflowsDTO == null)
            return new ArrayList<>();
        List<Workflow> result = new ArrayList<>();
        workflowsDTO.forEach(e->result.add(toEntity(e)));
        return result;
    }

    @Override
    public Workflow toEntity(WorkflowDTO workflowDTO) {
        if (workflowDTO == null)
            return null;

        Workflow workflow = new Workflow(workflowDTO.getOperations(), workflowDTO.getType(), workflowDTO.getName());

        ObjectId objectId = workflowDTO.getId();
        if (objectId == null) {
            objectId = new ObjectId();
            workflowDTO.setId(objectId);
        }
        workflow.setId(objectId);
        return workflow;
    }
}
