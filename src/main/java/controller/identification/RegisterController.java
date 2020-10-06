package controller.identification;

import controller.SatelliteApplicationController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
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
    public static final String ERROR_CONTEXT = "Sorry, that email is already registered in the application";
    public static final String ERROR_FORMAT = "Email must be in yyyy@yyy.yy format";
    @FXML
    private TextField lastName;
    @FXML
    private TextField firstName;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;
    @FXML
    private Button register;
    @FXML
    private Button cancel;

    private UserDTO userDTO;

    static final Logger logger = LogManager.getLogger(RegisterController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancel.setOnAction(e-> closeWindow());

        userDTO = new UserDTO(new SimpleStringProperty(),new SimpleStringProperty(),new SimpleStringProperty(),new SimpleStringProperty());
        bindProperties();

        register.disableProperty().bind(BindFieldsEmpty());

        register.setOnMouseClicked(this::register);
    }

    private void register(MouseEvent event) {
        if (validateUserData()) {
            if (userNotExits()) {
                UserDBDAO.getInstance().save(userDTO);
                logger.atInfo().log("New user {} registered successfully!",userDTO.getEmail());
                closeWindow();
            } else
                showErrorAlert(ERROR_CONTEXT);
        } else
            showErrorAlert(ERROR_FORMAT);
    }

    private void showErrorAlert(String context) {
        AlertFactory.showErrorDialog(ERROR_TITLE,ERROR_HEADER,context);
    }

    private BooleanBinding BindFieldsEmpty() {
        return Bindings.isEmpty(email.textProperty())
                .or(Bindings.isEmpty(password.textProperty()))
                .or(Bindings.isEmpty(firstName.textProperty())
                        .or(Bindings.isEmpty(lastName.textProperty())));
    }

    private void bindProperties() {
        userDTO.emailProperty().bindBidirectional(email.textProperty());
        userDTO.passwordProperty().bindBidirectional(password.textProperty());
        userDTO.firstNameProperty().bindBidirectional(firstName.textProperty());
        userDTO.lastNameProperty().bindBidirectional(lastName.textProperty());
    }

    private boolean userNotExits() {
        return UserDBDAO.getInstance().findByEmail(userDTO) == null;
    }

    private boolean validateUserData() {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        return userDTO.getEmail().toUpperCase().matches(regex)
                && userDTO.getPassword().length() > 0
                && !userDTO.getFirstName().isEmpty()
                && !userDTO.getLastName().isEmpty();
    }

    private void closeWindow() {
        ((Stage) register.getScene().getWindow()).close();
    }
}
