package utils;

public class SatelliteData {
    public static boolean isRadar(String platform) {
        try{
            RadarSatellite[] values = RadarSatellite.values();
            for (RadarSatellite value : values) {
                if (value.getName().equals(platform))
                    return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }



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
