package gui;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import model.products.Product;
import org.locationtech.jts.io.ParseException;

import java.util.List;

public class GTMapSearchController extends Pane {


    private final GTMap canvas;
    private HBox hbox;
    private boolean isSearchAreaDraw;
    private final double[] searchAreaCoordinates;
    private double baseDragedX;
    private double baseDragedY;

    public GTMapSearchController(double width, double height) {
        searchAreaCoordinates = new double[2];
        isSearchAreaDraw = false;
        this.canvas = new GTMap((int) width, (int) height);
        Pane mapPane = new	Pane(canvas.getCanvas());
        BorderPane border = new BorderPane();
        HBox controlBar = controlBar(canvas);
        border.setTop(controlBar);
        border.setCenter(mapPane);
        border.setBottom(addBottomHBox(canvas));
        Scene scene = new Scene(border);
        initEvent();

        getChildren().add(border);
    }

    private void initEvent() {
        addMapMousePressedEvent();
        addMapMouseClickedEvent();
        addMapMouseReleasedEvent();
        addMapMouseMovedEvent();
        addMapMouseDraggedEvent();
        addMapScrollMapEvent();

    }

    private void addMapScrollMapEvent() {
        addEventHandler(ScrollEvent.SCROLL, e-> {
            canvas.scroll(e.getDeltaY());
            e.consume();
        });
    }

    private void addMapMouseClickedEvent() {
        addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getClickCount() == 1) {
                canvas.selectFeature((int)(t.getX()),(int)(t.getY()-hbox.getHeight()));
                canvas.refresh();
            } else if (t.getClickCount() > 1)
                canvas.resetMap();
            t.consume();
        });
    }

    private void addMapMouseReleasedEvent() {
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if(e.isSecondaryButtonDown() && isSearchAreaDraw) {
                canvas.drawPolygon(searchAreaCoordinates[0], searchAreaCoordinates[1],e.getX(),e.getY());
                isSearchAreaDraw = false;
            }
            e.consume();
        });
    }

    private void addMapMouseMovedEvent() {
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            if (isSearchAreaDraw) {
                canvas.drawPolygon(searchAreaCoordinates[0], searchAreaCoordinates[1],e.getX(),e.getY());
            }
            e.consume();
        });
    }

    private void addMapMouseDraggedEvent() {
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            canvas.dragMap(baseDragedX,baseDragedY,e.getSceneX(),e.getSceneY());
            setBaseDraggedPosition(e);
            e.consume();

        });
    }

    private void addMapMousePressedEvent() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            setBaseDraggedPosition(e);
            if (e.isSecondaryButtonDown()) {
                if (!isSearchAreaDraw) {
                    setInitSearchCoordinates(e.getX(), e.getY());
                } else {
                    canvas.drawPolygon(searchAreaCoordinates[0], searchAreaCoordinates[1],e.getX(),e.getY());
                    isSearchAreaDraw = false;
                }
            }
            e.consume();
        });
    }

    private void setBaseDraggedPosition(MouseEvent e) {
        baseDragedX = e.getSceneX();
        baseDragedY = e.getSceneY();
    }

    private void setInitSearchCoordinates(double x, double y) {
        searchAreaCoordinates[0] = x;
        searchAreaCoordinates[1] = y;
        isSearchAreaDraw = true;
    }

    public void addProductsWKT(List<Product> products) {
        canvas.clearFeatures();
        products.forEach(p-> {
            try {
                addProductWKT(p.getFootprint(),p.getId());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        drawProductsWKT();
    }

    private void drawProductsWKT() {
        canvas.createAndDrawProductsLayer();
    }

    public void addProductWKT(String wkt, String id) throws ParseException {
        canvas.drawGeometryFromWKT(wkt,id);
    }

    public HBox addBottomHBox(GTMap canvas) {
        return new HBox();
    }

    public HBox controlBar(GTMap canvas) {
        hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");
        //buttonCurrent.setPrefSize(100, 20);

        Button deleteSearchAreaButton = getDeleteSearchAreaButton();
        //Button goToSelection = getGoToSelectionButton();
        Button resetMap = getResetMapButton();


        hbox.setSpacing(5f);
        hbox.getChildren().addAll(deleteSearchAreaButton,resetMap);

        return hbox;
    }


    private Button getResetMapButton() {
        Button resetMap = GlyphsDude.createIconButton(FontAwesomeIcon.EXPAND,"Reset map");
        resetMap.setAccessibleText("Reset map");
        resetMap.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
            canvas.resetMap();
        });
        return resetMap;
    }


    private Button getGoToSelectionButton() {
        Button goToSelection = GlyphsDude.createIconButton(FontAwesomeIcon.COMPRESS,"Zoom selected area");
        goToSelection.setAccessibleText("Zoom selected area");

        goToSelection.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
            canvas.goToSelection();
        });
        return goToSelection;
    }

    private Button getDeleteSearchAreaButton() {
        Button clearButton = GlyphsDude.createIconButton(FontAwesomeIcon.ERASER,"Delete selection");
        clearButton.setAccessibleText("Delete selection");
        clearButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
            canvas.removeLayer("SearchArea");
        });
        return clearButton;
    }

    public String getWKT() {
        return canvas.getWKT();
    }

    public void showProductArea(String id) {
        canvas.showFeatureArea(id);
    }

    public void clearMap() {
        canvas.clearMap();
    }

    public String getSelectedProduct() {
        return canvas.getSelectedFeatureID();

    }
}
