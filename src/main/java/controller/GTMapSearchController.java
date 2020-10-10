package controller;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gui.GTMap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import model.products.ProductDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.io.ParseException;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class GTMapSearchController {


    private final GTMap geotoolsMap;
    private final BorderPane border;
    private HBox hbox;
    private boolean isSearchAreaDrawing;
    private Point2D initialCoodinates;
    private double baseDragedX;
    private double baseDragedY;

    static final Logger logger = LogManager.getLogger(GTMapSearchController.class.getName());
    private final BooleanProperty wasPrimaryButtonClicked;
    private final BooleanProperty wasSecondaryButtonClicked;

    private Color selectedFeaturesBorderColor;
    private Color selectedFeaturesFillColor;
    private Color notSelectedFeaturesBorderColor;
    private Color notSelectedFeaturesFillColor;
    private boolean dragged;

    public GTMapSearchController(double width, double height, boolean controlBarActive) {
        wasPrimaryButtonClicked = new SimpleBooleanProperty(false);
        wasSecondaryButtonClicked = new SimpleBooleanProperty(false);
        isSearchAreaDrawing = false;
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
        dragged = false;

        selectedFeaturesBorderColor = Color.BLUE;
        selectedFeaturesFillColor = Color.CYAN;
        notSelectedFeaturesBorderColor = Color.BLACK;
        notSelectedFeaturesFillColor = null;
    }

    public Parent getView() {
        return border;
    }

    private void addGeotoolsMapEvents() {
        addMapMousePressedEvent();
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

    public void addSelectedAreaEvent(String layer) {
        geotoolsMap.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getClickCount() == 1 && wasPrimaryButtonClicked.get() && !dragged) {
                wasPrimaryButtonClicked.set(false);
                try {
                    geotoolsMap.selectFeature(new Point2D(t.getX(),t.getY()),t.isControlDown(),layer, selectedFeaturesBorderColor, selectedFeaturesFillColor, notSelectedFeaturesBorderColor, notSelectedFeaturesFillColor);
                } catch (IOException e) {
                    logger.atError().log("Not able to style selected features: {0}",e);
                }
                geotoolsMap.refresh();
            } else if (t.getClickCount() > 1)
                geotoolsMap.resetMap();
            dragged=false;
            t.consume();
        });
    }

    private void addMapMouseReleasedEvent() {
        geotoolsMap.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if(e.isSecondaryButtonDown() && isSearchAreaDrawing) {
                createSearchArea(new Point2D(e.getX(),e.getY()));
                isSearchAreaDrawing = false;
            }
            e.consume();
        });
    }

    private void addMapMouseMovedEvent() {
        geotoolsMap.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            if (isSearchAreaDrawing) {
                createSearchArea(new Point2D(e.getX(),e.getY()));
            }
            e.consume();
        });
    }

    private void addMapMouseDraggedEvent() {
        geotoolsMap.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            dragged = true;
            geotoolsMap.dragMap(baseDragedX,baseDragedY,e.getSceneX(),e.getSceneY());
            setBaseDraggedPosition(e);
            e.consume();

        });
    }

    private void addMapMousePressedEvent() {
        geotoolsMap.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            setBaseDraggedPosition(e);
            wasPrimaryButtonClicked.set(e.isPrimaryButtonDown());
            wasSecondaryButtonClicked.set(e.isSecondaryButtonDown());
            if (e.isSecondaryButtonDown()) {
                if (!isSearchAreaDrawing) {
                    setInitialCoordinates(e.getX(), e.getY());
                } else {
                    createSearchArea(new Point2D(e.getX(),e.getY()));
                    isSearchAreaDrawing = false;
                }
            }
            e.consume();
        });
    }

    public void showProductArea(List<String> ids,String layer) {
        geotoolsMap.highlightFeatures(ids, layer, selectedFeaturesBorderColor, selectedFeaturesFillColor, notSelectedFeaturesBorderColor, notSelectedFeaturesFillColor);
    }

    private void createSearchArea(Point2D end) {
        geotoolsMap.createLayerFromCoordinates(initialCoodinates,end,"searchArea");
    }

    private void setBaseDraggedPosition(MouseEvent e) {
        baseDragedX = e.getSceneX();
        baseDragedY = e.getSceneY();
    }

    private void setInitialCoordinates(double x, double y) {
        initialCoodinates = new Point2D(x,y);
        isSearchAreaDrawing = true;
    }

    public void printProductsInMap(List<ProductDTO> products, Color borderColor, Color fillColor) {
        printProductsInLayer("products",products,borderColor,fillColor);
    }

    public void printProductsInLayer(String layer, List<ProductDTO> products, Color borderColor, Color fillColor) {

        geotoolsMap.clearFeatures(layer);
        products.forEach(p-> {
            try {
                addProductWKT(p.getFootprint(),p.getId(),layer);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
        drawFeaturesOfLayer(layer,borderColor, fillColor);
    }

    public void drawFeaturesOfLayer(String layer,Color borderColor, Color fillColor) {
        geotoolsMap.createAndDrawLayer(layer, borderColor, fillColor);
    }

    public void addProductWKT(String wkt, String id,String layer) throws ParseException {
        geotoolsMap.createFeatureFromWKT(wkt,id,layer);
    }

    public HBox controlBar() {
        hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);

        Button deleteSearchAreaButton = getDeleteSearchAreaButton();
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
            geotoolsMap.removeLayer("searchArea");
        });
        return clearButton;
    }

    public String getWKT() {
        return geotoolsMap.getWKT();
    }

    public void clearMap(String layer) {
        geotoolsMap.clearFeatures(layer);
    }

    public void focusOnLayer(String layer) {
        geotoolsMap.focusOnLayer(layer);
    }

    public String getSelectedProduct() {
        return geotoolsMap.getSelectedFeatureID();
    }

    public void setSelectedFeaturesBorderColor(Color borderColor, Color fillColor) {
        selectedFeaturesBorderColor = borderColor;
        selectedFeaturesFillColor = fillColor;
    }

    public void setNotSelectedFeaturesBorderColor(Color borderColor, Color fillColor) {
        notSelectedFeaturesBorderColor = borderColor;
        notSelectedFeaturesFillColor = fillColor;
    }
}
