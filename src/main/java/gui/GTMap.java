package gui;

import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.AbstractGridFormat;
import org.geotools.coverage.grid.io.GridFormatFinder;
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
import org.geotools.ows.ServiceException;
import org.geotools.ows.wms.WebMapServer;
import org.geotools.ows.wms.map.WMSLayer;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.geotools.styling.*;
import org.geotools.styling.Stroke;
import org.geotools.swing.wms.WMSLayerChooser;
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
import org.opengis.style.ContrastMethod;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class GTMap extends Canvas {
    private static final double PAINT_HZ = 500;
    private static final String SEARCH_AREA = "SearchArea";
    private static final String RESULTS_LAYER_TITLE = "ResultsLayer";
    private final GTRenderer draw;
    private final GraphicsContext graphicsContext;
    private final FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
    private final StyleFactory sf = CommonFactoryFinder.getStyleFactory();

    static final Logger logger = LogManager.getLogger(GTMap.class.getName());

    private boolean repaint = true;
    private List<SimpleFeature> featureCollection;
    private final List<FeatureIdImpl> selectedFeatures;
    private MapContent mapContent;
    private Geometry searchAreaWKT;
    private String selectedFeatureID;
    private AbstractGridCoverage2DReader reader;



    public GTMap(int width, int height,boolean oms) {
        super(width, height);
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

    private Style createRGBStyle(File f) throws IOException {
        AbstractGridFormat format = GridFormatFinder.findFormat( f );
        reader = format.getReader(f);
        GridCoverage2D cov;
        cov = reader.read(null);
        SelectedChannelType[] sct = new SelectedChannelType[cov.getNumSampleDimensions()];
        ContrastEnhancement ce = sf.contrastEnhancement(ff.literal(1.0), ContrastMethod.HISTOGRAM);
        IntStream.range(0, 3).forEach(i -> sct[i] = sf.createSelectedChannelType(String.valueOf(i + 1), ce));
        RasterSymbolizer sym = sf.getDefaultRasterSymbolizer();
        sym.setChannelSelection(sf.channelSelection(sct[0], sct[1], sct[2]));
        return SLD.wrapSymbolizers(sym);
    }

    private void wms() {
        try {
            URL capabilitiesURL = new URL("https://tiles.maps.eox.at/wms/");
            WebMapServer wms = new WebMapServer(capabilitiesURL);

            List<org.geotools.ows.wms.Layer> wmsLayers = WMSLayerChooser.showSelectLayer(wms);

            mapContent.setTitle(wms.getCapabilities().getService().getTitle());
            for (org.geotools.ows.wms.Layer wmsLayer : wmsLayers) {
                System.out.println(wmsLayer.getTitle());
                WMSLayer displayLayer = new WMSLayer(wms, wmsLayer);
                mapContent.addLayer(displayLayer);
            }
        } catch (ServiceException | IOException e) {
            e.printStackTrace();
        }
    }

    private FileDataStore loadFileDataStore(String url) throws IOException {
        URL resource = this.getClass().getResource(url);
        File file = new File(resource.getPath());
        return FileDataStoreFinder.getDataStore(file);
    }

    public void createFeatureFromWKT(String wktCoordinates, String id) throws ParseException {

        SimpleFeatureBuilder featureBuilder;
        if (wktCoordinates.contains("MULTIPOLYGON"))
            featureBuilder = getSimpleFeatureBuilder(MultiPolygon.class);
        else
            featureBuilder = getSimpleFeatureBuilder(Polygon.class);

        featureBuilder.add(readWKTString(wktCoordinates));
        if (featureCollection == null) {
            featureCollection = new ArrayList<>();
            //createProductResultsLayer(Color.BLACK,null,1f);
        }
        featureCollection.add(featureBuilder.buildFeature(id));
        //doSetDisplayArea(new ReferencedEnvelope(mapContent.getViewport().getBounds()));
    }

    public void clearFeatures() {
        featureCollection = null;
    }

    public void showFeatureArea(List<String> ids)  {
        FeatureLayer layer = (FeatureLayer) getLayerByName(RESULTS_LAYER_TITLE);
        List<FeatureIdImpl> collect = ids.stream().map(FeatureIdImpl::new).collect(Collectors.toList());
        Style selectedStyle = createSelectedFeatureStyle(getLayerGeometryAttribute(layer), collect);
        layer.setStyle(selectedStyle);
        refresh();
    }

    private void drawPolygonFromCoordinates(Coordinate[] coordinates) throws ParseException {

        SimpleFeatureBuilder featureBuilder = getSimpleFeatureBuilder(Polygon.class);
        Polygon polygon = JTSFactoryFinder.getGeometryFactory().createPolygon(coordinates);
        searchAreaWKT = readWKTString(polygon.toText());
        featureBuilder.add(searchAreaWKT);

        removeSearchAreaLayer();

        addFeatureToMapContent(featureBuilder,Color.RED,null,0.5f, SEARCH_AREA);
        refresh();
    }

    public void createAndDrawProductsLayer() {
        Layer resultsLayer = createLayer(featureCollection,
                getPolygonStyle(Color.BLACK, null, 0.5f), RESULTS_LAYER_TITLE);
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

    private void removeSearchAreaLayer() {
        removeLayer(SEARCH_AREA);
    }


    private SimpleFeatureBuilder getSimpleFeatureBuilder(Class geometry) {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName("MyFeatureType");
        builder.setCRS( DefaultGeographicCRS.WGS84 ); // set crs
        builder.add("location", geometry); // add geometry
        return new SimpleFeatureBuilder(builder.buildFeatureType());
    }

    public String getWKT() {
        return searchAreaWKT == null ? "" : searchAreaWKT.toText();
    }

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
        draw.paint(graphics, rectangle, mapContent.getViewport().getBounds());
    }

    public void removeLayer(String title) {
        mapContent.layers().remove(getLayerByName(title));
        refresh();
    }

    public void refresh() {
        doSetDisplayArea(new ReferencedEnvelope(mapContent.getViewport().getBounds()));
    }

    public void selectFeature(int x, int y, boolean multipleSelection) throws IOException {
        if (getLayerByName(RESULTS_LAYER_TITLE) == null)
            return;
        FeatureLayer featureLayer = (FeatureLayer) getLayerByName(RESULTS_LAYER_TITLE);

        FeatureIdImpl featureId = (FeatureIdImpl) findFeatureIdByCoordinates(x, y, featureLayer);

        if (!multipleSelection || featureId == null)
            selectedFeatures.clear();

        if (featureId != null && !selectedFeatures.contains(featureId))
            selectedFeatures.add(featureId);
        else if (featureId!=null) {
            selectedFeatures.remove(featureId);
        }

        Style selectedStyle = createSelectedFeatureStyle(getLayerGeometryAttribute(featureLayer), selectedFeatures);
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

    private Style createSelectedFeatureStyle(String geometryAttributeName,List<FeatureIdImpl> ids) {
        Rule selectedRule = createRule(Color.BLUE,Color.CYAN, 3f, 0.1f, geometryAttributeName);
        selectedRule.setFilter(ff.id(Set.copyOf(ids)));

        Rule notSelectedFeaturesRules = createRule(Color.BLACK, null,1f,1f,geometryAttributeName);
        notSelectedFeaturesRules.setElseFilter(true);

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        if (ids.size() > 0)
            fts.rules().add(selectedRule);
        fts.rules().add(notSelectedFeaturesRules);

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
        mapContent.getViewport().setFixedBoundsOnResize(true);
        ReferencedEnvelope envelope = mapContent.getViewport().getBounds();
        double percent = (deltaY / getWidth())*-1;
        double deltaW = envelope.getWidth() * percent;
        double deltaH = envelope.getHeight() * percent;
        envelope.expandBy(deltaW, deltaH);
        doSetDisplayArea(envelope);
    }

    public void drawPolygon(double initX, double initY, double endX, double endY) {
        try {
            drawPolygonFromCoordinates(getSquareCoordinates(initX, initY, endX, endY));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private Coordinate[] getSquareCoordinates(double initX, double initY, double endX, double endY) {
        double[] startCoordinates = transformSceneToWorldCoordinate(initX, initY);
        double[] endCoordinates = transformSceneToWorldCoordinate(endX, endY);
        double diffX = endX - initX;
        double diffY = endY - initY;
        double[] upRightCorner = transformSceneToWorldCoordinate(initX+diffX,initY);
        double[] downLeftCorner = transformSceneToWorldCoordinate(initX,initY+diffY);
        return new Coordinate[] {
                new Coordinate(startCoordinates[0],startCoordinates[1]),
                new Coordinate(upRightCorner[0],upRightCorner[1]),
                new Coordinate(endCoordinates[0],endCoordinates[1]),
                new Coordinate(downLeftCorner[0],downLeftCorner[1]),
                new Coordinate(startCoordinates[0],startCoordinates[1])
        };
    }

    private double[] transformSceneToWorldCoordinate(double x, double y) {
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

    public void resetMap() {
        doSetDisplayArea(mapContent.layers().get(0).getBounds());
    }

    public void goToSelection() {
        doSetDisplayArea(mapContent.getMaxBounds());
    }


    public void clearMap() {
        List<Layer> layers = mapContent.layers();
            layers.stream()
                .filter(Objects::nonNull)
                .filter(l->l.getTitle()!=null && l.getTitle().equals(RESULTS_LAYER_TITLE))
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
