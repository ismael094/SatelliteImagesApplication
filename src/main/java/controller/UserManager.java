package controller;

import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import model.list.ProductListDTO;
import model.listeners.ComponentChangeListener;
import model.preprocessing.workflow.WorkflowDTO;
import model.user.UserDTO;
import services.database.UserDBDAO;
import services.database.WorkflowDBDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controls workflows information of user
 */
public class UserManager {
    private final UserDTO user;

    public UserManager(UserDTO user) {
        this.user = user;
        init();
    }

    private void init() {
        user.getProductListsDTO().addListener((ListChangeListener<ProductListDTO>) c -> {
            while (c.next())
                if (c.wasAdded()) {
                    UserDBDAO instance = UserDBDAO.getInstance();
                    c.getAddedSubList().forEach(p-> instance.addProductList(user,p));

                } else {
                    UserDBDAO instance = UserDBDAO.getInstance();
                    c.getRemoved().forEach(p->{
                        instance.removeProductList(user,p);
                    });

                }
        });

        /*user.getWorkflows().addListener((ListChangeListener<WorkflowDTO>) c -> {
            while (c.next())
                if (c.wasAdded()) {
                    UserDBDAO instance = UserDBDAO.getInstance();
                    c.getAddedSubList().forEach(p-> instance.addNewWorkflow(user,p));
                } else {
                    UserDBDAO instance = UserDBDAO.getInstance();
                    c.getRemoved().forEach(p->{
                        instance.removeWorkflow(user,p);
                    });
                }
        });*/

        //listTreeViewComponent.reload();
    }

    public UserDTO getUser() {
        return user;
    }

    /**
     * Add workflows to user
     * @param workflowsDTO list of workflows
     */
    public void updateUserWorkflows(ObservableList<WorkflowDTO> workflowsDTO) {
        WorkflowDBDAO instance = WorkflowDBDAO.getInstance();
        workflowsDTO.forEach(instance::save);
    }

    /**
     * Add workflow to user
     * @param workflowsDTO Workflow
     */
    public void addNewWorkflow(WorkflowDTO workflowsDTO) {
        UserDBDAO instance = UserDBDAO.getInstance();
        instance.addNewWorkflow(user,workflowsDTO);
        user.addWorkflow(workflowsDTO);
    }

    /**
     * Remove workflow
     * @param workflowsDTO workflow
     */
    public void removeWorkflow(WorkflowDTO workflowsDTO) {
        UserDBDAO instance = UserDBDAO.getInstance();
        instance.removeWorkflow(user,workflowsDTO);
        user.removeWorkflow(workflowsDTO);
        WorkflowDBDAO workflowDBDAO = WorkflowDBDAO.getInstance();
        workflowDBDAO.delete(workflowsDTO);
    }


}
