package controller.identification;

import com.jfoenix.controls.JFXSpinner;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetroStyleClass;
import model.user.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.database.UserDBDAO;
import utils.AlertFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    public static final String ERROR_TITLE = "Error while registering user";
    public static final String ERROR_HEADER = "Register";
    public static final String ERROR_CONTEXT = "Sorry, that username is already registered in the application";
    public static final String ERROR_FORMAT = "Passwords doesn't match";


    @FXML
    private JFXSpinner spinner;
    @FXML
    private AnchorPane spinnerPane;
    @FXML
    private BorderPane root;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField passwordConfirmation;
    @FXML
    private Button register;
    @FXML
    private Button cancel;

    private UserDTO userDTO;

    static final Logger logger = LogManager.getLogger(RegisterController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancel.setOnAction(e-> closeWindow());

        userDTO = new UserDTO(new SimpleStringProperty(),new SimpleStringProperty());
        bindProperties();

        root.getStyleClass().add(JMetroStyleClass.BACKGROUND);

        register.disableProperty().bind(bindFieldsIfThereAreEmpty());

        register.setOnMouseClicked(this::register);

        setSpinnerVisible(false);
    }

    private void register(MouseEvent event) {
        if (validateUserData()) {
            setSpinnerVisible(true);
            root.setDisable(true);
            Task<Boolean> task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    if (userNotExits()) {
                        UserDBDAO.getInstance().save(userDTO);
                        return true;
                    }
                    return false;
                }
            };

            task.setOnSucceeded(e->{
                setSpinnerVisible(false);
                root.setDisable(false);
                if (task.getValue()) {
                    AlertFactory.showSuccessDialog("User registered!","User registered","User registered successfully");
                    closeWindow();
                } else {
                    showErrorAlert(ERROR_CONTEXT);
                }
            });

            task.setOnFailed(e->{
                setSpinnerVisible(false);
                root.setDisable(false);
                showErrorAlert(ERROR_CONTEXT);
            });

            new Thread(task).start();

        } else
            showErrorAlert(ERROR_FORMAT);
    }

    private void showErrorAlert(String context) {
        AlertFactory.showErrorDialog(ERROR_TITLE,ERROR_HEADER,context);
    }

    private BooleanBinding bindFieldsIfThereAreEmpty() {
        return Bindings.isEmpty(password.textProperty())
                .or(Bindings.isEmpty(passwordConfirmation.textProperty()))
                .or(Bindings.isEmpty(username.textProperty()));
    }

    private void bindProperties() {
        userDTO.passwordProperty().bindBidirectional(password.textProperty());
        userDTO.usernameProperty().bindBidirectional(username.textProperty());
    }

    private boolean userNotExits() {
        return UserDBDAO.getInstance().findByUsername(userDTO) == null;
    }

    private boolean validateUserData() {
        return userDTO.getPassword().equals(passwordConfirmation.getText())
                && userDTO.getPassword().length() > 0
                && !userDTO.getUsername().isEmpty();
    }

    private void closeWindow() {
        ((Stage) register.getScene().getWindow()).close();
    }

    private void setSpinnerVisible(boolean b) {
        spinnerPane.setVisible(b);
        spinnerPane.setManaged(b);
        spinner.setVisible(b);
        spinner.setManaged(b);
    }
}
