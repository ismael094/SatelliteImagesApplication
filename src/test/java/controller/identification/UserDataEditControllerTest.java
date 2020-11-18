package controller.identification;

import controller.processing.workflow.operation.CalibrationOperationController;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import model.user.UserDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.control.TextMatchers.hasText;

public class UserDataEditControllerTest extends ApplicationTest {

    UserDataEditController controller;
    UserDTO userDTO;

    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(CalibrationOperationController.class.getResource("/fxml/UserDataEditView.fxml"));
        Parent mainNode = fxmlLoader.load();
        controller = fxmlLoader.getController();
        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
        userDTO = new UserDTO(new SimpleStringProperty(),new SimpleStringProperty());
    }

    @Before
    public void init() {
        userDTO = new UserDTO(new SimpleStringProperty(),new SimpleStringProperty());
    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});

    }

    @Test
    public void set_user() {
        userDTO.setPassword("oass");
        userDTO.setUsername("Hola");
        interact(() -> {
            controller.setUser(userDTO);
        });

        verifyThat("#username", (TextField p) -> p.getText().equals("Hola"));

    }

    @Test
    public void change_password() {
        userDTO.setPassword("oass");
        userDTO.setUsername("Hola");
        interact(() -> {
            controller.setUser(userDTO);
        });
        clickOn("#password");
        write("pass");
        clickOn("#confirmPassword");
        write("pass2");
        verifyThat("#password",(PasswordField p) -> p.getText().equals("pass"));
        verifyThat("#confirmPassword",(PasswordField p) -> p.getText().equals("pass2"));
    }
}
