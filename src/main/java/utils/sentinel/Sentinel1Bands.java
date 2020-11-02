package utils.sentinel;

import model.preprocessing.workflow.WorkflowDTO;
import model.products.ProductDTO;

import java.util.List;

public abstract class Sentinel1Bands {

    protected void band(boolean contain, StringBuilder res, StringBuilder bandsBuilder, String angle) {
        if (contain) {
            res.append(bandsBuilder.toString().replaceAll("band", angle));
        }
    }

    protected StringBuilder getBaseBand(String[] polarisation) {
        StringBuilder bandsBuilder = new StringBuilder();

        for (String p : polarisation) {
            bandsBuilder.append("band_").append(p).append(",");
        }

        return bandsBuilder;
    }

    public abstract List<String> getBands(ProductDTO productDTO, WorkflowDTO workflowDTO);
}
