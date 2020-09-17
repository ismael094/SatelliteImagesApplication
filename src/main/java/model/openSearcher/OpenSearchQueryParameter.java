package model.openSearcher;

public enum OpenSearchQueryParameter {
    PLATFORM_NAME("platformname"),
    PRODUCT_TYPE("producttype"),
    FOOTPRINT("footprint"),
    TITLE("title"),
    FILENAME("filename"),
    INGESTION_DATE("ingestiondate");

    private String name;

    OpenSearchQueryParameter(String name) {
        this.name = name;
    }

    public String getParameterName() {
        return name;
    }

}
