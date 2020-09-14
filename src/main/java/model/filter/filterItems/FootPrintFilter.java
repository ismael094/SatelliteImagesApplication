package model.filter.filterItems;

import model.filter.FilterItem;

public class FootPrintFilter implements FilterItem {

    private final String wkt;

    public FootPrintFilter(String wkt) {
        this.wkt = wkt;
    }

    @Override
    public String getString() {
        return "footprint:Intersects("+wkt+")";
    }
}
