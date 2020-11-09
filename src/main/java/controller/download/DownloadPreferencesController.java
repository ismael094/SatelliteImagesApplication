package controller.download;

import controller.identification.ConfirmDeleteAccountController;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import jfxtras.styles.jmetro.JMetro;
import org.apache.commons.io.FileUtils;
import services.download.DownloadEnum;
import utils.AlertFactory;
import utils.DownloadConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.ResourceBundle;

import static utils.DownloadConfiguration.*;
import static utils.ThemeConfiguration.getJMetroStyled;

public class DownloadPreferencesController implements Initializable {
    @FXML
    private CheckBox autoDownload;
    @FXML
    private TextField path;
    @FXML
    private Button openProductFileChooser;
    @FXML
    private TextField pathList;
    @FXML
    private Button openListFileChooser;
    @FXML
    private RadioButton singleDownload;
    @FXML
    private RadioButton multipleDownload;
    @FXML
    public Button delete;

    public static final ButtonType APPLY = new ButtonType("Apply");
    public static final ButtonType CANCEL_CLOSE = new ButtonType("Cancel");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup group = new ToggleGroup();

        if (getDownloadModeLocation() == DownloadEnum.DownloadMode.MULTIPLE)
            multipleDownload.setSelected(true);
        else
            singleDownload.setSelected(true);

        singleDownload.setToggleGroup(group);
        multipleDownload.setToggleGroup(group);
        path.setText(getProductDownloadFolderLocation());
        pathList.setText(getListDownloadFolderLocation());
        path.setDisable(true);
        pathList.setDisable(true);

        autoDownload.setSelected(DownloadConfiguration.getAutodownload());

        openProductFileChooser.setOnAction(e->path.setText(getFileChooser("Product Folder",path.getText())));
        openListFileChooser.setOnAction(e->pathList.setText(getFileChooser("List Folder",pathList.getText())));

        delete.setOnAction(e->confirmDeleteProducts());
    }

    private String getFileChooser(String title, String defaultFolder) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(title);
        chooser.setInitialDirectory(new File(defaultFolder));
        File file = chooser.showDialog(path.getParent().getScene().getWindow());
        return file != null ? file.getAbsolutePath() : defaultFolder;
    }

    public void applyChanges() {
        setListDownloadFolderLocation(pathList.getText());
        setProductDownloadFolderLocation(path.getText());
        if (singleDownload.isSelected())
            setDownloadMode("single");
        else
            setDownloadMode("multiple");

        if (autoDownload.isSelected())
            DownloadConfiguration.setAutodownload("true");
        else
            DownloadConfiguration.setAutodownload("false");
    }

    private void confirmDeleteProducts() {
        URL location = getClass().getResource("/fxml/ConfirmDeleteProductsView.fxml");
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
        Optional<ButtonType> buttonType = dialog.showAndWait();
        buttonType.filter(ConfirmDeleteAccountController.YES::equals).ifPresent(e-> deleteProducts());
    }

    private void deleteProducts() {
        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                FileUtils.cleanDirectory(new File(getProductDownloadFolderLocation()));
                return true;
            }
        };

        task.setOnSucceeded(e-> AlertFactory.showSuccessDialog("Products deleted","Products","Products deleted!"));

        task.setOnFailed(e->AlertFactory.showErrorDialog("Error","Error","Error while deleting products"));

        new Thread(task).start();
    }
}
