package services.database;

import model.processing.WorkflowDTO;
import services.database.mappers.WorkflowMapper;
import services.entities.ProductList;
import services.entities.User;
import services.entities.Workflow;
import utils.Encryptor;
import utils.database.MongoDBManager;

import java.util.List;

public class WorkflowDBDAO implements DAO<WorkflowDTO> {
    private static WorkflowDBDAO instance;
    private final MongoDBManager database;
    private final WorkflowMapper mapper;

    private WorkflowDBDAO() {
        database = MongoDBManager.getMongoDBManager();
        mapper = new WorkflowMapper();
    }

    public static WorkflowDBDAO getInstance() {
        if (instance == null) {
            instance = new WorkflowDBDAO();
        }
        return instance;
    }
    @Override
    public List<WorkflowDTO> getCollection() {
        return mapper.toDAO(database.getDatastore()
                .find(Workflow.class)
                .asList());
    }

    @Override
    public List<WorkflowDTO> find(WorkflowDTO dao) {
        return mapper.toDAO(database.getDatastore()
                .find(Workflow.class)
                .field("id")
                .equal(dao.getId())
                .asList());
    }

    @Override
    public WorkflowDTO findFirst(WorkflowDTO dao) {
        return mapper.toDAO(database.getDatastore()
                .find(Workflow.class)
                .field("id")
                .equal(dao.getId())
                .first());
    }

    @Override
    public void save(WorkflowDTO dao) {
        database.getDatastore().save(mapper.toEntity(dao));
    }

    @Override
    public void delete(WorkflowDTO dao) {
        database.getDatastore().delete(mapper.toEntity(dao));
    }

    @Override
    public void delete(List<WorkflowDTO> dao) {
        dao.forEach(this::delete);
    }
}
