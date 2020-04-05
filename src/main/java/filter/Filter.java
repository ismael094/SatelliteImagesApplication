package filter;

import java.util.ArrayList;
import java.util.List;

public class Filter {
    List<FilterItem> filters = new ArrayList<>();

    public void add(FilterItem filterItem) {
        filters.add(filterItem);
    }

    public String evaluate() {
        if (filters.size() == 0)
            return "";
        String filter = "";
        for (FilterItem s : filters)
            filter+=s.getString()+" and ";
        return  filter.substring(0,filter.length()-5);
    }

    public void clear() {
        filters.clear();
    }

    public List<FilterItem> getFilterItems() {
        return filters;
    }
}
