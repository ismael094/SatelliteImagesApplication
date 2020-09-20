package model.openSearcher;

public enum OpenSearchQueryParameter {
    PLATFORM_NAME("platformname"),
    PRODUCT_TYPE("producttype"),
    FOOTPRINT("footprint"),
    TITLE("title"),
    FILENAME("filename"),
    INGESTION_DATE("ingestionDate"),
    CLOUD_COVER_PERCENTAGE("cloudcoverpercentage"),
    POLARISATION_MODE("polarisationmode"),
    SENSOR_OPERATIONAL_MODE("sensoroperationalmode");

    private String name;

    OpenSearchQueryParameter(String name) {
        this.name = name;
    }

    public String getParameterName() {
        return name;
    }

}
