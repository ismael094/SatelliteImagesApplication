package controller.processing.workflow.operation;

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
    private JFXCheckBox outputInDb;

    private Map<String, Object> parameters;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.parameters = new HashMap<>();
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

    @Override
    public Map<String,Object> getParameters() {
        updateParameters();
        return parameters;
    }

    private void updateParameters() {
        parameters.put("outputBetaBand",outputBeta.isSelected());
        parameters.put("outputGammaBand",outputGamma.isSelected());
        parameters.put("outputSigmaBand",outputSigma.isSelected());
        parameters.put("outputImageScaleInDb",outputInDb.isSelected());
    }

    @Override
    public void setParameters(Map<String,Object> parameters) {
        this.parameters = parameters;
        outputBeta.setSelected((Boolean)parameters.getOrDefault("outputBetaBand",false));
        outputGamma.setSelected((Boolean)(parameters.getOrDefault("outputGammaBand",false)));
        outputSigma.setSelected((Boolean)(parameters.getOrDefault("outputSigmaBand",false)));
        outputInDb.setSelected((Boolean)(parameters.getOrDefault("outputImageScaleInDb",false)));
    }

    public void fixOutputBeta(boolean b) {
        outputBeta.setDisable(false);
        //outputBeta.setDisable(b);
    }
}
