package services.database;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.preprocessing.workflow.GeneralWorkflowDTO;
import model.preprocessing.workflow.defaultWorkflow.GRDDefaultWorkflowDTO;
import model.preprocessing.workflow.WorkflowDTO;
import model.preprocessing.workflow.WorkflowType;
import org.junit.Before;
import org.junit.Test;
import utils.database.MongoDBManager;
import utils.database.MongoDB_;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class WorkflowDBDAO_ {
    private MongoDBManager mongodb;
    private WorkflowDTO workflowDTO;
    private WorkflowDBDAO workflowDBDAO;

    @Before
    public void init() {
        mongodb = MongoDBManager.getMongoDBManager();
        if (!mongodb.isConnected()) {
            mongodb.setCredentialsAndDatabase(MongoDB_.USER,MongoDB_.PASSWORD,MongoDB_.DATABASE);
            mongodb.connect();
        }
        workflowDTO = new GRDDefaultWorkflowDTO();
        workflowDBDAO = WorkflowDBDAO.getInstance();
    }

    @Test
    public void get_collection() {
        List<WorkflowDTO> workflowDTOList = workflowDBDAO.getCollection();
        assertThat(workflowDTOList.size()).isGreaterThan(0);

    }

    @Test
    public void save_and_delete_empty_workflow() {
        GeneralWorkflowDTO generalWorkflowDTO = new GeneralWorkflowDTO(new SimpleStringProperty("NAME"), new SimpleObjectProperty<>(WorkflowType.SLC));
        workflowDBDAO.save(generalWorkflowDTO);
        List<WorkflowDTO> workflowDTOList = workflowDBDAO.find(generalWorkflowDTO);
        assertThat(workflowDTOList.size()).isEqualTo(1);
        assertThat(workflowDTOList.get(0).getName()).isEqualTo(generalWorkflowDTO.getName());
        assertThat(workflowDTOList.get(0).getId()).isEqualTo(generalWorkflowDTO.getId());
        workflowDBDAO.delete(generalWorkflowDTO);
        workflowDTOList = workflowDBDAO.find(generalWorkflowDTO);
        assertThat(workflowDTOList.size()).isEqualTo(0);
    }

    @Test
    public void save_and_delete_workflow() {
        workflowDBDAO.save(workflowDTO);
        List<WorkflowDTO> workflowDTOList = workflowDBDAO.find(workflowDTO);
        assertThat(workflowDTOList.size()).isEqualTo(1);
        assertThat(workflowDTOList.get(0).getName()).isEqualTo(workflowDTO.getName());
        assertThat(workflowDTOList.get(0).getId()).isEqualTo(workflowDTO.getId());
        workflowDBDAO.delete(workflowDTO);
        workflowDTOList = workflowDBDAO.find(workflowDTO);
        assertThat(workflowDTOList.size()).isEqualTo(0);
    }
}
