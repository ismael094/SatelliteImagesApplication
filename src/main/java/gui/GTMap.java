package gui;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.data.DataUtilities;
import org.geotools.data.FeatureLockException;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.*;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.*;
import org.geotools.styling.Stroke;
import org.geotools.tile.impl.osm.OSMService;
import org.geotools.tile.util.AsyncTileLayer;
import org.jfree.fx.FXGraphics2D;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class GTMap extends Canvas {
    private static final double PAINT_HZ = 500;
    private final GTRenderer draw;
    private final GraphicsContext graphicsContext;
    private final FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
    private final StyleFactory sf = CommonFactoryFinder.getStyleFactory();

    private boolean repaint = true;
    private final List<FeatureIdImpl> selectedFeatures;
    private MapContent mapContent;
    private String selectedFeatureID;
    private final Map<String,List<SimpleFeature>> layers;

    static final Logger logger = LogManager.getLogger(GTMap.class.getName());


    public GTMap(int width, int height, boolean oms) {
        super(width,height);
        layers = new HashMap<>();
        graphicsContext = getGraphicsContext2D();
        draw = new StreamingRenderer();
        selectedFeatures = new ArrayList<>();
        if (oms)
            initOMSMap();
        else
            initMapFromFile();
        initPaintThread();
        drawMap(graphicsContext);
    }

    private void initMapFromFile() {
        String url = "/maps/bathymetry.shp";
        mapContent = new MapContent();
        mapContent.setTitle("Geotool map");
        SimpleFeatureSource featureSource;
        try {
            featureSource = loadFileDataStore(url).getFeatureSource();
        } catch (IOException e) {
            logger.atError().log("Error loading {}: {}",url,e);
            return;
        }

        Style style = SLD.createSimpleStyle(featureSource.getSchema());
        FeatureLayer layer = new FeatureLayer(featureSource, style);
        layer.setTitle("MapLayer");
        mapContent.addLayer(layer);
        setScreenArea();
    }

    private void initOMSMap() {
        mapContent = new MapContent();
        mapContent.setTitle("Geotool map");
        OSMService service = new OSMService("OMS", "http://tile.openstreetmap.org/");
        AsyncTileLayer asyncTileLayer = new AsyncTileLayer(service);
        asyncTileLayer.setTitle("MapLayer");
        mapContent.addLayer(asyncTileLayer);
        setScreenArea();
    }

    private void setScreenArea() {
        mapContent.getViewport().setScreenArea(new Rectangle((int) getWidth(), (int) getHeight()));
    }

    private FileDataStore loadFileDataStore(String url) throws IOException {
        URL resource = this.getClass().getResource(url);
        File file = new File(resource.getPath());
        return FileDataStoreFinder.getDataStore(file);
    }

    /**
     * Add new feature to layer @layer
     * @param wktCoordinates WKT string with the area to draw
     * @param id Id of the feature to identified it
     * @param layer Name of the layer to draw the feature
     * @throws ParseException Error reading the WKT String
     */
    public void createFeatureFromWKT(String wktCoordinates, String id, String layer) throws ParseException {

        SimpleFeatureBuilder featureBuilder;
        if (wktCoordinates.contains("MULTIPOLYGON"))
            featureBuilder = getSimpleFeatureBuilder(MultiPolygon.class);
        else
            featureBuilder = getSimpleFeatureBuilder(Polygon.class);

        featureBuilder.add(readWKTString(wktCoordinates));
        if (layers.getOrDefault(layer,null) == null) {
            layers.put(layer,new ArrayList<>());
            System.out.println("Creating layer "+layer);
        }
        layers.get(layer).add(featureBuilder.buildFeature(id));
    }

    /**
     * Clear feature of one layer
     * @param layer Name of the layer to clear the features
     */
    public void clearFeatures(String layer) {
        List<SimpleFeature> simpleFeatures = layers.getOrDefault(layer,null);

        if (simpleFeatures != null)
            simpleFeatures.clear();
    }

    /**
     * Highlight features in a layer using its ID
     * @param ids IDs of the feature to highlight
     * @param layerName Name of the layer which contain the features
     * @param selectedBorderColor Border Color of the selected features
     * @param selectedFillColor Fill Color of the selected features
     * @param notSelectedBorderColor Border Color of the not selected features
     * @param notSelectedFillColor Fill Color of the not selected features
     */
    public void highlightFeatures(List<String> ids, String layerName, Color selectedBorderColor, Color selectedFillColor, Color notSelectedBorderColor, Color notSelectedFillColor)  {
        FeatureLayer layer = (FeatureLayer) getLayerByName(layerName);
        if (layer == null)
            return;

        List<FeatureIdImpl> collect = ids.stream().map(FeatureIdImpl::new).collect(Collectors.toList());

        Rule selectedRule = createRule(selectedBorderColor,selectedFillColor, 3f, 0.1f, getLayerGeometryAttribute(layer));
        selectedRule.setFilter(ff.id(new HashSet<>(collect)));

        Rule notSelectedRule = createRule(notSelectedBorderColor, notSelectedFillColor,1f,1f,getLayerGeometryAttribute(layer));
        notSelectedRule.setElseFilter(true);

        Style selectedStyle = createSelectedFeatureStyle(collect, selectedRule, notSelectedRule);
        layer.setStyle(selectedStyle);
        refresh();
    }

    /**
     * Draw in map a layer and all its features
     * @param layer Name of the layer
     * @param borderColor Border color of the features
     * @param fillColor Fill color of the features
     */
    public void createAndDrawLayer(String layer, Color borderColor, Color fillColor) {
        removeLayer(layer);
        Layer resultsLayer = createLayer(layers.getOrDefault(layer, new ArrayList<>()),
                getPolygonStyle(borderColor, fillColor, 0.5f), layer);
        mapContent.addLayer(resultsLayer);
        refresh();
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

    private SimpleFeatureBuilder getSimpleFeatureBuilder(Class geometry) {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("MyFeatureType");
        builder.setCRS( DefaultGeographicCRS.WGS84 ); // set crs
        builder.add("location", geometry); // add geometry
        return new SimpleFeatureBuilder(builder.buildFeatureType());
    }

    /**
     * Draw map, all layers and features
     * @param gc GraphicContext
     */
    public void drawMap(GraphicsContext gc) {
        if (!repaint) {
            return;
        }
        repaint = false;
        draw.setMapContent(mapContent);
        FXGraphics2D graphics = new FXGraphics2D(gc);
        graphics.setBackground(java.awt.Color.WHITE);
        graphics.clearRect(0, 0, (int) getWidth(), (int) getHeight());
        Rectangle rectangle = new Rectangle((int) getWidth(), (int) getHeight());
        try {
            draw.paint(graphics, rectangle, mapContent.getViewport().getBounds());
        } catch (java.lang.NullPointerException ex) {
            resetMap();
        }

    }

    /**
     * Remove layer
     * @param name Name of the layer to remove
     */
    public void removeLayer(String name) {
        mapContent.layers().remove(getLayerByName(name));
        refresh();
    }

    /**
     * Refresh map and all his layers
     */
    public void refresh() {
        doSetDisplayArea(new ReferencedEnvelope(mapContent.getViewport().getBounds()));
    }

    /**
     * Select features contained in a point
     * @param point Coordinates
     * @param multipleSelection Allow to select more than one features
     * @param layer Name of the layer to get features
     * @param selectedBorderColor Border color of selected features
     * @param selectedFillColor Border color of selected features
     * @param notSelectedBorderColor Border color of not selected features
     * @param notSelectedFillColor Fill color of not selected features
     * @throws IOException Error while finding features
     */
    public void selectFeature(Point2D point, boolean multipleSelection, String layer, Color selectedBorderColor, Color selectedFillColor, Color notSelectedBorderColor, Color notSelectedFillColor) throws IOException {
        //Find layer
        FeatureLayer featureLayer = (FeatureLayer) getLayerByName(layer);

        if (featureLayer == null)
            return;

        //Find features by coordinates
        FeatureIdImpl featureId = (FeatureIdImpl) findFeatureIdByCoordinates((int)point.getX(), (int)point.getY(), featureLayer);

        //If multiple option is false, clear selectedFeatures list
        if (!multipleSelection || featureId == null)
            selectedFeatures.clear();

        //If feature already in the list, remove it
        if (featureId != null && !selectedFeatures.contains(featureId))
            selectedFeatures.add(featureId);
        else if (featureId!=null) {
            selectedFeatures.remove(featureId);
        }

        //Rule to features selected
        Rule selectedRule = createRule(selectedBorderColor,selectedFillColor, 3f, 0.5f, getLayerGeometryAttribute(featureLayer));
        selectedRule.setFilter(ff.id(new HashSet<>(selectedFeatures)));

        //Rule to not selected features
        Rule notSelectedRule = createRule(notSelectedBorderColor, notSelectedFillColor,1f,0.5f,getLayerGeometryAttribute(featureLayer));
        notSelectedRule.setElseFilter(true);

        //Style layer
        Style selectedStyle = createSelectedFeatureStyle(selectedFeatures, selectedRule, notSelectedRule);
        featureLayer.setStyle(selectedStyle);

        if (featureId == null)
            throw new FeatureLockException();

    }

    private FeatureId findFeatureIdByCoordinates(int x, int y, FeatureLayer featureLayer) throws IOException {
        Filter filter = ff.intersects(ff.property(getLayerGeometryAttribute(featureLayer)), ff.literal(getMousePointReferencedEnvelope(x, y)));
        SimpleFeatureCollection features = featureLayer.getSimpleFeatureSource().getFeatures(filter);
        FeatureId featureId = null;
        SimpleFeatureIterator featureIterator = features.features();

        if (featureIterator.hasNext()) {
            SimpleFeature feature = featureIterator.next();
            featureId = feature.getIdentifier();
        }
        featureIterator.close();

        selectedFeatureID = null;
        if (featureId != null)
            selectedFeatureID = featureId.getID();
        return featureId;
    }

    private String getLayerGeometryAttribute(FeatureLayer featureLayer) {
        return featureLayer.getSimpleFeatureSource().getSchema().getGeometryDescriptor().getLocalName();
    }

    private ReferencedEnvelope getMousePointReferencedEnvelope(int x, int y) {
        AffineTransform screenToWorld = mapContent.getViewport().getScreenToWorld();
        Rectangle2D worldRect = screenToWorld.createTransformedShape(
                new Rectangle((x - 2), (y - 2), 5, 5)).getBounds2D();
        return new ReferencedEnvelope(
                worldRect, mapContent.getCoordinateReferenceSystem());
    }

    private Style createSelectedFeatureStyle(List<FeatureIdImpl> ids, Rule selectedRule,  Rule notSelectedRule) {
        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        if (ids.size() > 0)
            fts.rules().add(selectedRule);
        fts.rules().add(notSelectedRule);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }

    private Rule createRule(Color outlineColor, Color fillColor, float width, float opacity, String geometryAttributeName) {
        Stroke stroke = sf.createStroke(ff.literal(outlineColor), ff.literal(width));
        Fill fill = sf.createFill(ff.literal(fillColor), ff.literal(opacity));
        Symbolizer symbolizer = sf.createPolygonSymbolizer(stroke, fill, geometryAttributeName);

        Rule rule = sf.createRule();
        rule.symbolizers().add(symbolizer);
        return rule;
    }

    public void dragMap(double baseDrageX, double baseDrageY, double drageX, double drageY) {
        double difX = drageX - baseDrageX;
        double difY = drageY - baseDrageY;
        DirectPosition2D newPos = new DirectPosition2D(difX, difY);
        DirectPosition2D result = new DirectPosition2D();
        mapContent.getViewport().getScreenToWorld().transform(newPos, result);
        ReferencedEnvelope env = new ReferencedEnvelope(mapContent.getViewport().getBounds());
        env.translate(env.getMinimum(0) - result.x, env.getMaximum(1) - result.y);
        doSetDisplayArea(env);
    }

    public void scroll(double deltaY) {
        //mapContent.getViewport().f(true);
        ReferencedEnvelope envelope = mapContent.getViewport().getBounds();
        double percent = (deltaY / getWidth())*-1;
        double deltaW = envelope.getWidth() * percent;
        double deltaH = envelope.getHeight() * percent;
        envelope.expandBy(deltaW, deltaH);
        doSetDisplayArea(envelope);
    }

    public String getWKTFromCoordinates(Point2D initial, Point2D end) throws ParseException {
        Coordinate[] squareCoordinates = getSquareCoordinates(initial.getX(), initial.getY(), end.getX(), end.getY());
        return getGeometryFromCoordinates(squareCoordinates).toText();
    }

    private Geometry getGeometryFromCoordinates(Coordinate[] coordinates) throws ParseException {
        Polygon polygon = JTSFactoryFinder.getGeometryFactory().createPolygon(coordinates);
        return readWKTString(polygon.toText());
    }

    public void createLayerFromCoordinates(Point2D initial, Point2D end, String layerName) {
        try {
            drawPolygonFromCoordinates(getSquareCoordinates(initial.getX(), initial.getY(), end.getX(), end.getY()),layerName);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void drawPolygonFromCoordinates(Coordinate[] coordinates, String layerName) throws ParseException {

        SimpleFeatureBuilder featureBuilder = getSimpleFeatureBuilder(Polygon.class);
        Polygon polygon = JTSFactoryFinder.getGeometryFactory().createPolygon(coordinates);
        featureBuilder.add(getGeometryFromCoordinates(coordinates));

        removeLayer(layerName);

        addFeatureToMapContent(featureBuilder,Color.RED,Color.RED,0.5f, layerName);
        refresh();
    }

    private Coordinate[] getSquareCoordinates(double initX, double initY, double endX, double endY) {
        //double[] startCoordinates = transformSceneToWorldCoordinate(initX, initY);
        //double[] endCoordinates = transformSceneToWorldCoordinate(endX, endY);
        double diffX = endX - initX;
        double diffY = endY - initY;
        double[] upRightCorner = transformSceneToWorldCoordinate(initX+diffX,initY);
        double[] downLeftCorner = transformSceneToWorldCoordinate(initX,initY+diffY);
        return new Coordinate[] {
                new Coordinate(initX,initY),
                new Coordinate(initX+diffX,initY),
                new Coordinate(endX,endY),
                new Coordinate(initX,initY+diffY),
                new Coordinate(initX,initY)
        };
    }

    public double[] transformSceneToWorldCoordinate(double x, double y) {
        DirectPosition2D geoCoords = new DirectPosition2D(x, y);
        DirectPosition2D result = new DirectPosition2D();
        mapContent.getViewport().getScreenToWorld().transform(geoCoords, result);
        return result.getCoordinate();
    }

    protected void doSetDisplayArea(ReferencedEnvelope envelope) {
        mapContent.getViewport().setBounds(envelope);
        repaint = true;
    }


    private void initPaintThread() {
        logger.atInfo().log("Init map refresh thread");
        ScheduledService<Boolean> svc = new ScheduledService<Boolean>() {
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
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

    public void focusOnLayer(String layer) {
        Layer ly = getLayerByName(layer);
        if (ly != null) {
            int i = mapContent.layers().indexOf(ly);
            doSetDisplayArea(mapContent.layers().get(i).getBounds());
        }
    }

    public void resetMap() {
        doSetDisplayArea(mapContent.layers().get(0).getBounds());
    }

    public void goToSelection() {
        doSetDisplayArea(mapContent.getMaxBounds());
    }


    public void clearMap(String layerName) {
        List<Layer> layers = mapContent.layers();
            layers.stream()
                .filter(Objects::nonNull)
                .filter(l->l.getTitle()!=null && l.getTitle().equals(layerName))
                .forEach(layers::remove);
    }

    private Layer getLayerByName(String name) {
        return mapContent.layers().stream()
                .filter(l -> l.getTitle().equals(name))
                .findAny()
                .orElse(null);
    }

    public String getSelectedFeatureID() {
        return selectedFeatureID;
    }
}
