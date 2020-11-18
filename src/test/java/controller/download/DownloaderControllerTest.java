package controller.download;

import controller.postprocessing.ProductListProcessingResultItemController;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import model.SentinelData;
import org.junit.After;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import services.download.ProductDownloader;
import services.download.DownloadItem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class DownloaderControllerTest extends ApplicationTest {
    DownloadController controller;
    private ProductDownloader downloader;

    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ProductListProcessingResultItemController.class.getResource("/fxml/DownloadView.fxml"));
        downloader = mock(ProductDownloader.class);
        initMock();
        Parent mainNode = fxmlLoader.load();
        controller = fxmlLoader.getController();
        controller.setDownload(downloader);
        stage.setScene(new Scene(mainNode));
        stage.show();
        stage.toFront();
    }

    private void initMock() {
        when(downloader.getHistorical()).thenReturn(FXCollections.observableArrayList(new DownloadItem(SentinelData.getSentinel1Product())));
        when(downloader.getDownloading()).thenReturn(FXCollections.observableArrayList(new DownloadItem(SentinelData.getSentinel1Product())));
        when(downloader.getQueueSize()).thenReturn(1);
        when(downloader.timeLeftProperty()).thenReturn(new SimpleDoubleProperty(0.5));
    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void check_list_view() {
        ListView<DownloadItem> downloadingList = lookup("#downloadingList").query();
        assertThat(downloadingList.getItems().size()).isEqualTo(1);
        ListView<DownloadItem> queueList = lookup("#queueList").query();
        assertThat(queueList.getItems().size()).isEqualTo(1);
    }
}
