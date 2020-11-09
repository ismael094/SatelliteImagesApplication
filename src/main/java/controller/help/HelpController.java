package controller.help;

import com.jfoenix.controls.JFXTreeView;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelpController implements Initializable {

    @FXML
    private Hyperlink search;
    @FXML
    private Hyperlink processing;
    @FXML
    private AnchorPane accordion;
    @FXML
    private Button close;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initTree();
        accordion.getChildren().add(loadHelp("/fxml/SearchHelpView.fxml"));
        close.setOnAction(e-> close.getScene().getWindow().hide());
    }

    private void initTree() {
        search.setOnAction(event -> {
            accordion.getChildren().clear();
            accordion.getChildren().add(loadHelp("/fxml/SearchHelpView.fxml"));
        });

        processing.setOnAction(event -> {
            accordion.getChildren().clear();
            accordion.getChildren().add(loadHelp("/fxml/ProcessingHelpView.fxml"));
        });
    }

    private Parent loadHelp(String url) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
        try {
            return loader.load();
        } catch (IOException e) {
            return null;
        }
    }
}
