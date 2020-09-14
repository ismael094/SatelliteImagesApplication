package gui;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.locationtech.jts.io.ParseException;

import java.io.IOException;

public class MapGUI extends Pane {


    private final MapCanvas canvas;

    public MapGUI() {
        this.canvas = new MapCanvas(418, 374);

        Pane pane = new	Pane(canvas.getCanvas());


        BorderPane border = new BorderPane();
        HBox hbox = addHBox(canvas);
        border.setTop(hbox);
        border.setCenter(pane);
        border.setBottom(addBottomHBox(canvas));
        Scene scene = new Scene(border);

        getChildren().add(border);
    }

    public HBox addBottomHBox(MapCanvas canvas) {
        HBox hbox = new HBox();
        TextField l = new TextField("");
        Button buttonCurrent = new Button("Get WKT");

        buttonCurrent.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
            if (canvas.getWKT().length() == 0) {
                l.setText("No location setted");
            } else {
                try {
                    l.setText(canvas.WKTToGML2());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
            }
        });

        hbox.getChildren().addAll(buttonCurrent, l);

        return hbox;
    }

    public HBox addHBox(MapCanvas canvas) {
        HBox hbox = new HBox();
        /*hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");*/
        //buttonCurrent.setPrefSize(100, 20);

        Button iconButton = GlyphsDude.createIconButton(FontAwesomeIcon.ERASER,"Clear");
        iconButton.setAccessibleText("Clear map");
        Button resetMap = GlyphsDude.createIconButton(FontAwesomeIcon.EXPAND,"Reset");
        resetMap.setAccessibleText("Reset map");
        Button goToSelection = GlyphsDude.createIconButton(FontAwesomeIcon.COMPRESS,"Selection");
        goToSelection.setAccessibleText("Zoom selection");

        goToSelection.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
            canvas.goToSelection();
        });

        resetMap.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
            canvas.resetMap();
        });

        iconButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e->{
            canvas.deleteSquare();
        });

        hbox.setSpacing(5f);
        hbox.getChildren().addAll(iconButton,resetMap,goToSelection);

        return hbox;
    }

    public String getWKT() {
        return canvas.getWKT();
    }
}
