package gui.events;

import com.google.common.io.Files;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import controller.MainController;
import controller.interfaces.ProcessingResultsTabItem;
import controller.interfaces.TabItem;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import jfxtras.styles.jmetro.JMetro;
import model.postprocessing.ProcessingResults;

import java.io.File;
import java.io.IOException;

import static utils.ThemeConfiguration.getJMetroStyled;

public class SaveResultImagesEvent extends Event {

    public SaveResultImagesEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        Tab active = mainController.getTabComponent().getActive();
        TabItem tabItem = mainController.getTabComponent().getControllerOf(active);
        //Save results if the current tab is a ProcessingResultsTabItem
        if (tabItem instanceof ProcessingResultsTabItem) {


            File selectedFile = showFileChooser();
            if (selectedFile != null) {
                ProcessingResultsTabItem resultsTabItem = (ProcessingResultsTabItem)tabItem;
                ProcessingResults processingResults = resultsTabItem.getProcessingResults();
                Task<Boolean> task = new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {

                        long x = 0;
                        for (File f : processingResults.getFiles()) {
                            try {
                                if (isCancelled())
                                    return false;
                                Files.copy(f, new File(selectedFile.getAbsolutePath() + "\\" + f.getName()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            updateProgress(++x,processingResults.getFiles().size());
                        }
                        return true;
                    }
                };
                JFXAlert jfxAlert = waitUntilFileCopied(task);
                jfxAlert.show();
                task.setOnSucceeded(e->{
                    jfxAlert.hide();
                });
                task.setOnFailed(e->{
                    jfxAlert.hide();
                });

                new Thread(task).start();


            }

        }
    }

    private File showFileChooser() {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Select File");
        return fileChooser.showDialog(mainController.getRoot().getScene().getWindow());
    }

    public JFXAlert waitUntilFileCopied(Task<Boolean> task) {
        JFXAlert alert = new JFXAlert(mainController.getRoot().getScene().getWindow());
        alert.initModality(Modality.WINDOW_MODAL);
        alert.setOverlayClose(true);
        JFXDialogLayout layout = new JFXDialogLayout();
        layout.setHeading(new Label("Copying files..."));
        ProgressBar progressBar = new ProgressBar();
        progressBar.progressProperty().bind(task.progressProperty());
        layout.setBody(new ProgressBar());
        JFXButton closeButton = new JFXButton("Cancel");
        closeButton.getStyleClass().add("dialog-cancel");
        closeButton.setOnAction(e -> {
            task.cancel(true);
            alert.hide();
        });
        layout.setActions(closeButton);
        alert.setContent(layout);
        JMetro jMetro = getJMetroStyled();
        jMetro.setScene(alert.getDialogPane().getScene());
        return alert;
    }
}
