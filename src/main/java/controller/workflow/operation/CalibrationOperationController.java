package controller.workflow.operation;

import com.beust.jcommander.Strings;
import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.processing.Operation;
import model.processing.Operator;

import java.net.URL;
import java.util.*;

public class CalibrationOperationController implements Initializable, OperationController {
    public static final String BETA_0 = "Beta0_";
    public static final String GAMMA_0 = "Gamma0_";
    public static final String SIGMA_0 = "Sigma0_";
    @FXML
    private JFXCheckBox outputSigma;
    @FXML
    private JFXCheckBox outputBeta;
    @FXML
    private JFXCheckBox outputGamma;
    @FXML
    private ListView<String> calibrationSourceBands;
    @FXML
    private ListView<String> polarisations;
    @FXML
    private JFXCheckBox outputInDb;
    private Operation operation;
    private Map<String, Object> parameters;
    private ObservableList<String> bands;
    private OperationController nextOperationController;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.parameters = new HashMap<>();
        bands = FXCollections.observableArrayList();
        calibrationSourceBands.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        polarisations.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        calibrationSourceBands.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            handleBands(outputGamma, GAMMA_0);
            handleBands(outputBeta, BETA_0);
            handleBands(outputSigma, SIGMA_0);
        });

        polarisations.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            handleBands(outputGamma, GAMMA_0);
            handleBands(outputBeta, BETA_0);
            handleBands(outputSigma, SIGMA_0);
        });
        operation = new Operation(Operator.CALIBRATION, parameters);
        initControls();
        bind();
    }

    private void bind() {
        outputBeta.setOnAction(event -> handleBands(outputBeta, BETA_0));

        outputGamma.setOnAction(event -> handleBands(outputGamma, GAMMA_0));

        outputSigma.setOnAction(event -> handleBands(outputSigma, SIGMA_0));
    }

    private void handleBands(CheckBox checkbox, String name) {
        if (checkbox.isSelected())
            addBand(name, bands);
        else {
            removeBand(name, bands);
        }
        updateInput();
    }

    private void initControls() {
        initCalibrationSourceBandsControl();
        initPolarisationsControl();
    }

    private void selectOutputInDbControl(boolean select) {
        this.outputInDb.setSelected(select);
    }

    private void initPolarisationsControl() {
        ObservableList<String> items = FXCollections.observableArrayList("VV", "VH");
        polarisations.setItems(items);
    }

    private void initCalibrationSourceBandsControl() {
        ObservableList<String> items = FXCollections.observableArrayList("Intensity_VH", "Intensity_VV", "Amplitude_VV", "Amplitude_VH");
        calibrationSourceBands.setItems(items);
    }

    private void selectOutputGammaControl(boolean select) {
        this.outputGamma.setSelected(select);
        handleBands(outputGamma, GAMMA_0);
    }

    private void selectOutputBetaControl(boolean select) {
        this.outputBeta.setSelected(select);
        handleBands(outputBeta,BETA_0);
    }

    private void selectOutputSigmaControl(boolean select) {
        this.outputSigma.setSelected(select);
        handleBands(outputSigma, SIGMA_0);
    }

    @Override
    public Operation getOperation() {
        getParameters();
        return operation;
    }

    public BooleanProperty getOutputSigma() {
        return outputSigma.selectedProperty();
    }

    public BooleanProperty getOutputBeta() {
        return outputBeta.selectedProperty();
    }

    public BooleanProperty getOutputGamma() {
        return outputGamma.selectedProperty();
    }

    public ObservableList<String> getCalibrationSourceBands() {
        return calibrationSourceBands.getSelectionModel().getSelectedItems();
    }

    public ObservableList<String> getPolarisations() {
        return polarisations.getSelectionModel().getSelectedItems();
    }

    private void getParameters() {
        operation.getParameters().put("outputBetaBand",outputBeta.isSelected());
        operation.getParameters().put("outputGammaBand",outputGamma.isSelected());
        operation.getParameters().put("outputSigmaBand",outputSigma.isSelected());
        operation.getParameters().put("outputImageScaleInDb",outputInDb.isSelected());
        operation.getParameters().put("selectedPolarisations", Strings.join(",",polarisations.getSelectionModel().getSelectedItems()));
        operation.getParameters().put("sourceBands",Strings.join(",",calibrationSourceBands.getSelectionModel().getSelectedItems()));
    }

    @Override
    public void setOperation(Operation operation) {
        this.operation = operation;
        setParameters();
    }

    @Override
    public void inputBands(ObservableList<String> inputBands) {
        calibrationSourceBands.getItems().clear();
        calibrationSourceBands.getItems().addAll(inputBands);
    }

    @Override
    public ObservableList<String> getOutputBands() {
        System.out.println(bands);
        return bands;
    }

    @Override
    public void setNextOperationController(OperationController operationController) {
        this.nextOperationController = operationController;
        updateInput();
    }

    @Override
    public void updateInput() {
        if (nextOperationController!=null)
            nextOperationController.inputBands(bands);
    }

    private void addBand(String name, ObservableList<String> res) {
        calibrationSourceBands.getSelectionModel().getSelectedItems().forEach(b->{
            if (!res.contains(name+b.split("_")[1]))
                res.add(name+b.split("_")[1]);
        });
    }

    private void removeBand(String name, ObservableList<String> res) {
        calibrationSourceBands.getSelectionModel().getSelectedItems().forEach(b->{
            res.remove(name+b.split("_")[1]);
        });
    }

    private void setParameters() {
        polarisations.getItems().clear();
        ObservableList<String> strings = FXCollections.observableArrayList(Arrays.asList(operation.getParameters().getOrDefault("selectedPolarisations", "VH").toString().split(",")));
        polarisations.getItems().addAll(strings);
        strings.forEach(s-> polarisations.getSelectionModel().select(s));
        calibrationSourceBands.getItems().clear();
        ObservableList<String> sourceBands = FXCollections.observableArrayList(Arrays.asList(String.valueOf(operation.getParameters().getOrDefault("sourceBands", "Intensity_VH,Intensity_VV,Amplitude_VH,Amplitude_VV")).split(",")));
        calibrationSourceBands.getItems().addAll(sourceBands);
        sourceBands.forEach(s-> calibrationSourceBands.getSelectionModel().select(s));

        selectOutputBetaControl((Boolean)(operation.getParameters().getOrDefault("outputBetaBand",false)));
        selectOutputGammaControl((Boolean)(operation.getParameters().getOrDefault("outputGammaBand",false)));
        selectOutputSigmaControl((Boolean)(operation.getParameters().getOrDefault("outputSigmaBand",false)));
        selectOutputInDbControl((Boolean)(operation.getParameters().getOrDefault("outputImageScaleInDb",false)));
    }
}
