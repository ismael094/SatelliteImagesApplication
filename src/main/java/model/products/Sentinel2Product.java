package model.products;

import model.products.Product;

public class Sentinel2Product extends Product {
    private double cloudCoverPercentage;
    private double vegetationPercentageCoverage;
    private double notVegetationPercentageCoverage;
    private double waterPercentageCoverage;

    public Sentinel2Product(String ingestionDate, String title, String id, String footprint, String size,
                            String productType, String platformName, String status, double cloudCoverPercentage) {
        super(ingestionDate, title, id, footprint, size, productType, platformName, status);
        this.cloudCoverPercentage = cloudCoverPercentage;
    }

    public Sentinel2Product() {
    }

    public Sentinel2Product(double cloudCoverPercentage) {
        this.cloudCoverPercentage = cloudCoverPercentage;
    }

    public double getCloudCoverPercentage() {
        return cloudCoverPercentage;
    }

    public void setCloudCoverPercentage(double cloudCoverPercentage) {
        this.cloudCoverPercentage = cloudCoverPercentage;
    }

    public void setVegetationPercentageCoverage(double vegetationPercentageCoverage) {
        this.vegetationPercentageCoverage = vegetationPercentageCoverage;
    }

    public double getVegetationPercentageCoverage() {
        return vegetationPercentageCoverage;
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
}
