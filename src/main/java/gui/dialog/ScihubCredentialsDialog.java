package gui.dialog;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;


public class ScihubCredentialsDialog extends Dialog<Pair<String, String>> {
    public static final String SCIHUB_LOGIN = "Scihub Login";
    public static final String COPERNICUS_SCIHUB_LOGIN = "Copernicus";
    private ButtonType loginButtonType;
    private JFXTextField usernameField;
    private JFXPasswordField passwordField;

    public ScihubCredentialsDialog() {
        init();
    }

    private void init() {
        initUsernameField();
        initPasswordField();

        ImageView imageView = new ImageView(this.getClass().getResource("/img/Copernicus_logo.jpg").toString());
        imageView.setFitHeight(100d);
        imageView.setFitWidth(200d);
        this.setGraphic(imageView);
        setDialogTitle();


        loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        GridPane grid = getGrid();

        Node loginButton = this.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        this.getDialogPane().setContent(grid);
        addListenerToUsernameField();

        setPasswordAndUsernameWhenButtonIsClicked();
    }

    private void setDialogTitle() {
        this.setTitle(SCIHUB_LOGIN);
        this.setHeaderText(COPERNICUS_SCIHUB_LOGIN);
    }

    private void setPasswordAndUsernameWhenButtonIsClicked() {
        this.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(usernameField.getText(), passwordField.getText());
            }
            return null;
        });
    }

    private GridPane getGrid() {
        GridPane grid = new GridPane();
        setGridStyle(grid);
        addComponentsToGrid(grid);
        return grid;
    }

    private void addComponentsToGrid(GridPane grid) {

        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
    }

    private void setGridStyle(GridPane grid) {
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
    }


    private void initPasswordField() {
        passwordField = new JFXPasswordField();
        passwordField.setPromptText("Password");
    }


    private void initUsernameField() {
        usernameField = new JFXTextField();
        usernameField.setPromptText("Username");
    }

    private void addListenerToUsernameField() {
        Node loginButton = this.getDialogPane().lookupButton(loginButtonType);
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });
        Platform.runLater(usernameField::requestFocus);
    }
}
