package gui;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.util.Duration;
import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.gml2.GML;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.geotools.xsd.Configuration;
import org.geotools.xsd.Encoder;
import org.jfree.fx.FXGraphics2D;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;


public class MapCanvas {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private MapContent map;
    private boolean repaint = true;
    private Geometry wktSquare;

    public MapCanvas(int width, int height) {
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        initMap();
        initEvent();
        initPaintThread();
        drawMap(gc);
    }

    public Node getCanvas() {
        return canvas;
    }

    public MapContent getMap() {
        return map;
    }

    private void initMap() {
        try {
            URL resource = this.getClass().getResource("/countries.shp");
            File file = new File(resource.getPath());
            FileDataStore store = FileDataStoreFinder.getDataStore(file);
            SimpleFeatureSource featureSource = store.getFeatureSource();
            map = new MapContent();
            map.setTitle("Quickstart");
            Style style = SLD.createSimpleStyle(featureSource.getSchema());
            FeatureLayer layer = new FeatureLayer(featureSource, style);
            map.addLayer(layer);
            map.getViewport().setScreenArea(new Rectangle((int) canvas.getWidth(), (int) canvas.getHeight()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void drawSquare(Coordinate[] coordinates) {

        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("MyFeatureType");
        builder.setCRS( DefaultGeographicCRS.WGS84 ); // set crs
        builder.add("location", Polygon.class); // add geometry

        // build the type
        SimpleFeatureType TYPE = builder.buildFeatureType();

        // create features using the type defined
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);

        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
        Polygon point = geometryFactory.createPolygon(coordinates);
        //LineString point = geometryFactory.createLineString(coordinates);

        WKTReader reader = new WKTReader(geometryFactory);
        try {
            wktSquare = reader.read(point.toText());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        featureBuilder.add(point);
        SimpleFeature feature = featureBuilder.buildFeature("FeaturePoint");
        List<SimpleFeature> featureCollection = new ArrayList<>();
        featureCollection.add(feature); // Add feature 1, 2, 3, etc
        Style PointStyle = SLD.createPolygonStyle(Color.RED, null,  0.5f);
        //Style PointStyle = SLD.createLineStyle(Color.RED,(float) 5);

        Layer layer = new FeatureLayer(DataUtilities.collection(featureCollection), PointStyle);
        layer.setTitle("NewPointLayer");
        List<Layer> layers = map.layers();
        if (layers.size()>1)
            map.removeLayer(layers.get(1));
        map.addLayer(layer);
        ReferencedEnvelope env = new ReferencedEnvelope(map.getViewport().getBounds());
        doSetDisplayArea(env);
    }

    public String getWKT() {
        return wktSquare == null ? "" : wktSquare.toText();
    }

    public String getGML() {
        return wktSquare == null ? "" : wktSquare.toText();
    }

    public String WKTToGML2() throws IOException, ParseException {
        WKTReader wktR = new WKTReader();
        Geometry geom = wktR.read(getGML());

        Configuration configuration = new org.geotools.gml2.GMLConfiguration();
        Encoder encoder = new Encoder( configuration );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        encoder.encode(geom, GML._Geometry,out);
        return out.toString();

    }

    public void drawMap(GraphicsContext gc) {
        if (!repaint) {
            return;
        }
        repaint = false;
        StreamingRenderer draw = new StreamingRenderer();
        draw.setMapContent(map);
        FXGraphics2D graphics = new FXGraphics2D(gc);
        graphics.setBackground(java.awt.Color.WHITE);
        graphics.clearRect(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight());
        Rectangle rectangle = new Rectangle((int) canvas.getWidth(), (int) canvas.getHeight());
        draw.paint(graphics, rectangle, map.getViewport().getBounds());
    }

    private double[] transformSceneToWorldCoordinate(double x, double y) {
        DirectPosition2D geoCoords = new DirectPosition2D(x, y);
        DirectPosition2D result = new DirectPosition2D();
        map.getViewport().getScreenToWorld().transform(geoCoords, result);
        return result.getCoordinate();
    }

    private double baseDrageX;
    private double baseDrageY;

    private double[] lineStart;
    private double[] lineEnd;
    private double sceneX;
    private double sceneY;

    private void setInitalCoordinateOfSquare(double x, double y) {
        lineStart = transformSceneToWorldCoordinate(x,y);
        sceneX = x;
        sceneY = y;
        wktSquare = null;
    }

    private void initEvent() {
        /*
         * setting the original coordinate
         */
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            baseDrageX = e.getSceneX();
            baseDrageY = e.getSceneY();
            if (e.isSecondaryButtonDown() && lineStart == null) {
                setInitalCoordinateOfSquare(e.getX(),e.getY());
            } else if(e.isSecondaryButtonDown()) {
                getCoordinatesAndDrawSquare(e.getX(),e.getY());
                resetSquare();
            }
            e.consume();
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if(e.isSecondaryButtonDown() && lineStart != null) {
                getCoordinatesAndDrawSquare(e.getX(),e.getY());
                resetSquare();
            }
            e.consume();
        });

        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            if (lineStart != null) {
                getCoordinatesAndDrawSquare(e.getX(),e.getY());
            }
            e.consume();
        });


        /*
         * translate according to the mouse drag
         */
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if (!e.isSecondaryButtonDown()) {
                dragMapEvent(e);
            } else {
                if (lineStart != null) {
                    getCoordinatesAndDrawSquare(e.getX(),e.getY());
                }
            }
            e.consume();

        });
        /*
         * double clicks to restore to original map
         */
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getClickCount() > 1) {
                doSetDisplayArea(map.getMaxBounds());
            }
            t.consume();
        });

        /*
         * scroll for zoom in and out
         */
        canvas.addEventHandler(ScrollEvent.SCROLL, e -> {
            scrollEvent(e);
        });
    }

    private void resetSquare() {
        lineStart = null;
    }

    private void dragMapEvent(MouseEvent e) {
        double difX = e.getSceneX() - baseDrageX;
        double difY = e.getSceneY() - baseDrageY;
        baseDrageX = e.getSceneX();
        baseDrageY = e.getSceneY();
        DirectPosition2D newPos = new DirectPosition2D(difX, difY);
        DirectPosition2D result = new DirectPosition2D();
        map.getViewport().getScreenToWorld().transform(newPos, result);
        ReferencedEnvelope env = new ReferencedEnvelope(map.getViewport().getBounds());
        env.translate(env.getMinimum(0) - result.x, env.getMaximum(1) - result.y);
        doSetDisplayArea(env);
    }

    private void scrollEvent(ScrollEvent e) {
        map.getViewport().setFixedBoundsOnResize(true);
        ReferencedEnvelope envelope = map.getViewport().getBounds();
        double percent = (e.getDeltaY() / canvas.getWidth())*-1;
        double width = envelope.getWidth();
        double height = envelope.getHeight();
        double deltaW = width * percent;
        double deltaH = height * percent;
        envelope.expandBy(deltaW, deltaH);
        doSetDisplayArea(envelope);
        e.consume();
    }

    private void getCoordinatesAndDrawSquare(double x, double y) {
        Coordinate[] coordinates = getSquareCoordinates(x,y);
        drawSquare(coordinates);
    }

    private Coordinate[] getSquareCoordinates(double x, double y) {
        lineEnd = transformSceneToWorldCoordinate(x,y);
        double diffX = x - sceneX;
        double diffY = y - sceneY;
        double[] upRightCorner = transformSceneToWorldCoordinate(sceneX+diffX,sceneY);
        double[] downLeftCorner = transformSceneToWorldCoordinate(sceneX,sceneY+diffY);
        return new Coordinate[] {
                new Coordinate(lineStart[0],lineStart[1]),
                new Coordinate(upRightCorner[0],upRightCorner[1]),
                new Coordinate(lineEnd[0],lineEnd[1]),
                new Coordinate(downLeftCorner[0],downLeftCorner[1]),
                new Coordinate(lineStart[0],lineStart[1])
        };
    }

    protected void doSetDisplayArea(ReferencedEnvelope envelope) {
        map.getViewport().setBounds(envelope);
        repaint = true;
    }

    private static final double PAINT_HZ = 50.0;
    private void initPaintThread() {
        ScheduledService<Boolean> svc = new ScheduledService<Boolean>() {
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    protected Boolean call() {
                        Platform.runLater(() -> {
                            drawMap(gc);
                        });
                        return true;
                    }
                };
            }
        };
        svc.setPeriod(Duration.millis(1000.0 / PAINT_HZ));
        svc.start();
    }

    public void deleteSquare() {
        List<Layer> layers = map.layers();
        if (layers.size()>1)
            map.removeLayer(layers.get(1));
        ReferencedEnvelope env = new ReferencedEnvelope(map.getViewport().getBounds());
        doSetDisplayArea(env);
    }

    public void resetMap() {
        doSetDisplayArea(map.layers().get(0).getBounds());
    }

    public void goToSelection() {
        doSetDisplayArea(map.getMaxBounds());
    }
}
