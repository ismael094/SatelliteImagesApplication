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
import services.download.ProductDownloader;
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
    private ProductDownloader productDownloader;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabpane.getStyleClass().add(JMetroStyleClass.UNDERLINE_TAB_PANE);
    }

    public void setDownload(ProductDownloader productDownloader) {
        this.productDownloader = productDownloader;

        time.textProperty().bind(Bindings.format("%.1f", productDownloader.timeLeftProperty()).concat(" min left"));

        time.visibleProperty().bind(productDownloader.timeLeftProperty().isEqualTo(0).not());

        initDownloadingList();

        initProperties(productDownloader);

        initQueueList(productDownloader);

        initButtonsIconAndTooltip();

        stop.getStyleClass().add("-icons-color: blue;");

        setButtonOnActionEvent(productDownloader);
    }

    private void setButtonOnActionEvent(ProductDownloader productDownloader) {
        stop.setOnAction(event -> cancelDownload(productDownloader));
        pause.setOnAction(event -> pauseDownload(productDownloader));
        resume.setOnAction(event -> resumeDownload(productDownloader));
        clearQueue.setOnAction(event -> clearQueue(productDownloader));
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

    private void initProperties(ProductDownloader productDownloader) {
        resume.disableProperty().bind(Bindings.isEmpty(productDownloader.getDownloading()));
        stop.disableProperty().bind(Bindings.isEmpty(productDownloader.getDownloading()));
        pause.disableProperty().bind(Bindings.isEmpty(productDownloader.getDownloading()));
    }

    private void initQueueList(ProductDownloader productDownloader) {
        queueList.setItems(productDownloader.getHistorical());
        queueList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void initDownloadingList() {
        downloadingList.setItems(productDownloader.getDownloading());
        downloadingList.setCellFactory(e -> new DownloadListCell(productDownloader));
        downloadingList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    public void setTooltip(String text, Button button) {
        Tooltip t = new Tooltip(text);
        t.setFont(new Font(10));
        Tooltip.install(button,t);
    }

    private void clearQueue(ProductDownloader productDownloader) {
        productDownloader.clearQueue();
    }

    private void resumeDownload(ProductDownloader productDownloader) {
        productDownloader.resume();
        root.getStyleClass().remove("downloadPaused");
        root.applyCss();
        downloadingList.setDisable(false);
    }

    private void pauseDownload(ProductDownloader productDownloader) {
        productDownloader.pause();
        downloadingList.setDisable(true);
        pause.setDisable(true);
        root.getStyleClass().add("downloadPaused");
        root.applyCss();
    }

    private void cancelDownload(ProductDownloader productDownloader) {
        productDownloader.cancel();
        downloadingList.setDisable(false);
    }
}
