package controller;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.user.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.database.UserDBDAO;
import utils.AlertFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

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

    static final Logger logger = LogManager.getLogger(MainAppController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancel.setOnAction(e-> {
            Stage stage = (Stage) cancel.getScene().getWindow();
            stage.close();
        });

        userDTO = new UserDTO(new SimpleStringProperty(),new SimpleStringProperty(),new SimpleStringProperty(),new SimpleStringProperty());
        userDTO.emailProperty().bindBidirectional(email.textProperty());
        userDTO.passwordProperty().bindBidirectional(password.textProperty());
        userDTO.firstNameProperty().bindBidirectional(firstName.textProperty());
        userDTO.lastNameProperty().bindBidirectional(lastName.textProperty());

        register.disableProperty().bind(
                Bindings.isEmpty(email.textProperty())
                    .or(Bindings.isEmpty(password.textProperty()))
                    .or(Bindings.isEmpty(firstName.textProperty())
                    .or(Bindings.isEmpty(lastName.textProperty()))));

        register.setOnMouseClicked(e->{
            if (validateUserData()) {
                if (userNotExits()) {
                    UserDBDAO.getInstance().save(userDTO);
                    Stage window = (Stage) register.getScene().getWindow();
                    window.close();
                } else
                    AlertFactory.showErrorDialog("Error while registering user",
                            "Register",
                            "Sorry, that email is already registered in the application");
            } else
                AlertFactory.showErrorDialog("Error while registering user","Register","Email must be in yyyy@yyy.yy format");
        });
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
}
