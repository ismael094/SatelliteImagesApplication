package utils.sentinel.bands;

import java.util.Arrays;
import java.util.List;

public class SLCBands implements Bands{
    @Override
    public List<String> getBands() {
        return Arrays.asList("Amplitude","Intensity");
    }
}
