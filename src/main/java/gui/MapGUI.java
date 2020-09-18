package gui;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import model.products.Product;
import org.locationtech.jts.io.ParseException;

import java.io.IOException;
import java.util.List;

public class MapGUI extends Pane {


    private final MapCanvas canvas;

    public MapGUI(double width, double height) {
        this.canvas = new MapCanvas((int) width, (int) height);
        Pane mapPane = new	Pane(canvas.getCanvas());
        BorderPane border = new BorderPane();
        HBox controlBar = controlBar(canvas);
        border.setTop(controlBar);
        border.setCenter(mapPane);
        border.setBottom(addBottomHBox(canvas));
        Scene scene = new Scene(border);

        getChildren().add(border);
    }

    public void addProductsWKT(List<Product> products) {
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

    public HBox addBottomHBox(MapCanvas canvas) {
        return new HBox();
    }

    public HBox controlBar(MapCanvas canvas) {
        HBox hbox = new HBox();
        /*hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");*/
        //buttonCurrent.setPrefSize(100, 20);

        Button clearButton = getClearButton();
        Button goToSelection = getGoToSelectionButton();
        Button resetMap = getResetMapButton();


        hbox.setSpacing(5f);
        hbox.getChildren().addAll(clearButton,resetMap,goToSelection);

        return hbox;
    }


    private Button getResetMapButton() {
        Button resetMap = GlyphsDude.createIconButton(FontAwesomeIcon.EXPAND,"Reset");
        resetMap.setAccessibleText("Reset map");
        resetMap.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
            canvas.resetMap();
        });
        return resetMap;
    }


    private Button getGoToSelectionButton() {
        Button goToSelection = GlyphsDude.createIconButton(FontAwesomeIcon.COMPRESS,"Selection");
        goToSelection.setAccessibleText("Zoom selection");

        goToSelection.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
            canvas.goToSelection();
        });
        return goToSelection;
    }

    private Button getClearButton() {
        Button clearButton = GlyphsDude.createIconButton(FontAwesomeIcon.ERASER,"Clear");
        clearButton.setAccessibleText("Clear map");
        clearButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
            canvas.deleteSquare();
        });
        return clearButton;
    }

    public String getWKT() {
        return canvas.getWKT();
    }

    public void showProductArea(String id) {
        try {
            canvas.showProductArea(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearMap() {
        canvas.clearMap();
    }
}
