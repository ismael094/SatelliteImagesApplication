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
import jfxtras.styles.jmetro.JMetroStyleClass;
import services.download.CopernicusDownloader;
import services.download.DownloadItem;

import java.net.URL;
import java.util.ResourceBundle;

public class DownloadController implements Initializable {
    public static final String ICON_SIZE = "0.8em";
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
    private CopernicusDownloader copernicusDownloader;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabpane.getStyleClass().add(JMetroStyleClass.UNDERLINE_TAB_PANE);
    }

    public void setDownload(CopernicusDownloader copernicusDownloader) {
        this.copernicusDownloader = copernicusDownloader;

        time.textProperty().bind(Bindings.format("%.1f", copernicusDownloader.timeLeftProperty()).concat(" min left"));

        time.visibleProperty().bind(copernicusDownloader.timeLeftProperty().isEqualTo(0).not());

        initDownloadingList();

        initProperties(copernicusDownloader);

        initQueueList(copernicusDownloader);

        initButtonsIconAndTooltip();

        stop.getStyleClass().add("-icons-color: blue;");

        setButtonOnActionEvent(copernicusDownloader);
    }

    private void setButtonOnActionEvent(CopernicusDownloader copernicusDownloader) {
        stop.setOnAction(event -> cancelDownload(copernicusDownloader));
        pause.setOnAction(event -> pauseDownload(copernicusDownloader));
        resume.setOnAction(event -> resumeDownload(copernicusDownloader));
        clearQueue.setOnAction(event -> clearQueue(copernicusDownloader));
    }

    private void initButtonsIconAndTooltip() {
        setTooltip("Clear the queue",clearQueue);
        setTooltip("Resume downloads",resume);
        setTooltip("Pause downloads",pause);
        setTooltip("Cancel all downloads",stop);
        GlyphsDude.setIcon(clearQueue, FontAwesomeIcon.TRASH_ALT);
        GlyphsDude.setIcon(stop, FontAwesomeIcon.STOP, ICON_SIZE);
        GlyphsDude.setIcon(pause, FontAwesomeIcon.PAUSE,ICON_SIZE);
        GlyphsDude.setIcon(resume, FontAwesomeIcon.PLAY,ICON_SIZE);
    }

    private void initProperties(CopernicusDownloader copernicusDownloader) {
        resume.disableProperty().bind(Bindings.isEmpty(copernicusDownloader.getDownloading()));
        stop.disableProperty().bind(Bindings.isEmpty(copernicusDownloader.getDownloading()));
        pause.disableProperty().bind(Bindings.isEmpty(copernicusDownloader.getDownloading()));
    }

    private void initQueueList(CopernicusDownloader copernicusDownloader) {
        queueList.setItems(copernicusDownloader.getHistorical());
        queueList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void initDownloadingList() {
        downloadingList.setItems(copernicusDownloader.getDownloading());
        downloadingList.setCellFactory(e -> new DownloadListCell(copernicusDownloader));
        downloadingList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void setTooltip(String text, Button button) {
        Tooltip t = new Tooltip(text);
        //t.setHideDelay(new Duration(300));
        //t.setShowDelay(new Duration(300));
        t.setFont(new Font(10));
        button.setTooltip(t);
    }

    private void clearQueue(CopernicusDownloader copernicusDownloader) {
        copernicusDownloader.clearQueue();
    }

    private void resumeDownload(CopernicusDownloader copernicusDownloader) {
        copernicusDownloader.resume();
        root.getStyleClass().remove("downloadPaused");
        root.applyCss();
    }

    private void pauseDownload(CopernicusDownloader copernicusDownloader) {
        copernicusDownloader.pause();
        root.getStyleClass().add("downloadPaused");
        root.applyCss();
    }

    private void cancelDownload(CopernicusDownloader copernicusDownloader) {
        copernicusDownloader.cancel();
    }
}
