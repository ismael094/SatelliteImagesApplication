package utils;

import model.exception.AuthenticationException;
import model.exception.NotAuthenticatedException;
import model.products.ProductDTO;
import services.CopernicusService;
import services.Service;

import java.util.HashMap;
import java.util.Map;

public class ServiceFactory {

    private static String getProgramName(String name) {
        return "Copernicus";
    }

    private static Map<String,Service> getMap() throws NotAuthenticatedException, AuthenticationException {
        HashMap<String,Service> map = new HashMap<>();
        map.put("Copernicus", CopernicusService.getInstance());
        return map;
    }

    public static Service getService(ProductDTO productDTO) throws NotAuthenticatedException, AuthenticationException {
        return getMap().get(getProgramName(productDTO.getPlatformName()));
    }

    public static Service getService(String name) throws NotAuthenticatedException, AuthenticationException {
        return getMap().get(name);
    }

    public static Service getDownloadService(ProductDTO productDTO) {
        return null;
    }

    public static Service getProcessorService(ProductDTO productDTO) {
        return null;
    }
}
