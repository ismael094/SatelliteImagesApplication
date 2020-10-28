package controller.cell;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import services.download.DownloadItem;
import services.download.Downloader;

import java.io.IOException;

public class DownloadListCell extends ListCell<DownloadItem> {
    private final Downloader downloader;
    private FXMLLoader loader;


    @FXML
    private AnchorPane root;
    @FXML
    private Label title;
    @FXML
    private ProgressBar bar;
    @FXML
    private Button cancelDownload;

    public DownloadListCell(Downloader downloader) {
        this.downloader = downloader;
    }

    @Override
    protected void updateItem(DownloadItem item, boolean empty) {
        super.updateItem(item, empty);

        if(empty || item == null) {
            setText(null);
            setGraphic(null);

        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/fxml/DownloadListCell.fxml"));
                loader.setController(this);
                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            setDisable(false);
            prefWidthProperty().bind(getListView().prefWidthProperty().subtract(2));
            setMaxWidth(Control.USE_PREF_SIZE);

            GlyphsDude.setIcon(cancelDownload,FontAwesomeIcon.REMOVE,"0.6em");

            cancelDownload.setOnAction(e-> {
                setDisable(true);
                downloader.remove(item.getProductDTO());
            });

            title.setText(item.getProductDTO().getTitle());

            if (item.getProgressProperty() != null) {
                bar.progressProperty().bind(item.getProgressProperty());
            }

            setText(null);
            setGraphic(root);
        }

    }
}
