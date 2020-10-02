package controller;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gui.GTMap;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import model.products.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.data.FeatureLockException;
import org.locationtech.jts.io.ParseException;

import java.io.IOException;
import java.util.List;

public class GTMapSearchController {


    private final GTMap geotoolsMap;
    private final BorderPane border;
    private HBox hbox;
    private boolean isSearchAreaDraw;
    private final double[] searchAreaCoordinates;
    private double baseDragedX;
    private double baseDragedY;

    static final Logger logger = LogManager.getLogger(GTMapSearchController.class.getName());
    private boolean disableMouseClickedEvent;

    public GTMapSearchController(double width, double height, boolean controlBarActive) {
        searchAreaCoordinates = new double[2];
        disableMouseClickedEvent = false;
        isSearchAreaDraw = false;
        HBox controlBar = controlBar();
        geotoolsMap = new GTMap((int) width, (int) (height-controlBar.getHeight()),false);
        border = new BorderPane();
        if (controlBarActive)
            border.setTop(controlBar);
        else
            border.setTop(null);
        border.setCenter(geotoolsMap);
        border.setLeft(null);
        border.setRight(null);
        addGeotoolsMapEvents();
    }

    public Parent getView() {
        return border;
    }

    public void disableMouseClickedEvent() {
        this.disableMouseClickedEvent = true;
    }

    private void addGeotoolsMapEvents() {
        addMapMousePressedEvent();
        addMapMouseClickedEvent();
        addMapMouseReleasedEvent();
        addMapMouseMovedEvent();
        addMapMouseDraggedEvent();
        addMapScrollMapEvent();
    }

    private void addMapScrollMapEvent() {
        geotoolsMap.addEventHandler(ScrollEvent.SCROLL, e-> {
            geotoolsMap.scroll(e.getDeltaY());
            e.consume();
        });
    }

    private void addMapMouseClickedEvent() {
        geotoolsMap.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (disableMouseClickedEvent)
                return;
            if (t.getClickCount() == 1) {
                try {
                    boolean multipleFeatureSelection = t.isControlDown();
                    geotoolsMap.selectFeature((int)(t.getX()),(int)(t.getY()),multipleFeatureSelection);

                } catch (IOException e) {

                    logger.atError().log("Not able to style selected features: {0}",e);
                }
                geotoolsMap.refresh();
            } else if (t.getClickCount() > 1)
                geotoolsMap.resetMap();
            t.consume();
        });
    }

    private void addMapMouseReleasedEvent() {
        geotoolsMap.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if(e.isSecondaryButtonDown() && isSearchAreaDraw) {
                geotoolsMap.drawPolygon(searchAreaCoordinates[0], searchAreaCoordinates[1],e.getX(),e.getY());
                isSearchAreaDraw = false;
            }
            e.consume();
        });
    }

    private void addMapMouseMovedEvent() {
        geotoolsMap.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            if (isSearchAreaDraw) {
                geotoolsMap.drawPolygon(searchAreaCoordinates[0], searchAreaCoordinates[1],e.getX(),e.getY());
            }
            e.consume();
        });
    }

    private void addMapMouseDraggedEvent() {
        geotoolsMap.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            geotoolsMap.dragMap(baseDragedX,baseDragedY,e.getSceneX(),e.getSceneY());
            setBaseDraggedPosition(e);
            e.consume();

        });
    }

    private void addMapMousePressedEvent() {
        geotoolsMap.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            setBaseDraggedPosition(e);
            if (e.isSecondaryButtonDown()) {
                if (!isSearchAreaDraw) {
                    setInitSearchCoordinates(e.getX(), e.getY());
                } else {
                    geotoolsMap.drawPolygon(searchAreaCoordinates[0], searchAreaCoordinates[1],e.getX(),e.getY());
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
        geotoolsMap.clearFeatures();
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
        geotoolsMap.createAndDrawProductsLayer();
    }

    public void addProductWKT(String wkt, String id) throws ParseException {
        geotoolsMap.createFeatureFromWKT(wkt,id);
    }

    public HBox controlBar() {
        hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        //buttonCurrent.setPrefSize(100, 20);

        Button deleteSearchAreaButton = getDeleteSearchAreaButton();
        //Button goToSelection = getGoToSelectionButton();
        Button resetMap = getResetMapButton();


        hbox.setSpacing(10f);
        hbox.getChildren().addAll(deleteSearchAreaButton,resetMap);

        return hbox;
    }

    private Button getResetMapButton() {
        Button resetMap = GlyphsDude.createIconButton(FontAwesomeIcon.EXPAND,"Reset map");
        resetMap.setAccessibleText("Reset map");
        resetMap.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
            geotoolsMap.resetMap();
        });
        return resetMap;
    }

    private Button getDeleteSearchAreaButton() {
        Button clearButton = GlyphsDude.createIconButton(FontAwesomeIcon.ERASER,"Delete selection");
        clearButton.setAccessibleText("Delete selection");
        clearButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
            geotoolsMap.removeLayer("SearchArea");
        });
        return clearButton;
    }

    public String getWKT() {
        return geotoolsMap.getWKT();
    }

    public void showProductArea(List<String> ids) {
        geotoolsMap.showFeatureArea(ids);
    }

    public void clearMap() {
        geotoolsMap.clearMap();
    }

    public String getSelectedProduct() {
        return geotoolsMap.getSelectedFeatureID();

    }
}
