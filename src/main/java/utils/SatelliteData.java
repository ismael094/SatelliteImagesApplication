package utils;

public class SatelliteData {
    public enum RadarSatellite {
        SENTINEL_1("Sentinel-1");

        private final String name;

        RadarSatellite(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum OpticalSatellite {
        SENTINEL_2("Sentinel-2");

        private final String name;

        OpticalSatellite(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

}
