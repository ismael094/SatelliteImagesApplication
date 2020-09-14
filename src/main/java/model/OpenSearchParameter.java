package model;

public enum OpenSearchParameter {
    PLATFORM_NAME("platformname"),
    PRODUCT_TYPE("producttype"),
    FOOTPRINT("footprint");

    private String name;

    OpenSearchParameter(String name) {
        this.name = name;
    }

    public String getParameterName() {
        return name;
    }

}
