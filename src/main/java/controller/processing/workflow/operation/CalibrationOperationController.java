package controller.processing.workflow.operation;

import com.jfoenix.controls.JFXCheckBox;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.*;

/**
 * Controller to manager calibration parameters
 */
public class CalibrationOperationController implements Initializable, OperationController {
    public static final String BETA_0 = "Beta0_";
    public static final String GAMMA_0 = "Gamma0_";
    public static final String SIGMA_0 = "Sigma0_";
    @FXML
    private JFXCheckBox outputComplex;
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
        parameters.put("outputImageInComplex",outputComplex.isSelected());
    }

    @Override
    public void setParameters(Map<String,Object> parameters) {
        this.parameters = parameters;
        outputBeta.setSelected((Boolean)parameters.getOrDefault("outputBetaBand",false));
        outputGamma.setSelected((Boolean)(parameters.getOrDefault("outputGammaBand",false)));
        outputSigma.setSelected((Boolean)(parameters.getOrDefault("outputSigmaBand",false)));
        outputInDb.setSelected((Boolean)(parameters.getOrDefault("outputImageScaleInDb",false)));
        outputComplex.setSelected((Boolean)(parameters.getOrDefault("outputImageInComplex",false)));
    }

    public void fixOutputBeta(boolean b) {
        outputBeta.setDisable(false);
    }
}
