package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import model.list.ProductListDTO;

import java.io.File;
import java.io.IOException;

import static utils.Configuration.getListDownloadFolderLocation;
import static utils.Configuration.getProductDownloadFolderLocation;

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
}
