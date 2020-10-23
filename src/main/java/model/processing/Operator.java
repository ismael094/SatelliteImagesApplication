package model.processing;

public enum Operator {
    APPLY_ORBIT_FILE("Apply-Orbit-File"),
    THERMAL_NOISE_REMOVAL("ThermalNoiseRemoval"),
    CALIBRATION("Calibration"),
    TERRAIN_CORRECTION("Terrain-Correction"),
    TERRAIN_FLATTENING("Terrain-Flattening"),
    WRITE_AND_READ("WriteAndRead"),
    WRITE("Write"),
    READ("Read"),
    SUBSET("Subset"),
    CREATE_BUFFERED_IMAGE("CreateBufferedImage");

    private final String name;

    Operator(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
