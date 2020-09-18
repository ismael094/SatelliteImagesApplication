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
import org.apache.commons.collections.ArrayStack;
import org.geotools.data.DataUtilities;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.*;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.SLD;
import org.geotools.styling.Style;
import org.jfree.fx.FXGraphics2D;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;


public class MapCanvas {
    public static final String SEARCH_AREA = "SearchArea";
    private final Canvas geotoolsCanvas;
    private final GraphicsContext graphicsContext;
    private List<SimpleFeature> featureCollection;
    private MapContent mapContent;
    private boolean repaint = true;
    private Geometry wktSquare;
    private FeatureLayer selectedLayer;

    public MapCanvas(int width, int height) {
        geotoolsCanvas = new Canvas(width, height);
        graphicsContext = geotoolsCanvas.getGraphicsContext2D();
        initMap();
        initEvent();
        initPaintThread();
        drawMap(graphicsContext);
    }

    public Node getCanvas() {
        return geotoolsCanvas;
    }

    private void initMap() {
        try {
            SimpleFeatureSource featureSource = loadFileDataStore("/countries.shp").getFeatureSource();

            Style style = SLD.createSimpleStyle(featureSource.getSchema());
            FeatureLayer layer = new FeatureLayer(featureSource, style);

            mapContent = new MapContent();
            mapContent.setTitle("Geotool map");
            mapContent.addLayer(layer);
            mapContent.getViewport().setScreenArea(new Rectangle((int) geotoolsCanvas.getWidth(), (int) geotoolsCanvas.getHeight()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private FileDataStore loadFileDataStore(String url) throws IOException {
        URL resource = this.getClass().getResource(url);
        File file = new File(resource.getPath());
        return FileDataStoreFinder.getDataStore(file);
    }

    public void drawGeometryFromWKT(String wktCoordinates, String id) throws ParseException {

        SimpleFeatureBuilder featureBuilder;
        if (wktCoordinates.contains("MULTIPOLYGON"))
            featureBuilder = getSimpleFeatureBuilder(MultiPolygon.class);
        else
            featureBuilder = getSimpleFeatureBuilder(Polygon.class);

        featureBuilder.add(readWKTString(wktCoordinates));
        if (featureCollection == null) {
            System.out.println("Created layer");
            featureCollection = new ArrayList<>();
            //createProductResultsLayer(Color.BLACK,null,1f);
        }
        featureCollection.add(featureBuilder.buildFeature(id));
        //doSetDisplayArea(new ReferencedEnvelope(mapContent.getViewport().getBounds()));
    }

    public void showProductArea(String id) throws IOException {
        //if (selectedLayer != null)
            //selectedLayer.setStyle(getPolygonStyle(Color.BLACK,null,1f));
        selectedLayer = (FeatureLayer) mapContent.layers().stream()
                .filter(l -> l.getTitle() != null && l.getTitle().equals("ResultsLayer"))
                .findFirst().orElse(null);
        if (selectedLayer!=null) {
            Object[] features =  ((Layer) selectedLayer).getFeatureSource().getFeatures().toArray();
            SimpleFeature sF = (SimpleFeature)features[0];
            System.out.println(sF.getID());
            /*Arrays.stream(features)
                    .filter(f -> f.getID().equals(id))
                    .findFirst().ifPresent(simpleFeature -> System.out.println("ESTÁ"));*/
            selectedLayer.setStyle(getPolygonStyle(Color.RED,Color.ORANGE,1f));
        }

        doSetDisplayArea(new ReferencedEnvelope(mapContent.getViewport().getBounds()));
    }

    public void drawGeometryFromCoordinates(Coordinate[] coordinates) throws ParseException {

        SimpleFeatureBuilder featureBuilder = getSimpleFeatureBuilder(Polygon.class);
        Polygon polygon = JTSFactoryFinder.getGeometryFactory().createPolygon(coordinates);
        wktSquare = readWKTString(polygon.toText());
        featureBuilder.add(wktSquare);

        removeSearchAreaLayer(mapContent.layers());

        addFeatureToMapContent(featureBuilder,Color.RED,null,0.5f, SEARCH_AREA);

        doSetDisplayArea(new ReferencedEnvelope(mapContent.getViewport().getBounds()));
    }

    public void createAndDrawProductsLayer() {
        Layer resultsLayer = createLayer(featureCollection,
                getPolygonStyle(Color.BLACK, null, 0.5f), "ResultsLayer");
        FeatureLayer f = (FeatureLayer)resultsLayer;
        mapContent.addLayer(resultsLayer);
        doSetDisplayArea(new ReferencedEnvelope(mapContent.getViewport().getBounds()));
    }

    private void addFeatureToMapContent(SimpleFeatureBuilder featureBuilder, Color outlineColor, Color fillColor, float opacity, String title) {
        List<SimpleFeature> collection = new ArrayList<>();
        collection.add(featureBuilder.buildFeature("Search"));
        Layer searchAreaLayer = createLayer(collection, getPolygonStyle(outlineColor, fillColor, opacity), title);
        mapContent.addLayer(searchAreaLayer);
    }

    private Layer createLayer(List<SimpleFeature> featureCollection, Style pointStyle, String title) {
        Layer layer = new FeatureLayer(DataUtilities.collection(featureCollection), pointStyle);
        layer.setTitle(title);
        return layer;
    }

    private Geometry readWKTString(String wktCoordinates) throws ParseException {
        return new WKTReader(JTSFactoryFinder.getGeometryFactory()).read(wktCoordinates);

    }

    private Style getPolygonStyle(Color outlineColor, Color fillColor, float opacity) {
        return SLD.createPolygonStyle(outlineColor,fillColor, opacity);
    }

    private void removeSearchAreaLayer(List<Layer> layers) {
        layers.stream()
                .filter(l -> l.getTitle() != null && l.getTitle().equals(SEARCH_AREA))
                .findFirst().ifPresent(l -> mapContent.removeLayer(l));
    }


    private SimpleFeatureBuilder getSimpleFeatureBuilder(Class geometry) {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("MyFeatureType");
        builder.setCRS( DefaultGeographicCRS.WGS84 ); // set crs
        builder.add("location", geometry); // add geometry
        return new SimpleFeatureBuilder(builder.buildFeatureType());
    }

    public String getWKT() {
        return wktSquare == null ? "" : wktSquare.toText();
    }

    public void drawMap(GraphicsContext gc) {
        if (!repaint) {
            return;
        }
        repaint = false;
        StreamingRenderer draw = new StreamingRenderer();
        draw.setMapContent(mapContent);
        FXGraphics2D graphics = new FXGraphics2D(gc);
        graphics.setBackground(java.awt.Color.WHITE);
        graphics.clearRect(0, 0, (int) geotoolsCanvas.getWidth(), (int) geotoolsCanvas.getHeight());
        Rectangle rectangle = new Rectangle((int) geotoolsCanvas.getWidth(), (int) geotoolsCanvas.getHeight());
        draw.paint(graphics, rectangle, mapContent.getViewport().getBounds());
    }

    private double[] transformSceneToWorldCoordinate(double x, double y) {
        DirectPosition2D geoCoords = new DirectPosition2D(x, y);
        DirectPosition2D result = new DirectPosition2D();
        mapContent.getViewport().getScreenToWorld().transform(geoCoords, result);
        return result.getCoordinate();
    }

    private double baseDrageX;
    private double baseDrageY;

    private double[] lineStart;
    private double[] lineEnd;
    private double sceneX;
    private double sceneY;

    private void setInitialCoordinateOfSquare(double x, double y) {
        lineStart = transformSceneToWorldCoordinate(x,y);
        sceneX = x;
        sceneY = y;
        wktSquare = null;
    }

    private void initEvent() {
        mousePressedEvent();

        mouseReleasedEvent();

        mouseMovedEvent();

        mouseDraggedEvent();

        mouseClickedEvent();

        scrollEvent();
    }

    private void scrollEvent() {
        geotoolsCanvas.addEventHandler(ScrollEvent.SCROLL, this::scrollEvent);
    }

    private void mouseClickedEvent() {
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
        geotoolsCanvas.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {

            String geometryAttributeName = null;
            if (t.getClickCount() > 1) {
                /*Rectangle screenRect = new Rectangle((int)(t.getX() - 2), (int)(t.getY() - 2), 5, 5);
                AffineTransform screenToWorld = mapContent.getViewport().getScreenToWorld();
                Rectangle2D worldRect = screenToWorld.createTransformedShape(screenRect).getBounds2D();
                ReferencedEnvelope bbox =
                        new ReferencedEnvelope(
                                worldRect, mapContent.getCoordinateReferenceSystem());
                Filter filter = ff.intersects(ff.property(""), ff.literal(bbox));
                mapContent.layers()
                        .forEach(l-> {
                            try {
                                l.getFeatureSource().getFeatures(filter).;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                try {
                    SimpleFeatureCollection selectedFeatures = mapContent.

                    Set<FeatureId> IDs = new HashSet<>();
                    try (SimpleFeatureIterator iter = selectedFeatures.features()) {
                        while (iter.hasNext()) {
                            SimpleFeature feature = iter.next();
                            IDs.add(feature.getIdentifier());

                            System.out.println("   " + feature.getIdentifier());
                        }
                    }

                    if (IDs.isEmpty()) {
                        System.out.println("   no feature selected");
                    }

                    displaySelectedFeatures(IDs);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }*/
                doSetDisplayArea(mapContent.getMaxBounds());
            }
            t.consume();
        });
    }

    private void mouseDraggedEvent() {
        geotoolsCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if (!e.isSecondaryButtonDown()) {
                dragMapEvent(e);
            } else {
                if (lineStart != null) {
                    getCoordinatesAndDrawSquare(e.getX(),e.getY());
                }
            }
            e.consume();

        });
    }

    private void mouseMovedEvent() {
        geotoolsCanvas.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            if (lineStart != null) {
                getCoordinatesAndDrawSquare(e.getX(),e.getY());
            }
            e.consume();
        });
    }

    private void mouseReleasedEvent() {
        geotoolsCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if(e.isSecondaryButtonDown() && lineStart != null) {
                getCoordinatesAndDrawSquare(e.getX(),e.getY());
                resetSquare();
            }
            e.consume();
        });
    }

    private void mousePressedEvent() {
        geotoolsCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            baseDrageX = e.getSceneX();
            baseDrageY = e.getSceneY();
            if (e.isSecondaryButtonDown() && lineStart == null) {
                setInitialCoordinateOfSquare(e.getX(),e.getY());
            } else if(e.isSecondaryButtonDown()) {
                getCoordinatesAndDrawSquare(e.getX(),e.getY());
                resetSquare();
            }
            e.consume();
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
        mapContent.getViewport().getScreenToWorld().transform(newPos, result);
        ReferencedEnvelope env = new ReferencedEnvelope(mapContent.getViewport().getBounds());
        env.translate(env.getMinimum(0) - result.x, env.getMaximum(1) - result.y);
        doSetDisplayArea(env);
    }

    private void scrollEvent(ScrollEvent e) {
        mapContent.getViewport().setFixedBoundsOnResize(true);
        ReferencedEnvelope envelope = mapContent.getViewport().getBounds();
        double percent = (e.getDeltaY() / geotoolsCanvas.getWidth())*-1;
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
        try {
            drawGeometryFromCoordinates(coordinates);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        mapContent.getViewport().setBounds(envelope);
        repaint = true;
    }

    private static final double PAINT_HZ = 50.0;
    private void initPaintThread() {
        ScheduledService<Boolean> svc = new ScheduledService<>() {
            protected Task<Boolean> createTask() {
                return new Task<>() {
                    protected Boolean call() {
                        Platform.runLater(() -> drawMap(graphicsContext));
                        return true;
                    }
                };
            }
        };
        svc.setPeriod(Duration.millis(1000.0 / PAINT_HZ));
        svc.start();
    }

    public void deleteSquare() {
        List<Layer> layers = mapContent.layers();
        if (layers.size()>1)
            mapContent.removeLayer(layers.get(1));
        ReferencedEnvelope env = new ReferencedEnvelope(mapContent.getViewport().getBounds());
        doSetDisplayArea(env);
    }

    public void resetMap() {
        doSetDisplayArea(mapContent.layers().get(0).getBounds());
    }

    public void goToSelection() {
        doSetDisplayArea(mapContent.getMaxBounds());
    }


    public void clearMap() {
        List<Layer> layers = mapContent.layers();
        if (layers.size()>2)
            layers.stream()
                .filter(Objects::nonNull)
                .filter(l->l.getTitle()!=null && !l.getTitle().equals(SEARCH_AREA))
                .forEach(layers::remove);
    }
}
