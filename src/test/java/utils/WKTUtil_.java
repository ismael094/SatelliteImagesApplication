package utils;

import org.junit.Test;
import org.locationtech.jts.io.ParseException;

import static org.assertj.core.api.Assertions.assertThat;

public class WKTUtil_ {
    private final String container = "POLYGON((-18.61394412646544 29.180631143628986,-16.02117068896544 29.180631143628986," +
            "-16.02117068896544 27.166379179927947,-18.61394412646544 27.166379179927947,-18.61394412646544 29.180631143628986))";
    private final String contains = "POLYGON((-18.11406619677794 28.921320355362344,-17.65813357959044 28.921320355362344," +
            "-17.65813357959044 28.391087872273115,-18.11406619677794 28.391087872273115,-18.11406619677794 28.921320355362344))";
    private final String intersectsContains = "POLYGON((-18.28435428271544 28.95039018687099,-17.86962039599669 28.95039018687099," +
            "-17.86962039599669 28.396146770255452,-18.28435428271544 28.396146770255452,-18.28435428271544 28.95039018687099))";
    private final String notIntersects = "POLYGON((-16.77922732959044 28.393730627903686,-16.26836307177794 28.393730627903686," +
            "-16.26836307177794 28.013715126759934,-16.77922732959044 28.013715126759934,-16.77922732959044 28.393730627903686))";


    @Test
    public void contains() throws ParseException {
        assertThat(WKTUtil.workingAreaContains(container,contains)).isTrue();
    }

    @Test
    public void not_contain() throws ParseException {
        assertThat(WKTUtil.workingAreaContains(contains,container)).isFalse();
    }

    @Test
    public void contain_intersects_with_container() throws ParseException {
        assertThat(WKTUtil.workingAreaIntersects(container,contains)).isTrue();
    }

    @Test
    public void container_intersects_with_contains() throws ParseException {
        assertThat(WKTUtil.workingAreaIntersects(contains, intersectsContains)).isTrue();
    }

    @Test
    public void no_intersects() throws ParseException {
        assertThat(WKTUtil.workingAreaIntersects(contains, notIntersects)).isFalse();
    }

}
