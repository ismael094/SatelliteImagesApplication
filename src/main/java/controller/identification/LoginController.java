package controller.identification;

import controller.SatelliteApplicationController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import model.user.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.database.UserDBDAO;
import utils.AlertFactory;
import utils.Encryptor;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private CheckBox remember;
    @FXML
    private Button signIn;
    @FXML
    private Hyperlink signUp;
    @FXML
    private TextField email;
    @FXML
    private PasswordField password;

    private UserDTO userDTO;
    static final Logger logger = LogManager.getLogger(SatelliteApplicationController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        userDTO = new UserDTO(new SimpleStringProperty(),new SimpleStringProperty(),new SimpleStringProperty(),new SimpleStringProperty());
        userDTO.emailProperty().bindBidirectional(email.textProperty());
        userDTO.passwordProperty().bindBidirectional(password.textProperty());
        Tooltip tooltip = new Tooltip("example@example.com");
        tooltip.setShowDelay(new Duration(300));
        email.setTooltip(tooltip);

        signIn.disableProperty().bind(
                Bindings.isEmpty(email.textProperty())
                        .and(Bindings.isEmpty(password.textProperty())));

        signIn.setOnMouseClicked(e->{
            if (dataIsValid()) {
                UserDTO dbUser = UserDBDAO.getInstance().findByEmail(userDTO);
                if (dbUser!=null && Encryptor.matchString(userDTO.getPassword(),dbUser.getPassword())) {
                    Stage window = (Stage)signUp.getScene().getWindow();
                    window.close();
                } else {
                    AlertFactory.showErrorDialog("Login","Incorrect data","Email or password incorrect");
                }
            }
        });
        setShowSignUpViewEvent();
    }

    private boolean dataIsValid() {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        return userDTO.getEmail().toUpperCase().matches(regex) && userDTO.getPassword().length() > 0;
    }

    private void setShowSignUpViewEvent() {
        signUp.setOnAction(e->{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/RegisterView.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            RegisterController controller = fxmlLoader.getController();
            JMetro jMetro = new JMetro(Style.LIGHT);
            jMetro.setScene(scene);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.show();
        });
    }

    public UserDTO getUser() {
        return userDTO;
    }
}
