package controller.results;

import controller.processing.PreviewImageController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.imgscalr.Scalr;
import utils.ThemeConfiguration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProductListProcessingResultItemController implements Initializable {
    static final Logger logger = LogManager.getLogger(ProductListProcessingResultItemController.class.getName());

    @FXML
    private Button options;
    @FXML
    private AnchorPane root;
    @FXML
    private ImageView image;
    @FXML
    private Label name;
    @FXML
    private MenuItem deleteFile;
    @FXML
    private MenuItem openFile;

    private File file;
    private Parent parent;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.getStyleClass().add("processingResult");
        image.setOnMouseClicked(e->{
            try {
                if (e.getClickCount() > 2 && e.isPrimaryButtonDown())
                    loadImage();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        GlyphsDude.setIcon(options, FontAwesomeIcon.BARS);
        deleteFile.setOnAction(event -> delete());
        openFile.setOnAction(e-> {
            try {
                loadImage();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
    }

    private void delete() {
        boolean delete = file.delete();
    }

    public void setFile(File file) {
        this.file = file;
        if (file.getName().length()> 18)
            name.setText(file.getName().substring(0,15));
        else
            name.setText(file.getName());
        if (file.getName().contains("PNG")) {
            image.setImage(new Image("/img/image.jpg"));
        }
        Tooltip tooltip = new Tooltip(file.getName());
        Tooltip.install(name,tooltip);
    }

    private void loadImage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PreviewImageView.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        JMetro jMetro = ThemeConfiguration.getJMetroStyled();

        PreviewImageController controller = fxmlLoader.getController();
        //BufferedImage read = ImageIO.read(file);
        //controller.setImage(SwingFXUtils.toFXImage(read,null));
        System.out.println(file.toURI().toString());
        Image img = new Image(file.toURI().toString());
        controller.setImage(img);

        Stage stage = new Stage();
        stage.setHeight(755);
        stage.setWidth(755);
        stage.initOwner(image.getScene().getWindow());
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);
        jMetro.setScene(scene);
        stage.show();
    }
}