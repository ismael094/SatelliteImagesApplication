package controller.processing;

import controller.MainController;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProductListProcessingResultItemController implements Initializable {
    static final Logger logger = LogManager.getLogger(ProductListProcessingResultItemController.class.getName());
    @FXML
    private ImageView image;
    @FXML
    private Label name;

    private File file;
    private Parent parent;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name.setText("Name");
    }

    public void setFile(File file) {
        this.file = file;
        setData();
    }

    private void setData() {
        name.setText(file.getName());
        /*Image image = generateThumbnail();
        if (image!= null)
            this.image.setImage(image);*/
    }

    private Image generateThumbnail() {
        try {
            BufferedImage scaledImg = Scalr.resize(ImageIO.read(file), 150,BufferedImage.TYPE_INT_BGR);
            return SwingFXUtils.toFXImage(scaledImg,null);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
