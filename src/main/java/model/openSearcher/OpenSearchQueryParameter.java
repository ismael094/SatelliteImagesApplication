package model.openSearcher;

public enum OpenSearchQueryParameter {
    PLATFORM_NAME("platformname"),
    PRODUCT_TYPE("producttype"),
    FOOTPRINT("footprint");

    private String name;

    OpenSearchQueryParameter(String name) {
        this.name = name;
    }

    public String getParameterName() {
        return name;
    }

}
