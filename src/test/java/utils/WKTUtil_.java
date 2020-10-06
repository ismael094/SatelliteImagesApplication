package utils;

import org.junit.Test;
import org.locationtech.jts.io.ParseException;

import static org.assertj.core.api.Assertions.assertThat;

public class WKTUtil_ {
    private final String container = "POLYGON((-18.61394412646544 29.180631143628986,-16.02117068896544 29.180631143628986,-16.02117068896544 27.166379179927947,-18.61394412646544 27.166379179927947,-18.61394412646544 29.180631143628986))";
    private final String contains = "POLYGON((-18.11406619677794 28.921320355362344,-17.65813357959044 28.921320355362344,-17.65813357959044 28.391087872273115,-18.11406619677794 28.391087872273115,-18.11406619677794 28.921320355362344))";

    @Test
    public void contains() throws ParseException {
        assertThat(WKTUtil.workingAreaContains(container,contains)).isTrue();
    }

    @Test
    public void not_contain() throws ParseException {
        assertThat(WKTUtil.workingAreaContains(contains,container)).isFalse();
    }
}
