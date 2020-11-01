package model.products.sentinel;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.util.Calendar;

public class Sentinel2ProductDTO extends SentinelProductDTO {
    private final DoubleProperty cloudCoverPercentage;
    private final DoubleProperty vegetationPercentageCoverage;
    private final DoubleProperty notVegetationPercentageCoverage;
    private final DoubleProperty waterPercentageCoverage;

    public Sentinel2ProductDTO(StringProperty id, StringProperty title, StringProperty platformName, StringProperty productType, StringProperty footprint, StringProperty size, StringProperty status, ObjectProperty<Calendar> ingestionDate, DoubleProperty cloudCoverPercentage, DoubleProperty vegetationPercentageCoverage, DoubleProperty notVegetationPercentageCoverage, DoubleProperty waterPercentageCoverage) {
        super(id, title, platformName, productType, footprint, size, status, ingestionDate);
        this.cloudCoverPercentage = cloudCoverPercentage;
        this.vegetationPercentageCoverage = vegetationPercentageCoverage;
        this.notVegetationPercentageCoverage = notVegetationPercentageCoverage;
        this.waterPercentageCoverage = waterPercentageCoverage;
    }

    public double getCloudCoverPercentage() {
        return cloudCoverPercentage.get();
    }

    public DoubleProperty cloudCoverPercentageProperty() {
        return cloudCoverPercentage;
    }

    public void setCloudCoverPercentage(double cloudCoverPercentage) {
        this.cloudCoverPercentage.set(cloudCoverPercentage);
    }

    public double getVegetationPercentageCoverage() {
        return vegetationPercentageCoverage.get();
    }

    public DoubleProperty vegetationPercentageCoverageProperty() {
        return vegetationPercentageCoverage;
    }

    public void setVegetationPercentageCoverage(double vegetationPercentageCoverage) {
        this.vegetationPercentageCoverage.set(vegetationPercentageCoverage);
    }

    public double getNotVegetationPercentageCoverage() {
        return notVegetationPercentageCoverage.get();
    }

    public DoubleProperty notVegetationPercentageCoverageProperty() {
        return notVegetationPercentageCoverage;
    }

    public void setNotVegetationPercentageCoverage(double notVegetationPercentageCoverage) {
        this.notVegetationPercentageCoverage.set(notVegetationPercentageCoverage);
    }

    public double getWaterPercentageCoverage() {
        return waterPercentageCoverage.get();
    }

    public DoubleProperty waterPercentageCoverageProperty() {
        return waterPercentageCoverage;
    }

    public void setWaterPercentageCoverage(double waterPercentageCoverage) {
        this.waterPercentageCoverage.set(waterPercentageCoverage);
    }

    @Override
    public String toString() {
        return "Sentinel2Product{" +
                "cloudCoverPercentage=" + cloudCoverPercentage +
                ", vegetationPercentageCoverage=" + vegetationPercentageCoverage +
                ", notVegetationPercentageCoverage=" + notVegetationPercentageCoverage +
                ", waterPercentageCoverage=" + waterPercentageCoverage +
                '}';
    }
}
