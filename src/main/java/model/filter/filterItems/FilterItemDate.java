package model.filter.filterItems;

import model.filter.FilterItem;
import model.filter.operators.ComparisonOperators;

public class FilterItemDate implements FilterItem {
    private final String property;
    private final ComparisonOperators operator;
    private final String value;
    private final DateFunction function;


    public FilterItemDate(DateFunction function, String property, ComparisonOperators operator, String value) {
        this.function = function;
        this.property = property;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public String getString() {
        return function.toString().toLowerCase()+"("+property + ") " + operator.toString().toLowerCase() + " " + value;
    }
}