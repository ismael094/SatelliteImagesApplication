package utils.sentinel.bands;

import model.preprocessing.workflow.WorkflowDTO;
import model.preprocessing.workflow.operation.Operation;
import model.preprocessing.workflow.operation.Operator;
import model.products.ProductDTO;
import model.products.sentinel.Sentinel1ProductDTO;
import utils.sentinel.Sentinel1Bands;

import java.util.Arrays;
import java.util.List;

public class GRDSentinel1Bands extends Sentinel1Bands {
    @Override
    public List<String> getBands(ProductDTO productDTO, WorkflowDTO workflowDTO) {
        Sentinel1ProductDTO sentinel1 = (Sentinel1ProductDTO)productDTO;
        Operation operation = workflowDTO.getOperation(Operator.CALIBRATION);
        String[] polarisation = sentinel1.getPolarizationMode().split(" ");

        StringBuilder res = new StringBuilder();
        StringBuilder bandsBuilder = getBaseBand(polarisation);

        if (workflowDTO.getOperation(Operator.TERRAIN_FLATTENING) != null) {
            band(true, res, bandsBuilder,  "Gamma0");
            return Arrays.asList(res.toString().split(",").clone());
        }


        band((Boolean) operation.getParameters().getOrDefault("outputBetaBand",false), res, bandsBuilder, "Beta0");

        band((Boolean) operation.getParameters().getOrDefault("outputGammaBand",false), res, bandsBuilder,  "Gamma0");

        band((Boolean) operation.getParameters().getOrDefault("outputSigmaBand",true), res, bandsBuilder,  "Sigma0");

        if (res.length() == 0)
            band(true, res, bandsBuilder,  "Sigma0");

        //res.deleteCharAt(bandsBuilder.lastIndexOf(","));

        return Arrays.asList(res.toString().split(",").clone());
    }
}
