package services.database.mappers;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.processing.GeneralWorkflowDTO;
import model.processing.WorkflowDTO;
import services.entities.Product;
import services.entities.Workflow;

import java.util.ArrayList;
import java.util.List;

public class WorkflowMapper implements DAOMapper<WorkflowDTO, Workflow> {
    @Override
    public List<WorkflowDTO> toDAO(List<Workflow> toList) {
        if (toList == null)
            return null;
        List<WorkflowDTO> result = new ArrayList<>();
        toList.forEach(e->result.add(toDAO(e)));
        return result;
    }

    @Override
    public WorkflowDTO toDAO(Workflow workflow) {
        if (workflow == null)
            return null;
        GeneralWorkflowDTO generalWorkflowDTO = new GeneralWorkflowDTO(new SimpleStringProperty(workflow.getName()), new SimpleObjectProperty<>(workflow.getType()));
        generalWorkflowDTO.setOperations(workflow.getOperations());
        return generalWorkflowDTO;
    }

    @Override
    public List<Workflow> toEntity(List<WorkflowDTO> workflowsDTO) {
        if (workflowsDTO == null)
            return null;
        List<Workflow> result = new ArrayList<>();
        workflowsDTO.forEach(e->result.add(toEntity(e)));
        return result;
    }

    @Override
    public Workflow toEntity(WorkflowDTO workflowDTO) {
        if (workflowDTO == null)
            return null;
        return new Workflow(workflowDTO.getOperations(),workflowDTO.getType(),workflowDTO.getName());
    }
}
