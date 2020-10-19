package controller;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gui.GTMap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
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

    static final Logger logger = LogManager.getLogger(GTMapSearchController.class.getName());
    private final BooleanProperty wasPrimaryButtonClicked;
    private final BooleanProperty wasSecondaryButtonClicked;

    private Color selectedFeaturesBorderColor;
    private Color selectedFeaturesFillColor;
    private Color notSelectedFeaturesBorderColor;
    private Color notSelectedFeaturesFillColor;
    private boolean dragged;
    private String searchArea;
    private String selectedLayerEvent;

    public GTMapSearchController(double width, double height, boolean showControls) {

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
        System.out.println(mapContainer.getWidth());
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

    public GTMap getMap() {
        return geotoolsMap;
    }

    public HBox controlBar() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);




        hbox.setSpacing(10f);
        //hbox.getChildren().addAll(deleteSearchAreaButton,resetMap);

        return hbox;
    }

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

    public void showProductArea(List<String> ids,String layer) {
        geotoolsMap.highlightFeatures(ids, layer, selectedFeaturesBorderColor, selectedFeaturesFillColor, notSelectedFeaturesBorderColor, notSelectedFeaturesFillColor);
    }

    private void createSearchArea(Point2D end) {
        geotoolsMap.createLayerFromCoordinates(initialCoordinates,end,"searchArea");
        try {
            searchArea = geotoolsMap.getWKTFromCoordinates(initialCoordinates,end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

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

    public String getWKT() {
        return searchArea;
    }

    public void clearMap(String layer) {
        geotoolsMap.clearFeatures(layer);
    }

    public void focusOnLayer(String layer) {
        geotoolsMap.focusOnLayer(layer);
    }

    public String getSelectedFeatureId() {
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
