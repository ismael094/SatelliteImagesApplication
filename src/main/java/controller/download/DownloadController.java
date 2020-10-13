package controller.download;

import controller.cell.DownloadListCell;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import jfxtras.styles.jmetro.JMetroStyleClass;
import services.download.DownloadManager;
import services.download.DownloadItem;

import java.net.URL;
import java.util.ResourceBundle;

public class DownloadController implements Initializable {
    @FXML
    private Label time;
    @FXML
    private GridPane root;
    @FXML
    private Button pause;
    @FXML
    private Button stop;
    @FXML
    private Button resume;
    @FXML
    private Button clearQueue;
    @FXML
    private TabPane tabpane;
    @FXML
    private ListView<DownloadItem> downloadingList;
    @FXML
    private ListView<DownloadItem> queueList;
    private DownloadManager downloadManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabpane.getStyleClass().add(JMetroStyleClass.UNDERLINE_TAB_PANE);
    }

    public void setDownload(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;

        time.textProperty().bind(Bindings.format("%.1f", downloadManager.timeLeftProperty()).concat(" min left"));

        time.visibleProperty().bind(downloadManager.timeLeftProperty().isEqualTo(0).not());

        initDownloadingList();

        initProperties(downloadManager);

        initQueueList(downloadManager);

        initButtonsIconAndTooltip();

        stop.getStyleClass().add("-icons-color: blue;");

        setButtonOnActionEvent(downloadManager);
    }

    private void setButtonOnActionEvent(DownloadManager downloadManager) {
        stop.setOnAction(event -> cancelDownload(downloadManager));
        pause.setOnAction(event -> pauseDownload(downloadManager));
        resume.setOnAction(event -> resumeDownload(downloadManager));
        clearQueue.setOnAction(event -> clearQueue(downloadManager));
    }

    private void initButtonsIconAndTooltip() {
        setTooltip("Clear the queue",clearQueue);
        setTooltip("Resume downloads",resume);
        setTooltip("Pause downloads",pause);
        setTooltip("Cancel all downloads",stop);
        GlyphsDude.setIcon(clearQueue, FontAwesomeIcon.TRASH_ALT);
        GlyphsDude.setIcon(stop, FontAwesomeIcon.STOP,"0.8em");
        GlyphsDude.setIcon(pause, FontAwesomeIcon.PAUSE,"0.8em");
        GlyphsDude.setIcon(resume, FontAwesomeIcon.PLAY,"0.8em");
    }

    private void initProperties(DownloadManager downloadManager) {
        resume.disableProperty().bind(Bindings.isEmpty(downloadManager.getDownloading()));
        stop.disableProperty().bind(Bindings.isEmpty(downloadManager.getDownloading()));
        pause.disableProperty().bind(Bindings.isEmpty(downloadManager.getDownloading()));
    }

    private void initQueueList(DownloadManager downloadManager) {
        queueList.setItems(downloadManager.getHistorical());
        //queueList.setCellFactory(e -> new DownloadListCell(downloadManager));
        queueList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void initDownloadingList() {
        downloadingList.setItems(downloadManager.getDownloading());
        downloadingList.setCellFactory(e -> new DownloadListCell(downloadManager));
        downloadingList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void setTooltip(String text, Button button) {
        Tooltip t = new Tooltip(text);
        t.setHideDelay(new Duration(300));
        t.setShowDelay(new Duration(300));
        t.setFont(new Font(10));
        button.setTooltip(t);
    }

    private void clearQueue(DownloadManager downloadManager) {
        downloadManager.clearQueue();
    }

    private void resumeDownload(DownloadManager downloadManager) {
        downloadManager.resume();
        root.getStyleClass().remove("downloadPaused");
        root.applyCss();
    }

    private void pauseDownload(DownloadManager downloadManager) {
        downloadManager.pause();
        root.getStyleClass().add("downloadPaused");
        root.applyCss();
    }

    private void cancelDownload(DownloadManager downloadManager) {
        downloadManager.cancel();
    }
}
