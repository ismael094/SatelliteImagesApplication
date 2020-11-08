package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.list.ProductListDTO;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static java.lang.System.currentTimeMillis;
import static utils.DownloadConfiguration.getListDownloadFolderLocation;
import static utils.DownloadConfiguration.getProductDownloadFolderLocation;

public class FileUtils {
    public static String DEFAULT_DOWNLOAD_FOLDER = System.getProperty("user.home")+"\\Documents\\SatInf\\Products";
    public static String DEFAULT_LIST_FOLDER = System.getProperty("user.home")+"\\Documents\\SatInf\\Lists";

    public static void createFolderIfNotExists(String path) {
        File file = new File(path);
        if (!file.exists())
            file.mkdir();
    }

    public static boolean fileExists(String file) {
        return new File(file).exists();
    }

    public static boolean productExists(String title) {
        return fileExists(getProductDownloadFolderLocation()+"\\"+title+".zip");
    }

    public static void saveObjectToJson(ProductListDTO productList) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.writeValue(new File(getListFolder(productList)+"\\data.json"), productList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ProductListDTO loadObjectToJson(File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(file, ProductListDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getListFolder(ProductListDTO name) {

        String path = getListDownloadFolderLocation()+"\\"+name;
        File file = new File(path);

        int i = 1;
        while (file.exists()) {
            if (isFolderOfProductList(name,path)) return path;
            path = getListDownloadFolderLocation()+"\\"+name+ " ("+(i++)+")";
            file = new File(path);
        }
        file.mkdirs();
        return file.getAbsolutePath();
    }


    private static boolean isFolderOfProductList(ProductListDTO list, String path) {
        File json = new File(path+"\\data.json");
        ProductListDTO productListDTO = loadObjectToJson(json);
        return !json.exists() || (productListDTO != null && productListDTO.getId().toString().equals(list.getId().toString()));
    }


    public static boolean copyAlgorithm(File file) {
        try {
            FileUtils.createFolderIfNotExists(AlgorithmsLoader.getAlgorithmFolder());
            Files.copy(file.toPath(), Paths.get(AlgorithmsLoader.getAlgorithmFolder()+"\\"+file.getName()), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean renameFile(String temporalFileLocation, String finalFileLocation) {
        try {
            org.apache.commons.io.FileUtils.moveFile(new File(temporalFileLocation),new File(finalFileLocation));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
