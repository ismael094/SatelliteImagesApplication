package controller;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gui.GTMap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import model.products.ProductDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.io.ParseException;

import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * Controller to manage GeoToolsMap
 */
public class GTMapController {

    @FXML
    private AnchorPane mapContainer;
    @FXML
    private Button delete;
    @FXML
    private Button reset;

    private Parent parent;

    private final GTMap geotoolsMap;
    private boolean isSearchAreaDrawing;
    private Point2D initialCoordinates;
    private double baseDragedX;
    private double baseDragedY;

    static final Logger logger = LogManager.getLogger(GTMapController.class.getName());
    private final BooleanProperty wasPrimaryButtonClicked;
    private final BooleanProperty wasSecondaryButtonClicked;

    private Color selectedFeaturesBorderColor;
    private Color selectedFeaturesFillColor;
    private Color notSelectedFeaturesBorderColor;
    private Color notSelectedFeaturesFillColor;
    private boolean dragged;
    private String searchArea;
    private String selectedLayerEvent;

    public GTMapController(double width, double height, boolean showControls) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GeotoolsMapView.fxml"));
        loader.setController(this);
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        wasPrimaryButtonClicked = new SimpleBooleanProperty(false);
        wasSecondaryButtonClicked = new SimpleBooleanProperty(false);
        isSearchAreaDrawing = false;
        geotoolsMap = new GTMap((int)width,(int)height,false);
        //geotoolsMap.widthProperty().bind(mapContainer.widthProperty());
        //geotoolsMap.heightProperty().bind(mapContainer.heightProperty());

        if (showControls) {
            getDeleteSearchAreaButton();
            getResetMapButton();
        }

        mapContainer.getChildren().add(geotoolsMap);
        AnchorPane.setTopAnchor(geotoolsMap,0.0);
        AnchorPane.setBottomAnchor(geotoolsMap,0.0);
        AnchorPane.setRightAnchor(geotoolsMap,0.0);
        AnchorPane.setLeftAnchor(geotoolsMap,0.0);
        addGeotoolsMapEvents();
        geotoolsMap.toBack();
        dragged = false;

