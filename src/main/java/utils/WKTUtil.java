package utils;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class WKTUtil {
    /**
     * Method to check if a WKT polygon contains another
     * @param wktContainer WKT String container
     * @param wktContains WKT String to check if its contain in container
     * @return true if wktContains is inside of wktContainer, if not, return false
     * @throws ParseException Error while reading WKT
     */
    public static boolean workingAreaContains(String wktContainer, String wktContains) throws ParseException {
        WKTReader wktReader = new WKTReader(JTSFactoryFinder.getGeometryFactory());
        Geometry container = wktReader.read(wktContainer);
        Geometry contains = wktReader.read(wktContains);
        return container.contains(contains);
    }
}
