package utils;

import services.Service;

import java.util.HashMap;
import java.util.Map;

public class ServiceConfiguration {

    public static Map<String, Service> getServiceMap() {
        Map<String, Service> serviceMap = new HashMap<>();
        serviceMap.put("Sentinel", null);
        return serviceMap;
    }

    public static Map<String, Service> getDownloadServiceMap() {
        Map<String, Service> serviceMap = new HashMap<>();
        serviceMap.put("Sentinel", null);
        return serviceMap;
    }

    public static Map<String, Service> getProcessingServiceMap() {
        Map<String, Service> serviceMap = new HashMap<>();
        serviceMap.put("Sentinel", null);
        return serviceMap;
    }
}
