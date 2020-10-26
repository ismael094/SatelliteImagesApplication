package controller.processing.workflow.operation;

import com.beust.jcommander.Strings;
import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.processing.workflow.operation.Operation;
import model.processing.workflow.operation.Operator;

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
            updateBands();
        });

        polarisations.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateBands();
        });
        operation = new Operation(Operator.CALIBRATION, parameters);
        initControls();
        bind();
    }

    private void updateBands() {
        bands.clear();
        handleBands(outputGamma, GAMMA_0);
        handleBands(outputBeta, BETA_0);
        handleBands(outputSigma, SIGMA_0);
    }

    private void bind() {
        outputBeta.setOnAction(event -> handleBands(outputBeta, BETA_0));

        outputGamma.setOnAction(event -> handleBands(outputGamma, GAMMA_0));

        outputSigma.setOnAction(event -> handleBands(outputSigma, SIGMA_0));
    }

    private void handleBands(CheckBox checkbox, String name) {
        removeBand(name, bands);
        if (checkbox.isSelected())
            addBand(name, bands);

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
        ObservableList<String> items = FXCollections.observableArrayList("Intensity_VH", "Intensity_VV");
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
        if (polarisations.getSelectionModel().getSelectedItems().isEmpty())
            setPolarisationParameter(polarisations.getItems());
        else
            setPolarisationParameter(polarisations.getSelectionModel().getSelectedItems());

        if (calibrationSourceBands.getSelectionModel().getSelectedItems().isEmpty())
            setSourceBandsParameter(calibrationSourceBands.getItems());
        else
            setSourceBandsParameter(calibrationSourceBands.getSelectionModel().getSelectedItems());
    }

    private void setPolarisationParameter(ObservableList<String> polarisations) {
        operation.getParameters().put("selectedPolarisations", Strings.join(",",polarisations));
    }

    private void setSourceBandsParameter(ObservableList<String> polarisations) {
        operation.getParameters().put("sourceBands", Strings.join(",",polarisations));
    }


    @Override
    public void setOperation(Operation operation) {
        this.operation = operation;
        setParameters();
    }

    @Override
    public void setInputBands(ObservableList<String> inputBands) {
        calibrationSourceBands.getItems().clear();
        calibrationSourceBands.getItems().addAll(inputBands);
    }

    @Override
    public ObservableList<String> getInputBands() {
        return calibrationSourceBands.getItems();
    }

    @Override
    public ObservableList<String> getOutputBands() {
        if (bands.isEmpty()) {
            ObservableList<String> objects = FXCollections.observableArrayList();
            polarisations.getSelectionModel().getSelectedItems().forEach(p->{
                objects.add(SIGMA_0+p);
            });
            return objects;
        }
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
            nextOperationController.setInputBands(getOutputBands());
    }

    @Override
    public OperationController getNextOperationController() {
        return nextOperationController;
    }

    private void addBand(String name, ObservableList<String> res) {
        polarisations.getSelectionModel().getSelectedItems().forEach(b->{
            if (!res.contains(name+b))
                res.add(name+b);
        });
    }

    private void removeBand(String name, ObservableList<String> res) {
        polarisations.getSelectionModel().getSelectedItems().forEach(b->{
            res.remove(name+b);
        });
    }

    private void setParameters() {
        polarisations.getItems().clear();
        ObservableList<String> strings = FXCollections.observableArrayList(Arrays.asList(operation.getParameters().getOrDefault("selectedPolarisations", "VH,VV").toString().split(",")));
        polarisations.getItems().addAll(strings);
        strings.forEach(s-> polarisations.getSelectionModel().select(s));
        calibrationSourceBands.getItems().clear();
        ObservableList<String> sourceBands = FXCollections.observableArrayList(Arrays.asList(String.valueOf(operation.getParameters().getOrDefault("sourceBands", "Intensity_VH,Intensity_VV")).split(",")));
        calibrationSourceBands.getItems().addAll(sourceBands);
        sourceBands.forEach(s-> calibrationSourceBands.getSelectionModel().select(s));

        selectOutputBetaControl((Boolean)(operation.getParameters().getOrDefault("outputBetaBand",false)));
        selectOutputGammaControl((Boolean)(operation.getParameters().getOrDefault("outputGammaBand",false)));
        selectOutputSigmaControl((Boolean)(operation.getParameters().getOrDefault("outputSigmaBand",false)));
        selectOutputInDbControl((Boolean)(operation.getParameters().getOrDefault("outputImageScaleInDb",false)));
    }

    public void fixOutputBeta(boolean b) {
        outputBeta.setSelected(b);
        outputBeta.setDisable(b);
    }
}
