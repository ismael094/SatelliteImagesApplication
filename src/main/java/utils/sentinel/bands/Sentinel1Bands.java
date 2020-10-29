package utils.sentinel.bands;

import model.processing.workflow.WorkflowDTO;
import model.products.ProductDTO;
import model.products.Sentinel1ProductDTO;

import java.util.List;

public abstract class Sentinel1Bands {

    protected void band(boolean contain, StringBuilder res, StringBuilder bandsBuilder, String angle) {
        if (contain) {
            res.append(bandsBuilder.toString().replaceAll("band", angle));
        }
    }

    protected StringBuilder getBaseBand(String[] polarisation) {
        StringBuilder bandsBuilder = new StringBuilder();

        for (String s : polarisation) {
            bandsBuilder.append("band_").append(s).append(",");
        }

        //bandsBuilder.deleteCharAt(bandsBuilder.lastIndexOf(","));
        return bandsBuilder;
    }
    public abstract List<String> getBands(ProductDTO productDTO, WorkflowDTO workflowDTO);
}
