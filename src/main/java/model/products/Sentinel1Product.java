package model.products;

import model.products.Product;

public class Sentinel1Product extends Product {
    private String sensorOperationalMode;
    private String polarizationMode;

    public Sentinel1Product(String ingestionDate, String title, String id, String footprint, String size,
                            String productType, String platformName, String status, String sensorOperationalMode,
                            String polarizationMode) {
        super(ingestionDate, title, id, footprint, size, productType, platformName, status);
        this.sensorOperationalMode = sensorOperationalMode;
        this.polarizationMode = polarizationMode;
    }

    public Sentinel1Product() {
    }

    public Sentinel1Product(String sensorOperationalMode, String polarizationMode) {
        this.sensorOperationalMode = sensorOperationalMode;
        this.polarizationMode = polarizationMode;
    }

    public String getSensorOperationalMode() {
        return sensorOperationalMode;
    }

    public void setSensorOperationalMode(String sensorOperationalMode) {
        this.sensorOperationalMode = sensorOperationalMode;
    }

    public String getPolarizationMode() {
        return polarizationMode;
    }

    public void setPolarizationMode(String polarizationMode) {
        this.polarizationMode = polarizationMode;
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
