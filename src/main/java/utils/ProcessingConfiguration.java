package utils;

import model.processing.workflow.defaultWorkflow.GRDDefaultWorkflowDTO;
import model.processing.workflow.defaultWorkflow.SLCDefaultWorkflowDTO;
import model.processing.workflow.defaultWorkflow.S2MSI1CDefaultWorkflowDTO;
import model.processing.workflow.WorkflowDTO;
import model.processing.workflow.WorkflowType;
import model.products.ProductType;
import services.processing.Processor;
import services.processing.processors.SentinelProcessor;

import java.util.HashMap;
import java.util.Map;

public class ProcessingConfiguration {
    public static String tmpDirectory = System.getProperty("user.home")+"\\Documents\\SatInf\\Tmp";

    public static Map<ProductType, Processor> getProcessor() {
        Map<ProductType, Processor> productTypeProcessHashMap = new HashMap<>();
        productTypeProcessHashMap.put(ProductType.SENTINEL,new SentinelProcessor());
        return productTypeProcessHashMap;
    }

    public static WorkflowDTO getDefaultWorkflow(String productType) {
        Class<?> class_ = null;
        try {
            class_ = Class.forName("model.processing.workflow.defaultWorkflow."+productType+"DefaultWorkflowDTO");
            return (WorkflowDTO)class_.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            return null;
        }
    }


}
