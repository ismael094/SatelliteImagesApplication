package utils;

import model.SentinelData;
import model.preprocessing.workflow.defaultWorkflow.GRDDefaultWorkflowDTO;
import model.preprocessing.workflow.defaultWorkflow.SLCDefaultWorkflowDTO;
import model.preprocessing.workflow.WorkflowDTO;
import model.preprocessing.workflow.operation.Operation;
import model.preprocessing.workflow.operation.Operator;
import model.products.ProductDTO;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductBandsUtilsTest {

    @Test
    public void with_default_GRD_workflow_should_return_sigma0() {
        ProductDTO productDTO = SentinelData.getSentinel1Product();
        WorkflowDTO workflow = new GRDDefaultWorkflowDTO();

        List<String> outputBands = ProductBandUtils.getOutputBands(productDTO, workflow);

        assertThat(outputBands).isNotNull();
        assertThat(outputBands.size()).isGreaterThan(0);
        assertThat(outputBands).contains("Sigma0_VH","Sigma0_VV");
    }

    @Test
    public void with_beta_output_should_return_beta() {
        ProductDTO productDTO = SentinelData.getSentinel1Product();
        WorkflowDTO workflow = new GRDDefaultWorkflowDTO();

        Operation op = workflow.getOperation(Operator.CALIBRATION);
        op.getParameters().put("outputBetaBand",true);
        op.getParameters().remove("outputSigmaBand");

        List<String> outputBands = ProductBandUtils.getOutputBands(productDTO, workflow);
        assertThat(outputBands).isNotNull();
        assertThat(outputBands.size()).isGreaterThan(0);
        assertThat(outputBands).contains("Sigma0_VH","Sigma0_VV","Beta0_VV","Beta0_VH");
    }

    @Test
    public void with_no_bands_selected_output_should_return() {
        ProductDTO productDTO = SentinelData.getSentinel1Product();
        WorkflowDTO workflow = new SLCDefaultWorkflowDTO();

        Operation op = workflow.getOperation(Operator.CALIBRATION);
        op.getParameters().put("outputBetaBand",false);
        op.getParameters().put("outputSigmaBand",false);
        op.getParameters().put("outputGammaBand",false);

        List<String> outputBands = ProductBandUtils.getOutputBands(productDTO, workflow);
        assertThat(outputBands).isNotNull();
        assertThat(outputBands.size()).isGreaterThan(0);
        assertThat(outputBands).contains("Intensity_IW1_VH", "Intensity_IW1_VV");
    }

    @Test
    public void with_beta_sigma_and_beta_output_should_return_all() {
        ProductDTO productDTO = SentinelData.getSentinel1Product();
        WorkflowDTO workflow = new GRDDefaultWorkflowDTO();

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
        WorkflowDTO workflow = new GRDDefaultWorkflowDTO();

        Operation op = workflow.getOperation(Operator.CALIBRATION);
        op.getParameters().put("outputSigmaBand",false);

        List<String> outputBands = ProductBandUtils.getOutputBands(productDTO, workflow);
        assertThat(outputBands).isNotNull();
        assertThat(outputBands.size()).isGreaterThan(0);
        assertThat(outputBands).contains("Sigma0_VH","Sigma0_VV");
    }

    @Test
    public void with_slc_complex_data_output_should_return_intensity() {
        ProductDTO productDTO = SentinelData.getSentinel1Product();
        productDTO.setProductType("SLC");
        WorkflowDTO workflow = new SLCDefaultWorkflowDTO();

        List<String> outputBands = ProductBandUtils.getOutputBands(productDTO, workflow);
        assertThat(outputBands).isNotNull();
        assertThat(outputBands.size()).isGreaterThan(0);
        assertThat(outputBands).contains("Intensity_IW1_VV","Intensity_IW1_VH");
    }

    @Test
    public void with_slc_complex_data_and_terrain_correction_no_complex_output_should_return_sigma() {
        ProductDTO productDTO = SentinelData.getSentinel1Product();
        productDTO.setProductType("SLC");
        WorkflowDTO workflow = new SLCDefaultWorkflowDTO();


        workflow.getOperation(Operator.CALIBRATION).getParameters().put("outputImageInComplex",false);
        workflow.getOperation(Operator.CALIBRATION).getParameters().put("outputBetaBand",false);

        List<String> outputBands = ProductBandUtils.getOutputBands(productDTO, workflow);
        assertThat(outputBands).isNotNull();
        assertThat(outputBands.size()).isGreaterThan(0);
        assertThat(outputBands).contains("Sigma0_IW1_VH", "Sigma0_IW1_VV");
    }

    @Test
    public void with_no_output_complex_data_and_terrain_correction_no_complex_output_and_beta_output_should_return_sigma_and_beta() {
        ProductDTO productDTO = SentinelData.getSentinel1Product();
        productDTO.setProductType("SLC");
        WorkflowDTO workflow = new SLCDefaultWorkflowDTO();


        workflow.getOperation(Operator.CALIBRATION).getParameters().put("outputImageInComplex",false);
        workflow.getOperation(Operator.CALIBRATION).getParameters().put("outputBetaBand",true);

        List<String> outputBands = ProductBandUtils.getOutputBands(productDTO, workflow);
        assertThat(outputBands).isNotNull();
        assertThat(outputBands.size()).isGreaterThan(0);
        assertThat(outputBands).contains("Sigma0_IW1_VH", "Sigma0_IW1_VV","Beta0_IW1_VH", "Beta0_IW1_VV");
    }

    @Test
    public void with_slc_no_output_complex_data_and_terrain_correction_no_complex_output_and_beta_output_and_sigma_false_should_return_beta() {
        ProductDTO productDTO = SentinelData.getSentinel1Product();
        productDTO.setProductType("SLC");
        WorkflowDTO workflow = new SLCDefaultWorkflowDTO();


        workflow.getOperation(Operator.CALIBRATION).getParameters().put("outputImageInComplex",false);
        workflow.getOperation(Operator.CALIBRATION).getParameters().put("outputBetaBand",true);
        workflow.getOperation(Operator.CALIBRATION).getParameters().put("outputSigmaBand",false);

        List<String> outputBands = ProductBandUtils.getOutputBands(productDTO, workflow);
        assertThat(outputBands).isNotNull();
        assertThat(outputBands.size()).isGreaterThan(0);
        assertThat(outputBands).contains("Beta0_IW1_VH", "Beta0_IW1_VV");
    }

    @Test
    public void with_slc_complex_data_and_beta_output_and_sigma_false_should_return_intensity() {
        ProductDTO productDTO = SentinelData.getSentinel1Product();
        productDTO.setProductType("SLC");
        WorkflowDTO workflow = new SLCDefaultWorkflowDTO();


        workflow.getOperation(Operator.CALIBRATION).getParameters().put("outputImageInComplex",true);
        workflow.getOperation(Operator.CALIBRATION).getParameters().put("outputBetaBand",true);
        workflow.getOperation(Operator.CALIBRATION).getParameters().put("outputSigmaBand",false);

        List<String> outputBands = ProductBandUtils.getOutputBands(productDTO, workflow);
        assertThat(outputBands).isNotNull();
        assertThat(outputBands.size()).isGreaterThan(0);
        assertThat(outputBands).contains("Intensity_IW1_VV","Intensity_IW1_VH");
    }

}
