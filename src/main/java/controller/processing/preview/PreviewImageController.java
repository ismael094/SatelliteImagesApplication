package controller.processing.preview;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class PreviewImageController implements Initializable {
    @FXML
    private AnchorPane root;
    @FXML
    private ImageView image;
    private Point2D dragInitialCoordinate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setImage(WritableImage image) {
        this.image.setImage(image);
        initEvents();
    }

    public void setImage(Image image) {
        this.image.setImage(image);
        initEvents();
    }

    private void initEvents() {
        fitRoot();
        onDragInImageViewMoveImage();
        onDragMoveDragImage();
        onScrollInImageViewZoomImage();
        this.image.setViewport(new Rectangle2D(0,0,755,755));
    }

    private void fitRoot() {
        this.image.setFitWidth(755);
        this.image.setFitHeight(755);
    }

    public void onScrollInImageViewZoomImage() {
        image.setOnScroll(event -> {
            Rectangle2D viewport = image.getViewport();
            double percent = (event.getDeltaY() / image.getImage().getWidth());
            double deltaW = viewport.getWidth() * percent*3.0;
            double deltaH = viewport.getHeight() * percent*3.0;
            if ((viewport.getWidth()-deltaW+viewport.getMinX()) > image.getImage().getWidth() || (viewport.getHeight()-deltaW+viewport.getMinY()) > image.getImage().getHeight())
                return;
            image.setViewport(new Rectangle2D(viewport.getMinX(),viewport.getMinY(),viewport.getWidth()-deltaW,viewport.getHeight()-deltaH));

        });
    }

    public void onDragInImageViewMoveImage() {
        image.setOnMousePressed(this::setBaseDraggedPosition);
    }

    public void onDragMoveDragImage() {
        image.setOnMouseDragged(event -> {
            Rectangle2D viewport = image.getViewport();
            double difX = event.getSceneX() - dragInitialCoordinate.getX();
            double difY = event.getSceneY() - dragInitialCoordinate.getY();
            setBaseDraggedPosition(event);
            if (viewport.getMinX()-difX*2.0 < 0 || viewport.getMinY()-difY*2.0 < 0)
                return;
            if ((viewport.getMinX()-difX*2.0)+viewport.getWidth() > image.getImage().getWidth() || (viewport.getMinY()-difY*2.0)+viewport.getHeight() > image.getImage().getHeight() )
                return;
            image.setViewport(new Rectangle2D(viewport.getMinX()-difX*2.0, viewport.getMinY()-difY*2.0,viewport.getWidth(),viewport.getHeight()));

        });
    }

    private void setBaseDraggedPosition(MouseEvent e) {
        dragInitialCoordinate = new Point2D(e.getSceneX(),e.getSceneY());
    }


}
