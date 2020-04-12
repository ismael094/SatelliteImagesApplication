import model.filter.Filter;
import model.filter.filterItems.*;
import model.filter.operators.ComparisonOperators;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

public class Filter_ {
    private Filter filter;

    @Before
    public void initFilter() {
        filter = new Filter();
    }


    @Test
    public void no_filter_should_return_empty() {

        assertThat(filter.evaluate()).isEqualTo("");
    }

    @Test
    public void add_a_filter_item() {
        filter.add(new FilterItemStartWith("Name","S1"));
        assertThat(filter.evaluate()).isEqualTo("startswith(Name,'S1')");
    }

    @Test
    public void add_two_filter_item() {
        filter.add(new FilterItemStartWith("Name","S1"));
        filter.add(new FilterItemSubstringOf("Name","IW"));
        assertThat(filter.evaluate()).isEqualTo("startswith(Name,'S1') and substringof('IW',Name)");
    }

    @Test
    public void add_date_filter_item() {
        filter.add(new FilterItemStartWith("Name","S1"));
        filter.add(new FilterItemDate(DateFunction.YEAR,"CreationDate", ComparisonOperators.GE,"2020"));
        assertThat(filter.evaluate()).isEqualTo("startswith(Name,'S1') and year(CreationDate) ge 2020");
    }

    @Test
    public void add_datetime_filter_item() {
        LocalDateTime now = LocalDateTime.now();
        OffsetDateTime of = OffsetDateTime.of(now,
                ZoneOffset.ofHoursMinutes(6, 30));
        filter.add(new FilterItemStartWith("Name","S1"));
        filter.add(new FilterItemDateTime("CreationDate",ComparisonOperators.GE,now));
        assertThat(filter.evaluate()).isEqualTo("startswith(Name,'S1') and CreationDate ge " +of);
    }

    @Test
    public void clear_filter_should_remove_all_filter_item() {
        LocalDateTime now = LocalDateTime.now();
        OffsetDateTime of = OffsetDateTime.of(now,
                ZoneOffset.ofHoursMinutes(6, 30));
        filter.add(new FilterItemStartWith("Name","S1"));
        filter.add(new FilterItemDateTime("CreationDate",ComparisonOperators.GE,now));
        filter.clear();
        assertThat(filter.getFilterItems().size()).isEqualTo(0);
    }

}





