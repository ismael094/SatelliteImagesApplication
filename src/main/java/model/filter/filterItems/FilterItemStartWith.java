package model.filter.filterItems;

import model.filter.FilterItem;

public class FilterItemStartWith implements FilterItem {
    private final String property;
    private final String value;

    public FilterItemStartWith(String property, String value) {
        this.property = property;
        this.value = value;
    }

    @Override
    public String getString() {
        return "startswith("+property+",'"+value+"')";
    }
}
