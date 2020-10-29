package utils;

import model.SentinelData;
import model.processing.workflow.Sentinel1GRDDefaultWorkflowDTO;
import model.processing.workflow.WorkflowDTO;
import model.processing.workflow.operation.Operation;
import model.processing.workflow.operation.Operator;
import model.products.ProductDTO;
import org.assertj.core.util.Strings;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductBandsUtils_ {

    @Test
    public void with_default_GRD_workflow_should_return_sigma0() {
        ProductDTO productDTO = SentinelData.getSentinel1Product();
        WorkflowDTO workflow = new Sentinel1GRDDefaultWorkflowDTO();

        List<String> outputBands = ProductBandUtils.getOutputBands(productDTO, workflow);

        assertThat(outputBands).isNotNull();
        assertThat(outputBands.size()).isGreaterThan(0);
        assertThat(outputBands).contains("Sigma0_VH","Sigma0_VV");
    }

    @Test
    public void with_beta_output_should_return_beta() {
        ProductDTO productDTO = SentinelData.getSentinel1Product();
        WorkflowDTO workflow = new Sentinel1GRDDefaultWorkflowDTO();

        Operation op = workflow.getOperation(Operator.CALIBRATION);
        op.getParameters().put("outputBetaBand",true);

        List<String> outputBands = ProductBandUtils.getOutputBands(productDTO, workflow);
        assertThat(outputBands).isNotNull();
        assertThat(outputBands.size()).isGreaterThan(0);
        assertThat(outputBands).contains("Sigma0_VH","Sigma0_VV","Beta0_VV","Beta0_VH");
    }

    @Test
    public void with_beta_sigma_and_beta_output_should_return_all() {
        ProductDTO productDTO = SentinelData.getSentinel1Product();
        WorkflowDTO workflow = new Sentinel1GRDDefaultWorkflowDTO();

        Operation op = workflow.getOperation(Operator.CALIBRATION);
        op.getParameters().put("outputBetaBand",true);
        op.getParameters().put("outputGammaBand",true);

        List<String> outputBands = ProductBandUtils.getOutputBands(productDTO, workflow);
        assertThat(outputBands).isNotNull();
        assertThat(outputBands.size()).isGreaterThan(0);
        assertThat(outputBands).contains("Sigma0_VH","Sigma0_VV","Beta0_VV","Beta0_VH","Gamma0_VV","Gamma0_VH");
    }

    @Test
    public void with_none_output_should_return_sigma() {
        ProductDTO productDTO = SentinelData.getSentinel1Product();
        WorkflowDTO workflow = new Sentinel1GRDDefaultWorkflowDTO();

        Operation op = workflow.getOperation(Operator.CALIBRATION);
        op.getParameters().put("outputSigmaBand",false);

        List<String> outputBands = ProductBandUtils.getOutputBands(productDTO, workflow);
        assertThat(outputBands).isNotNull();
        assertThat(outputBands.size()).isGreaterThan(0);
        assertThat(outputBands).contains("Sigma0_VH","Sigma0_VV");
    }
}
