package controller.workflow.operation;

import com.jfoenix.controls.JFXCheckBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import model.processing.Operation;
import model.processing.Operator;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class CalibrationOperationController implements Initializable, OperationController {
    @FXML
    private JFXCheckBox outputSigma;
    @FXML
    private JFXCheckBox outputBeta;
    @FXML
    private JFXCheckBox outputGamma;
    @FXML
    private ChoiceBox<String> calibrationSourceBands;
    @FXML
    private ChoiceBox<String> polarisations;
    @FXML
    private JFXCheckBox outputInDb;
    private Operation operation;
    private Map<String, Object> parameters;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.parameters = new HashMap<>();
        operation = new Operation(Operator.CALIBRATION, parameters);
        initControls();
    }

    private void initControls() {
        initCalibrationSourceBandsControl();
        initPolarisationsControl();
    }

    private void selectOutputInDbControl(boolean select) {
        this.outputInDb.setSelected(select);
    }

    private void initPolarisationsControl() {
        ObservableList<String> items = FXCollections.observableArrayList("VV", "VH", "VV,VH");
        polarisations.setItems(items);
        if (polarisations.getValue() == null)
            polarisations.setValue("VV");
    }

    private void initCalibrationSourceBandsControl() {
        ObservableList<String> items = FXCollections.observableArrayList("Intensity_VH", "Intensity_VV");
        calibrationSourceBands.setItems(items);
        if (calibrationSourceBands.getValue() == null)
            calibrationSourceBands.setValue("Intensity_VH");
    }

    private void selectOutputGammaControl(boolean select) {
        this.outputGamma.setSelected(select);
    }

    private void selectOutputBetaControl(boolean select) {
        this.outputBeta.setSelected(select);
    }

    private void selectOutputSigmaControl(boolean select) {
        this.outputSigma.setSelected(select);
    }

    @Override
    public Operation getOperation() {
        getParameters();
        return operation;
    }

    private void getParameters() {
        operation.getParameters().put("outputBetaBand",outputBeta.isSelected());
        operation.getParameters().put("outputGammaBand",outputGamma.isSelected());
        operation.getParameters().put("outputSigmaBand",outputSigma.isSelected());
        operation.getParameters().put("outputImageScaleInDb",outputInDb.isSelected());
        operation.getParameters().put("selectedPolarisations",polarisations.getValue());
        operation.getParameters().put("sourceBands",calibrationSourceBands.getValue());
    }

    @Override
    public void setOperation(Operation operation) {
        this.operation = operation;
        setParameters();
    }

    private void setParameters() {
        selectOutputBetaControl((Boolean)(operation.getParameters().getOrDefault("outputBetaBand",false)));
        selectOutputGammaControl((Boolean)(operation.getParameters().getOrDefault("outputGammaBand",false)));
        selectOutputSigmaControl((Boolean)(operation.getParameters().getOrDefault("outputSigmaBand",false)));
        selectOutputInDbControl((Boolean)(operation.getParameters().getOrDefault("outputImageScaleInDb",false)));
        polarisations.setValue(String.valueOf(operation.getParameters().getOrDefault("selectedPolarisations","VH")));
        calibrationSourceBands.setValue(String.valueOf(operation.getParameters().getOrDefault("sourceBands","Intensity_VH")));
    }
}
