package model.products.sentinel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.util.Calendar;

public class Sentinel1ProductDTO extends SentinelProductDTO {
    private final StringProperty sensorOperationalMode;
    private final StringProperty polarizationMode;

    public Sentinel1ProductDTO(StringProperty id, StringProperty title, StringProperty platformName, StringProperty productType, StringProperty footprint, StringProperty size, StringProperty status, ObjectProperty<Calendar> ingestionDate, StringProperty sensorOperationalMode, StringProperty polarizationMode) {
        super(id, title, platformName, productType, footprint, size, status, ingestionDate);
        this.sensorOperationalMode = sensorOperationalMode;
        this.polarizationMode = polarizationMode;
    }

    public String getSensorOperationalMode() {
        return sensorOperationalMode.get();
    }

    public StringProperty sensorOperationalModeProperty() {
        return sensorOperationalMode;
    }

    public void setSensorOperationalMode(String sensorOperationalMode) {
        this.sensorOperationalMode.set(sensorOperationalMode);
    }

    public String getPolarizationMode() {
        return polarizationMode.get();
    }

    public StringProperty polarizationModeProperty() {
        return polarizationMode;
    }

    public void setPolarizationMode(String polarizationMode) {
        this.polarizationMode.set(polarizationMode);
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

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
