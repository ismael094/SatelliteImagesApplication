package services.entities;

public class Sentinel2Product extends Product {
    private double cloudCoverPercentage;
    private double vegetationPercentageCoverage;
    private double notVegetationPercentageCoverage;
    private double waterPercentageCoverage;

    public Sentinel2Product(String id, String title, String platformName, String productType, String footprint, String size, String status, String ingestionDate, double cloudCoverPercentage, double vegetationPercentageCoverage, double notVegetationPercentageCoverage, double waterPercentageCoverage) {
        super(id, title, platformName, productType, footprint, size, status, ingestionDate);
        this.cloudCoverPercentage = cloudCoverPercentage;
        this.vegetationPercentageCoverage = vegetationPercentageCoverage;
        this.notVegetationPercentageCoverage = notVegetationPercentageCoverage;
        this.waterPercentageCoverage = waterPercentageCoverage;
    }

    public Sentinel2Product() {
    }

    public double getCloudCoverPercentage() {
        return cloudCoverPercentage;
    }

    public void setCloudCoverPercentage(double cloudCoverPercentage) {
        this.cloudCoverPercentage = cloudCoverPercentage;
    }

    public double getVegetationPercentageCoverage() {
        return vegetationPercentageCoverage;
    }

    public void setVegetationPercentageCoverage(double vegetationPercentageCoverage) {
        this.vegetationPercentageCoverage = vegetationPercentageCoverage;
    }

    public double getNotVegetationPercentageCoverage() {
        return notVegetationPercentageCoverage;
    }

    public void setNotVegetationPercentageCoverage(double notVegetationPercentageCoverage) {
        this.notVegetationPercentageCoverage = notVegetationPercentageCoverage;
    }

    public double getWaterPercentageCoverage() {
        return waterPercentageCoverage;
    }

    public void setWaterPercentageCoverage(double waterPercentageCoverage) {
        this.waterPercentageCoverage = waterPercentageCoverage;
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
