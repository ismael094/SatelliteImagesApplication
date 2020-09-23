package gui;

import controller.MainAppController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import model.products.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.io.ParseException;

import java.util.List;

public class GTMapSearchController extends Pane {


    private final GTMap geotoolsMap;
    private HBox hbox;
    private boolean isSearchAreaDraw;
    private final double[] searchAreaCoordinates;
    private double baseDragedX;
    private double baseDragedY;

    static final Logger logger = LogManager.getLogger(GTMapSearchController.class.getName());

    public GTMapSearchController(double width, double height) {
        searchAreaCoordinates = new double[2];
        isSearchAreaDraw = false;
        geotoolsMap = new GTMap((int) width, (int) height);
        Pane mapPane = new Pane(geotoolsMap);
        BorderPane border = new BorderPane();
        HBox controlBar = controlBar();
        border.setTop(controlBar);
        border.setCenter(mapPane);
        addGeotoolsMapEvents();
        getChildren().add(border);
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
        addEventHandler(ScrollEvent.SCROLL, e-> {
            geotoolsMap.scroll(e.getDeltaY());
            e.consume();
        });
    }

    private void addMapMouseClickedEvent() {
        addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getClickCount() == 1) {
                geotoolsMap.selectFeature((int)(t.getX()),(int)(t.getY()-hbox.getHeight()));
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

    public HBox addBottomHBox() {
        return new HBox();
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
        hbox.getChildren().addAll(new Label("Map Controls: "),deleteSearchAreaButton,resetMap);

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


    private Button getGoToSelectionButton() {
        Button goToSelection = GlyphsDude.createIconButton(FontAwesomeIcon.COMPRESS,"Zoom selected area");
        goToSelection.setAccessibleText("Zoom selected area");

        goToSelection.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
            geotoolsMap.goToSelection();
        });
        return goToSelection;
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

    public void showProductArea(String id) {
        geotoolsMap.showFeatureArea(id);
    }

    public void clearMap() {
        geotoolsMap.clearMap();
    }

    public String getSelectedProduct() {
        return geotoolsMap.getSelectedFeatureID();

    }
}
