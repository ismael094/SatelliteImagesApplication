package controller.identification;

import com.jfoenix.controls.JFXSpinner;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jfxtras.styles.jmetro.Style;
import model.user.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.database.UserDBDAO;
import utils.AlertFactory;
import utils.Encryptor;
import utils.ThemeConfiguration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller to login users in the application
 */
public class LoginController implements Initializable {
    @FXML
    private BorderPane loginPane;
    @FXML
    private AnchorPane spinnerPane;
    @FXML
    private JFXSpinner spinner;
    @FXML
    private AnchorPane root;
    @FXML
    private CheckBox remember;
    @FXML
    private Button signIn;
    @FXML
    private Hyperlink signUp;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    private UserDTO userDTO;

    static final Logger logger = LogManager.getLogger(LoginController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        userDTO = new UserDTO(new SimpleStringProperty(),new SimpleStringProperty());

        bindProperties();

        root.getStyleClass().add(JMetroStyleClass.BACKGROUND);

        signIn.disableProperty().bind(bindEmailAndPasswordEmpty());

        signIn.setOnMouseClicked(this::login);

        signUp.setOnAction(this::showRegisterView);

        setSpinnerVisible(false);
    }

    private void setSpinnerVisible(boolean b) {
        spinnerPane.setVisible(b);
        spinnerPane.setManaged(b);
        spinner.setVisible(b);
        spinner.setManaged(b);
    }

    private void showRegisterView(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/RegisterView.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        JMetro jMetro;
        if (ThemeConfiguration.getThemeMode().equals("light"))
            jMetro = new JMetro(Style.LIGHT);
        else
            jMetro = new JMetro(Style.DARK);

        Stage stage = new Stage();
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        jMetro.setScene(scene);
        stage.show();
    }

    //Bind components to user model
    private void bindProperties() {
        userDTO.passwordProperty().bindBidirectional(password.textProperty());
        userDTO.usernameProperty().bindBidirectional(username.textProperty());
        Tooltip tooltip = new Tooltip("pepe98");
        //tooltip.setShowDelay(new Duration(300));
        tooltip.setFont(new Font(10));
        username.setTooltip(tooltip);
    }

    private BooleanBinding bindEmailAndPasswordEmpty() {
        return Bindings.isEmpty(username.textProperty())
                .and(Bindings.isEmpty(password.textProperty()));
    }

    private void login(MouseEvent e) {
        if (dataIsValid()) {
            loginTask();
        } else {
            AlertFactory.showErrorDialog("Login","Incorrect data","Write a valid username and password");
        }
    }

    private void loginTask() {
        toggleSpinner(true);
        //Create task and check if user exists
        Task<UserDTO> task = new Task<UserDTO>() {
            @Override
            protected UserDTO call() throws Exception {
                UserDTO dbUser = UserDBDAO.getInstance().findByUsername(userDTO);
                if (dbUser != null && Encryptor.matchString(userDTO.getPassword(), dbUser.getPassword())) {
                    return dbUser;
                }
                return null;
            }
        };
        //If exists, get user data and close windows
        task.setOnSucceeded(event->{
            if (task.getValue() != null) {
                userDTO = task.getValue();
                logger.atInfo().log("Login completed! User {} successfully logged",userDTO.getUsername());
                closeWindow();
            } else {
                AlertFactory.showErrorDialog("Login","Incorrect data","Email or password incorrect");
            }

            toggleSpinner(false);
        });
        //If an error occurred, show error message
        task.setOnFailed(event -> {
            AlertFactory.showErrorDialog("Login","A problem has occurred","An error has occurred during the login");
            toggleSpinner(false);
        });
        new Thread(task).start();
    }

    private void toggleSpinner(boolean b) {
        setSpinnerVisible(b);
        loginPane.setDisable(b);
    }

    private boolean dataIsValid() {
        return !userDTO.getUsername().isEmpty() && userDTO.getPassword().length() > 0;
    }

    public UserDTO getUser() {
        return userDTO;
    }

    private void closeWindow() {
        ((Stage) username.getScene().getWindow()).close();
    }
}
