import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

class FilterItemDateTime implements FilterItem {
    private final String property;
    private final ComparisonOperators operator;
    private final LocalDateTime value;
    private final OffsetDateTime dateTime4;

    public FilterItemDateTime(String property, ComparisonOperators operator, LocalDateTime value) {
        this.property = property;
        this.operator = operator;
        this.value = value;
        this.dateTime4 = OffsetDateTime.of(value,
                ZoneOffset.ofHoursMinutes(6, 30));
    }

    @Override
    public String getString() {
        return property + " " + operator.toString().toLowerCase() + " " + dateTime4 + "";
    }
}