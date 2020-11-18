package controller.postprocessing;

import controller.processing.preview.PreviewImageController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.AlertFactory;
import utils.ThemeConfiguration;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.spi.IIORegistry;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

/**
 * Controller to show the processing results of a productList
 */
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

        GlyphsDude.setIcon(options, FontAwesomeIcon.BARS);
        deleteFile.setOnAction(event -> delete());
        openFile.setOnAction(e-> {
            loadImageTask();

            e.consume();
        });
    }

    private void loadImageTask() {
        Task<Image> task = new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                root.setCursor(Cursor.WAIT);
                if (file.getName().contains("tif")) {
                    IIORegistry.getDefaultInstance().registerApplicationClasspathSpis();
                    Iterator<ImageWriter> readers = ImageIO.getImageWritersByFormatName("TIF");
                    while (readers.hasNext()) {
                        System.out.println("reader: " + readers.next());
                    }
                    Iterator<ImageReader> tif1 = ImageIO.getImageReadersByFormatName("TIF");
                    tif1.next();
                    tif1.next();
                    ImageReader next = tif1.next();
                    next.setInput(ImageIO.createImageInputStream(file));
                    BufferedImage read = next.read(0);
                    return SwingFXUtils.toFXImage(read,null);
                }
                return new Image(file.toURI().toString());
            }
        };

        task.setOnSucceeded(event->{
            root.setCursor(Cursor.DEFAULT);
            try {
                loadImage(task.getValue());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        task.setOnFailed(event->{
            event.getSource().getException().printStackTrace();
            root.setCursor(Cursor.DEFAULT);
            AlertFactory.showErrorDialog("Error displaying image","Error displaying image","Error while trying to display image");
        });

        new Thread(task).start();
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

    /**
     * Load new processing file
     * @param img JavaFX image loaded
     * @throws IOException Error while loading FXML view
     */
    private void loadImage(Image img) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PreviewImageView.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        JMetro jMetro = ThemeConfiguration.getJMetroStyled();

        PreviewImageController controller = fxmlLoader.getController();

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
