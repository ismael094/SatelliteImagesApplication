package model.openSearcher;

public enum SentinelProductParameters implements ProductParameters {
    PLATFORM_NAME("platformname"),
    PRODUCT_TYPE("producttype"),
    FOOTPRINT("footprint"),
    TITLE("title"),
    FILENAME("filename"),
    INGESTION_DATE("ingestionDate"),
    CLOUD_COVER_PERCENTAGE("cloudcoverpercentage"),
    POLARISATION_MODE("polarisationmode"),
    ID("id"),
    SIZE("size"),
    SENSOR_OPERATIONAL_MODE("sensoroperationalmode");

    private String name;

    SentinelProductParameters(String name) {
        this.name = name;
    }

    @Override
    public String getParameterName() {
        return name;
    }

}
