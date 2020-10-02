package utils;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class WKTUtil {
    public static boolean workingAreaContains(String wktContainer, String wktContains) throws ParseException {
        WKTReader wktReader = new WKTReader(JTSFactoryFinder.getGeometryFactory());
        Geometry container = wktReader.read(wktContainer);
        Geometry contains = wktReader.read(wktContains);
        return container.contains(contains);
    }
}
