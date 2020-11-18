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

    /**
     * get service of product
     * @param productDTO product
     * @return service of product
     * @throws NotAuthenticatedException not login in service
     * @throws AuthenticationException not login in service
     */
    public static Service getService(ProductDTO productDTO) throws NotAuthenticatedException, AuthenticationException {
        return getMap().get(getProgramName(productDTO.getPlatformName()));
    }

    /**
     * get service with name
     * @param name name of service
     * @return service
     * @throws NotAuthenticatedException not login in service
     * @throws AuthenticationException not login in service
     */
    public static Service getService(String name) throws NotAuthenticatedException, AuthenticationException {
        return getMap().get(name);
    }
}
