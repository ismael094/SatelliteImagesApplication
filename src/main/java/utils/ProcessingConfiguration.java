package utils;

import model.preprocessing.workflow.WorkflowDTO;
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
            class_ = Class.forName("model.preprocessing.workflow.defaultWorkflow."+productType+"DefaultWorkflowDTO");
            return (WorkflowDTO)class_.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            return null;
        }
    }


}
