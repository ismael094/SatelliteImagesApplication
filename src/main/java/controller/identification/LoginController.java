package controller.identification;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
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

public class LoginController implements Initializable {
    @FXML
    private AnchorPane root;
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

    static final Logger logger = LogManager.getLogger(LoginController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        userDTO = new UserDTO(new SimpleStringProperty(),new SimpleStringProperty(),new SimpleStringProperty(),new SimpleStringProperty());

        bindProperties();

        root.getStyleClass().add(JMetroStyleClass.BACKGROUND);

        signIn.disableProperty().bind(bindEmailAndPasswordEmpty());

        signIn.setOnMouseClicked(this::login);

        signUp.setOnAction(this::initRegister);
    }

    private void initRegister(ActionEvent actionEvent) {
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

    private void bindProperties() {
        userDTO.emailProperty().bindBidirectional(email.textProperty());
        userDTO.passwordProperty().bindBidirectional(password.textProperty());
        Tooltip tooltip = new Tooltip("example@example.com");
        //tooltip.setShowDelay(new Duration(300));
        tooltip.setFont(new Font(10));
        email.setTooltip(tooltip);
    }

    private BooleanBinding bindEmailAndPasswordEmpty() {
        return Bindings.isEmpty(email.textProperty())
                .and(Bindings.isEmpty(password.textProperty()));
    }

    private void login(MouseEvent e) {
        if (dataIsValid()) {
            UserDTO dbUser = UserDBDAO.getInstance().findByEmail(userDTO);
            if (dbUser!=null && Encryptor.matchString(userDTO.getPassword(),dbUser.getPassword())) {
                userDTO = dbUser;
                logger.atInfo().log("Login completed! User {} successfully logged",dbUser.getFirstName());
                closeWindow();
            } else {
                AlertFactory.showErrorDialog("Login","Incorrect data","Email or password incorrect");
            }
        }
    }

    private boolean dataIsValid() {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        return userDTO.getEmail().toUpperCase().matches(regex) && userDTO.getPassword().length() > 0;
    }

    public UserDTO getUser() {
        return userDTO;
    }

    private void closeWindow() {
        ((Stage) email.getScene().getWindow()).close();
    }
}
