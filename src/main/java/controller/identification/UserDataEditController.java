package controller.identification;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import model.user.UserDTO;
import services.database.UserDBDAO;
import utils.AlertFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static utils.ThemeConfiguration.getJMetroStyled;

public class UserDataEditController implements Initializable {
    @FXML
    private TextField username;
    @FXML
    private PasswordField confirmPassword;
    @FXML
    private PasswordField password;
    @FXML
    private Button deleteAccount;
    @FXML
    private Button saveChanges;
    @FXML
    private Button cancel;
    private UserDTO user;
    private boolean userDeleted;
    private boolean userChange;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userDeleted = false;
        userChange = false;
        //When user save new password, saved it in the database
        saveChanges.setOnAction(e->{
            userChange = true;
            if (passwordValid()) {
                setUserData();
                UserDBDAO instance = UserDBDAO.getInstance();
                instance.updatePassword(user);
                getStage().hide();
            } else {
                AlertFactory.showErrorDialog("No changes made","No changes made","No changes has been made");
            }
        });
        cancel.setOnAction(e->{
            getStage().hide();
        });
        deleteAccount.setOnAction(e->confirmDeleteAccount());
    }

    private void setUserData() {
        if (password.getText().equals(confirmPassword.getText()))
            user.setPassword(password.getText());
    }

    private Stage getStage() {
        return (Stage)username.getScene().getWindow();
    }

    public void setUser(UserDTO user) {
        this.user = user;
        setData();
    }

    private void setData() {
        username.setText(user.getUsername());
    }

    private boolean passwordValid() {
        return password.getText().length() > 0 && password.getText().equals(confirmPassword.getText());
    }

    private void confirmDeleteAccount() {
        URL location = getClass().getResource("/fxml/ConfirmDeleteAccountView.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(location);
        Dialog<ButtonType> dialog = new Dialog<>();
        try {
            dialog.setDialogPane(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        JMetro jMetro = getJMetroStyled();
        jMetro.setScene(dialog.getDialogPane().getScene());

        dialog.setOnCloseRequest(e->{
            dialog.hide();
        });
        //Confirm deletion of the user by a model
        Optional<ButtonType> buttonType = dialog.showAndWait();
        buttonType.filter(ConfirmDeleteAccountController.YES::equals).ifPresent(e-> deleteUserAccount());
    }

    private void deleteUserAccount() {
        UserDBDAO instance = UserDBDAO.getInstance();
        instance.delete(user);
        getStage().hide();
        this.userDeleted = true;
    }

    public boolean isUserDeleted() {
        return userDeleted;
    }
}
