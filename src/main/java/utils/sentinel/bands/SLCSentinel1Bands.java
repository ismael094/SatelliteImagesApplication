package utils.sentinel.bands;

import model.preprocessing.workflow.WorkflowDTO;
import model.preprocessing.workflow.operation.Operation;
import model.preprocessing.workflow.operation.Operator;
import model.products.ProductDTO;
import model.products.sentinel.Sentinel1ProductDTO;
import utils.sentinel.Sentinel1Bands;

import java.util.Arrays;
import java.util.List;

public class SLCSentinel1Bands extends Sentinel1Bands {

    @Override
    public List<String> getBands(ProductDTO productDTO, WorkflowDTO workflowDTO) {
        Operation calibration = workflowDTO.getOperation(Operator.CALIBRATION);

        Operation correction = workflowDTO.getOperation(Operator.TERRAIN_CORRECTION);

        String[] subswaths = String.valueOf(workflowDTO.getOperation(Operator.TOPSAR_SPLIT).getParameters().get("subswath")).split(",");

        String[] polarisation = ((Sentinel1ProductDTO)productDTO).getPolarizationMode().split(" ");

        StringBuilder builder = new StringBuilder();

        if ((Boolean) calibration.getParameters().getOrDefault("outputImageInComplex",false))
            return slcBands("Intensity",polarisation, subswaths,  builder);

        addBand((Boolean) calibration.getParameters().getOrDefault("outputBetaBand",false), "Beta0", polarisation, subswaths, builder);

        addBand((Boolean) calibration.getParameters().getOrDefault("outputGammaBand",false), "Gamma0", polarisation, subswaths, builder);

        addBand((Boolean) calibration.getParameters().getOrDefault("outputSigmaBand",true), "Sigma0", polarisation, subswaths, builder);

        if (builder.length() == 0)
            slcBands("Sigma0",subswaths,polarisation,builder);

        //res.deleteCharAt(bandsBuilder.lastIndexOf(","));

        return Arrays.asList(builder.toString().split(",").clone());
    }

    private void addBand(Boolean contain, String band, String[] polarisation, String[] subswaths, StringBuilder res) {
        if (contain)
            slcBands(band,polarisation,subswaths,res);
    }

    private List<String> slcBands(String band,String[] polarisation, String[] subswaths, StringBuilder builder) {
        for (String p : polarisation) {
            setSLCBand(band, p, subswaths[0], builder);
        }
        return Arrays.asList(builder.toString().split(","));
    }

    public void setSLCBand(String band, String polarisation, String subswath, StringBuilder builder) {
        builder.append(band).append("_").append(subswath).append("_").append(polarisation).append(",");
    }




}
