package utils;

import model.products.ProductType;
import services.processing.Processor;
import services.processing.SentinelProcessor;

import java.util.HashMap;
import java.util.Map;

public class ProcessingConfiguration {
    public static String tmpDirectory = System.getProperty("user.home")+"\\Documents\\SatInf\\Tmp";

    public static Map<ProductType, Processor> getProcessor() {
        Map<ProductType, Processor> productTypeProcessHashMap = new HashMap<>();
        productTypeProcessHashMap.put(ProductType.SENTINEL,new SentinelProcessor());
        return productTypeProcessHashMap;
    }


}
