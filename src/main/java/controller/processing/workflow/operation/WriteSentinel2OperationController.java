package controller.processing.workflow.operation;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import model.processing.workflow.operation.Operation;
import org.controlsfx.control.ToggleSwitch;
import org.jetbrains.annotations.TestOnly;

import javax.xml.stream.XMLStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

public class WriteSentinel2OperationController implements OperationController, Initializable {
    @FXML
    private ChoiceBox<String> writeFormat;
    @FXML
    private ToggleSwitch generatePng;
    @FXML
    private TextField red;
    @FXML
    private TextField green;
    @FXML
    private TextField blue;
    @FXML
    private ChoiceBox<String> profiles;
    @FXML

    private Operation operation;
    private ObservableList<String> inputBands;

    @Override
    public Operation getOperation() {
        getParameters();
        return operation;
    }

    @Override
    public void setOperation(Operation operation) {
        this.operation = operation;
        setParameters();
    }

    private void setParameters() {
        writeFormat.setValue(String.valueOf(operation.getParameters().getOrDefault("formatName","GeoTIFF")));
        boolean generatePNG = (boolean) operation.getParameters().getOrDefault("generatePNG", false);
        generatePng.selectedProperty().set(generatePNG);

        profiles.setValue(String.valueOf(operation.getParameters().getOrDefault("profile","Some")));
        red.setText(String.valueOf(operation.getParameters().getOrDefault("red","B4")));
        green.setText(String.valueOf(operation.getParameters().getOrDefault("green","B3")));
        blue.setText(String.valueOf(operation.getParameters().getOrDefault("blue","B2")));

    }

    private void getParameters() {
        operation.getParameters().clear();
        operation.getParameters().put("formatName",writeFormat.getValue());
        if (generatePng.isSelected()) {
            operation.getParameters().put("generatePNG",true);
            operation.getParameters().put("profile",profiles.getValue());
            operation.getParameters().put("red",red.getText());
            operation.getParameters().put("green",green.getText());
            operation.getParameters().put("blue",blue.getText());
        } else {
            operation.getParameters().put("generatePNG",false);
        }
    }

    @Override
    public void setInputBands(ObservableList<String> inputBands) {
        this.inputBands = inputBands;
    }

    @Override
    public ObservableList<String> getInputBands() {
        return inputBands;
    }

    @Override
    public ObservableList<String> getOutputBands() {
        return inputBands;
    }

    @Override
    public void setNextOperationController(OperationController operationController) {

    }

    @Override
    public void updateInput() {

    }

    @Override
    public OperationController getNextOperationController() {
        return null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bindProperties();
        red.setText("B4");
        green.setText("B3");
        blue.setText("B2");
        profiles.setItems(FXCollections.observableArrayList("Some1","Some2","Some3","Some4"));
        profiles.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Some1"))
                setSome1();
            else if (newValue.equals("Some2"))
                setSome2();
        });
        profiles.setValue("Some1");
        writeFormat.setItems(FXCollections.observableArrayList("GeoTIFF","PolSARPro"));
    }

    private void setSome1() {
        red.setText("B8");
        green.setText("B7");
        blue.setText("B6");
    }

    private void setSome2() {
        red.setText("B9");
        green.setText("B7");
        blue.setText("B4");
    }

    public ToggleSwitch getGeneratePng() {
        return generatePng;
    }

    public TextField getRed() {
        return red;
    }

    public TextField getGreen() {
        return green;
    }

    public TextField getBlue() {
        return blue;
    }

    private void bindProperties() {
        profiles.disableProperty().bind(generatePng.selectedProperty().not());
        red.disableProperty().bind(generatePng.selectedProperty().not());
        green.disableProperty().bind(generatePng.selectedProperty().not());
        blue.disableProperty().bind(generatePng.selectedProperty().not());
    }

    public ChoiceBox<String> getProfile() {
        return profiles;
    }
}
