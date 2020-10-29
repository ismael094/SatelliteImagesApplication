package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.list.ProductListDTO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static utils.DownloadConfiguration.getListDownloadFolderLocation;
import static utils.DownloadConfiguration.getProductDownloadFolderLocation;

public class FileUtils {
    public static String DEFAULT_DOWNLOAD_FOLDER = System.getProperty("user.home")+"\\Documents\\SatInf\\Products";
    public static String DEFAULT_LIST_FOLDER = System.getProperty("user.home")+"\\Documents\\SatInf\\Lists";
    public static String DEFAULT_ALGORITHM_FOLDER = System.getProperty("user.home")+"\\Documents\\SatInf\\Algorithm";

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
            objectMapper.writeValue(new File(getListFolder(productList.getName())+"\\data.json"), productList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getListFolder(String name) {
        File file = new File(getListDownloadFolderLocation()+"\\"+name);
        int i = 1;
        while (file.exists())
            file = new File(getListDownloadFolderLocation()+"\\"+name+ " ("+(i++)+")");
        file.mkdirs();
        return file.getAbsolutePath();
    }

    public static boolean copyAlgorithm(File file) {
        try {
            Files.copy(file.toPath(), Paths.get(DEFAULT_ALGORITHM_FOLDER+"\\"+file.getName()), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