        selectedFeaturesBorderColor = Color.BLUE;
        selectedFeaturesFillColor = Color.CYAN;
        notSelectedFeaturesBorderColor = Color.BLACK;
        notSelectedFeaturesFillColor = null;
    }

    /**
     * Get GeoTools Map
     * @return GeoTools Canvas
     */
    public GTMap getMap() {
        return geotoolsMap;
    }

    /**
     * Get map view
     * @return Parent view
     */
    public Parent getView() {
        return parent;
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

    /**
     * Add mouse click event in layer @layer
     * @param layer Name of the layer to add the event
     */
    public void addSelectedAreaEvent(String layer) {
        setLayerSelectedAreaEvent(layer);
        geotoolsMap.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getClickCount() == 1 && wasPrimaryButtonClicked.get() && !dragged) {
                wasPrimaryButtonClicked.set(false);
                try {
                    geotoolsMap.selectFeature(new Point2D(t.getX(),t.getY()),t.isControlDown(),this.selectedLayerEvent, selectedFeaturesBorderColor, selectedFeaturesFillColor, notSelectedFeaturesBorderColor, notSelectedFeaturesFillColor);
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

    /**
     * set layer to mouseclick event
     * @param layer Name of the layer
     */
    public void setLayerSelectedAreaEvent(String layer) {
        this.selectedLayerEvent = layer;
    }

    private void addMapMouseReleasedEvent() {
        geotoolsMap.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if(e.isSecondaryButtonDown() && isSearchAreaDrawing) {
                createSearchArea(createPoint(geotoolsMap.transformSceneToWorldCoordinate(e.getX(), e.getY())));
                isSearchAreaDrawing = false;
            }
            e.consume();
        });
    }

    private void addMapMouseMovedEvent() {
        geotoolsMap.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            if (isSearchAreaDrawing) {
                createSearchArea(createPoint(geotoolsMap.transformSceneToWorldCoordinate(e.getX(), e.getY())));
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
                    initialCoordinates = createPoint(geotoolsMap.transformSceneToWorldCoordinate(e.getX(), e.getY()));
                } else {
                    createSearchArea(createPoint(geotoolsMap.transformSceneToWorldCoordinate(e.getX(), e.getY())));
                    isSearchAreaDrawing = false;
                }
            }
            e.consume();
        });
    }

    /**
     * Highlight areas in layer
     * @param ids Id of the features
     * @param layer Name of the layer
     */
    public void showProductArea(List<String> ids,String layer) {
        geotoolsMap.highlightFeatures(ids, layer, selectedFeaturesBorderColor, selectedFeaturesFillColor, notSelectedFeaturesBorderColor, notSelectedFeaturesFillColor);
    }

    private void createSearchArea(Point2D end) {
        if (end == null || initialCoordinates == null) {
            isSearchAreaDrawing = false;
            return;
        }
        geotoolsMap.createLayerFromCoordinates(initialCoordinates,end,"searchArea");
        try {
            searchArea = geotoolsMap.getWKTFromCoordinates(initialCoordinates,end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set feature in searchLayer
     * @param wkt Area of search
     */
    public void setSearchArea(String wkt) {
        this.searchArea = wkt;
    }

    private void setBaseDraggedPosition(MouseEvent e) {
        baseDragedX = e.getSceneX();
        baseDragedY = e.getSceneY();
    }

    private Point2D createPoint(double[] coordinates) {
        isSearchAreaDrawing = true;
        return new Point2D(coordinates[0],coordinates[1]);
    }

    /**
     * Print products footprints in products layer
     * @param products ProductDTO products
     * @param borderColor Color of border
     * @param fillColor Fill color
     */
    public void printProductsInMap(List<ProductDTO> products, Color borderColor, Color fillColor) {
        drawProductsInLayer("products",products,borderColor,fillColor);
    }

    /**
     * Print products footprints in @layer
     * @param layer Name of the layer
     * @param products Products to print
     * @param borderColor Color of border
     * @param fillColor Fill color
     */
    public void drawProductsInLayer(String layer, List<ProductDTO> products, Color borderColor, Color fillColor) {

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

    /**
     * Print features in layer @layer
     * @param layer Name of the layer
     * @param borderColor Color of the border
     * @param fillColor Fill color
     */
    public void drawFeaturesOfLayer(String layer,Color borderColor, Color fillColor) {
        geotoolsMap.createAndDrawLayer(layer, borderColor, fillColor);
    }

    /**
     * Add feature as WKT to a layer
     * @param wkt area
     * @param id if of feature
     * @param layer name of the layer
     * @throws ParseException error while reading WKT
     */
    public void addProductWKT(String wkt, String id,String layer) throws ParseException {
        geotoolsMap.createFeatureFromWKT(wkt,id,layer);
    }

    private void getResetMapButton() {
        GlyphsDude.setIcon(reset,FontAwesomeIcon.EXPAND,"Reset map");
        reset.setAccessibleText("Reset map");
        reset.setOnAction(e-> geotoolsMap.resetMap());
    }

    private void getDeleteSearchAreaButton() {
        GlyphsDude.setIcon(delete,FontAwesomeIcon.ERASER,"Delete selection");
        delete.setAccessibleText("Delete selection");
        delete.setOnAction(e->{
            geotoolsMap.removeLayer("searchArea");
            searchArea = null;
        });
    }

    /**
     * Return search area as WKT
     * @return WKT of the area selected
     */
    public String getWKT() {
        return searchArea;
    }

    /**
     * Set searchArea by WKT
     * @param wkt Search area
     */
    public void setWKT(String wkt) {
        this.searchArea = wkt;
    }

    /**
     * Remove layer @layer
     * @param layer Name of the layer to remove
     */
    public void clearMap(String layer) {
        geotoolsMap.clearFeatures(layer);
        geotoolsMap.removeLayer(layer);
    }

    /**
     * GoTo layer features
     * @param layer Name of the features
     */
    public void focusOnLayer(String layer) {
        geotoolsMap.focusOnLayer(layer);
    }

    /**
     * get selected feature ID
     * @return id of the selected feature
     */
    public String getSelectedFeatureId() {
        return geotoolsMap.getSelectedFeatureID();
    }

    /**
     * Set selected features colors
     * @param borderColor Border Color
     * @param fillColor Fill color
     */
    public void setSelectedFeaturesBorderColor(Color borderColor, Color fillColor) {
        selectedFeaturesBorderColor = borderColor;
        selectedFeaturesFillColor = fillColor;
    }

    /**
     * Set not selected features colors
     * @param borderColor Border Color
     * @param fillColor Fill color
     */
    public void setNotSelectedFeaturesBorderColor(Color borderColor, Color fillColor) {
        notSelectedFeaturesBorderColor = borderColor;
        notSelectedFeaturesFillColor = fillColor;
    }
}
