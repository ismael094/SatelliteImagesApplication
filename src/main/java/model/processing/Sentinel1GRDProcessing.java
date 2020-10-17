package model.processing;

import model.list.ProductListDTO;
import model.products.ProductDTO;
import org.locationtech.jts.io.ParseException;

import java.io.IOException;

public class Sentinel1GRDProcessing extends Sentinel1Processing{

    @Override
    public void process(ProductListDTO productList) {

    }

    @Override
    public void process(ProductDTO productDTO) {
        /*try {
            Product product = readProduct("path");
            product = getThermalNoiseRemoval(product,getThermalNoiseRemovalParameters());
            product = getCalibration(product, getCalibrationParameters(product));
            saveProduct(product,System.getProperty("user.home")+"\\Documents\\SatInf\\processing","BEAM-DIMAP");
            product = readProduct(System.getProperty("user.home")+"\\Documents\\SatInf\\processing.dim");
            product = getTerrainFlattening(product,getTerrainFlatteningParameters(product));
            product = getTerrainCorrection(product,getTerrainCorrectionParameters(product));
            product =  subset(product,getSubsetParameters(productDTO.getFootprint()));
            saveProduct(product,System.getProperty("user.home")+"\\Documents\\SatInf\\default\\"+productDTO.getTitle(),"GeoTIFF+XML");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }*/

    }
}
