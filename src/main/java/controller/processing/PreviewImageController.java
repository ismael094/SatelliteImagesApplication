package controller.processing;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class PreviewImageController implements Initializable {
    @FXML
    private ImageView image;
    private Point2D dragInitialCoordinate;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        onDragInImageViewMoveImage();
        onDragMoveDragImage();
        onScrollInImageViewZoomImage();
    }

    public void setImage(WritableImage image) {
        this.image.setImage(image);
        this.image.setFitWidth(725);
        this.image.setFitHeight(725);
        this.image.setViewport(new Rectangle2D(0,0,725,725));
    }

    public void onScrollInImageViewZoomImage() {
        image.setOnScroll(event -> {
            Rectangle2D viewport = image.getViewport();
            double percent = (event.getDeltaY() / image.getImage().getWidth());
            double deltaW = viewport.getWidth() * percent;
            double deltaH = viewport.getHeight() * percent;
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
            image.setViewport(new Rectangle2D(viewport.getMinX()-difX*2.0, viewport.getMinY()-difY*2.0,viewport.getWidth(),viewport.getHeight()));
            setBaseDraggedPosition(event);
        });
    }

    private void setBaseDraggedPosition(MouseEvent e) {
        dragInitialCoordinate = new Point2D(e.getSceneX(),e.getSceneY());
    }


}
