package controller.download;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import services.download.DownloadEnum;
import utils.DownloadConfiguration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static utils.DownloadConfiguration.*;

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

        autoDownload.setSelected(DownloadConfiguration.getAutodownload());

        openProductFileChooser.setOnAction(e->path.setText(getFileChooser("Product Folder",path.getText())));
        openListFileChooser.setOnAction(e->path.setText(getFileChooser("List Folder",pathList.getText())));
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
}
