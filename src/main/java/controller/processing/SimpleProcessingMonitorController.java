package controller.processing;

import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.util.ResourceBundle;

public class SimpleProcessingMonitorController implements Initializable {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setProductListProgressBar(DoubleProperty doubleProperty) {
        numProductProgress.progressProperty().bind(doubleProperty);
    }

    public void setProductProgressBar(DoubleProperty doubleProperty) {
        productProcess.progressProperty().bind(doubleProperty);
    }

    public void setOperationProcessingProgressBar(DoubleProperty doubleProperty) {
        operationProgress.progressProperty().bind(doubleProperty);
    }

    public void setProductListText(String text) {
        this.step.setText(text);
    }

    public void setProductOperation(String text) {
        this.productOperation.setText(text);
    }

    public void setOperation(String operation) {
        this.operation.setText(operation);
    }

}
