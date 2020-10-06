package services.entities;



public class Sentinel1Product extends Product {
    private String sensorOperationalMode;
    private String polarizationMode;

    public Sentinel1Product(String id, String title, String platformName, String productType, String footprint, String size, String status, String ingestionDate, String sensorOperationalMode, String polarizationMode) {
        super(id, title, platformName, productType, footprint, size, status, ingestionDate);
        this.sensorOperationalMode = sensorOperationalMode;
        this.polarizationMode = polarizationMode;
    }

    public Sentinel1Product() {
    }

    public void setSensorOperationalMode(String sensorOperationalMode) {
        this.sensorOperationalMode = sensorOperationalMode;
    }

    public void setPolarizationMode(String polarizationMode) {
        this.polarizationMode = polarizationMode;
    }

    public String getSensorOperationalMode() {
        return sensorOperationalMode;
    }

    public String getPolarizationMode() {
        return polarizationMode;
    }

    @Override
    public String toString() {
        return "Sentinel1Product{" +
                "title='" + title + '\'' +
                ", id='" + id + '\'' +
                ", sensorOperationalMode='" + sensorOperationalMode + '\'' +
                ", polarizationMode='" + polarizationMode + '\'' +
                '}';
    }
}
