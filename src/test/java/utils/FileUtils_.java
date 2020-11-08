package utils;

import javafx.beans.property.SimpleStringProperty;
import model.list.ProductListDTO;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class FileUtils_ {

    @Test
    public void save_and_load_json() {
        ObjectId objectId = new ObjectId();
        ProductListDTO productListDTO = new ProductListDTO(new SimpleStringProperty("test_file_no_match"),new SimpleStringProperty("descriptiobn"));
        productListDTO.setId(objectId);

        FileUtils.saveObjectToJson(productListDTO);
        FileUtils.saveObjectToJson(productListDTO);
        ProductListDTO productListDTO1 = FileUtils.loadObjectToJson(new File(DownloadConfiguration.getListDownloadFolderLocation() + "\\" + productListDTO.getName() + "\\data.json"));
    }

    @Test
    public void create_productlist_folder() {
        ObjectId objectId = new ObjectId();
        ProductListDTO productListDTO = new ProductListDTO(new SimpleStringProperty("test_file_no_match"),new SimpleStringProperty("descriptiobn"));
        productListDTO.setId(objectId);

        FileUtils.saveObjectToJson(productListDTO);
        assertThat(new File(DownloadConfiguration.getListDownloadFolderLocation()+"\\"+productListDTO.getName()).exists()).isTrue();
        assertThat(FileUtils.loadObjectToJson(new File(DownloadConfiguration.getListDownloadFolderLocation()+"\\"+productListDTO.getName()+"\\data.json"))).isNotNull();

        productListDTO = new ProductListDTO(new SimpleStringProperty("test_file_no_match"),new SimpleStringProperty("descriptiobn"));
        productListDTO.setId(new ObjectId());

        FileUtils.saveObjectToJson(productListDTO);
        assertThat(new File(DownloadConfiguration.getListDownloadFolderLocation()+"\\"+productListDTO.getName()+" (1)").exists()).isTrue();
        assertThat(FileUtils.loadObjectToJson(new File(DownloadConfiguration.getListDownloadFolderLocation()+"\\"+productListDTO.getName()+" (1)\\data.json"))).isNotNull();


    }

    @Test
    public void rename_file() throws IOException {
        assertThat(FileUtils.renameFile(DownloadConfiguration.getProductDownloadFolderLocation() + "\\test.zip",DownloadConfiguration.getProductDownloadFolderLocation() + "\\test2.zip")).isTrue();
        assertThat(new File(DownloadConfiguration.getProductDownloadFolderLocation() + "\\test2.zip").exists()).isTrue();
    }

    @After
    public void delete_folders() throws IOException {
        //Files.delete(Paths.get(DownloadConfiguration.getListDownloadFolderLocation()+"\\test_file_no_match"));
        //Files.delete(Paths.get(DownloadConfiguration.getListDownloadFolderLocation()+"\\test_file_no_match (1)"));
    }
}
