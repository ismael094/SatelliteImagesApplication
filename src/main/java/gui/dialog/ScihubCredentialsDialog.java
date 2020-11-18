package gui.dialog;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.sun.javafx.application.HostServicesDelegate;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Pair;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

import javax.ws.rs.core.Link;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Copernicus Account information. Get credentials
 */
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
        Hyperlink hyperlink = getHyperlink("Forgot Password?","https://scihub.copernicus.eu/dhus/#/forgot-password");
        grid.add(hyperlink, 1, 2);
        grid.add(new Label("Not registered?"), 0, 3);
        grid.add(getHyperlink("Sign Up!","https://scihub.copernicus.eu/dhus/#/self-registration"), 1, 3);
        grid.add(new Label("Note: This application uses ApiHub"), 0, 4);
        grid.add(getHyperlink("Read more","https://scihub.copernicus.eu/twiki/do/view/SciHubWebPortal/APIHubDescription"), 1, 4);

    }

    private Hyperlink getHyperlink(String label, String url) {
        Hyperlink hyperlink = new Hyperlink(label);
        hyperlink.setOnAction(e->{
            if(Desktop.isDesktopSupported())
            {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
        return hyperlink;
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
