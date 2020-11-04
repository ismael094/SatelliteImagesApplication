package utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SatelliteHelper_ {

    @Test
    public void is_radar_sentinel1() {
        assertThat(SatelliteHelper.isRadar("Sentinel-1")).isTrue();
        ;
    }
}
