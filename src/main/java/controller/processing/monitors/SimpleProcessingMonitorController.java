package controller.processing.monitors;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import model.preprocessing.ProcessorManager;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller to show the processing progress
 */
public class SimpleProcessingMonitorController implements Initializable {
    @FXML
    private AnchorPane root;
    @FXML
    private ProgressBar numProductProgress;
    @FXML
    private ProgressBar productProcess;
    @FXML
    private ProgressBar operationProgress;
    @FXML
    private Label step;
    @FXML
    private Label productOperation;
    @FXML
    private Label operation;
    @FXML
    public Button cancel;

    private ProcessorManager processManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cancel.setOnAction(e->processManager.cancel());
        GlyphsDude.setIcon(cancel, FontAwesomeIcon.CLOSE);
    }

    /**
     * Set progress monitor for the productList progress bar
     * @param doubleProperty Double property of the property
     */
    public void setProductListProgressBar(DoubleProperty doubleProperty) {
        numProductProgress.progressProperty().bind(doubleProperty);
    }

    /**
     * Set progress monitor for the product progress bar
     * @param doubleProperty Double property of the property
     */
    public void setProductProgressBar(DoubleProperty doubleProperty) {
        productProcess.progressProperty().bind(doubleProperty);
    }

    /**
     * Set progress monitor for the operation progress bar
     * @param doubleProperty Double property of the property
     */
    public void setOperationProcessingProgressBar(DoubleProperty doubleProperty) {
        operationProgress.progressProperty().bind(doubleProperty);
    }

    /**
     * Set the current operation name
     * @param operation Name of the operation
     */
    public void setOperation(String operation) {
        this.operation.setText(operation);
    }

    /**
     * Set the processor to bind properties
     * @param processor ProcessorManager of the application
     */
    public void setProcessorManager(ProcessorManager processor) {
        this.processManager = processor;
        bindProperties();
    }

    private void bindProperties() {
        setOperationProcessingProgressBar(processManager.getOperationMonitor().getProgress());
        setProductProgressBar(processManager.getProductMonitor().getProgress());
        setProductListProgressBar(processManager.getListMonitor().getProgress());
        root.visibleProperty().bind(processManager.processingProperty());
    }
}
