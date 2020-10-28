package utils.sentinel.bands;

import java.util.Arrays;
import java.util.List;

public class GRDBands implements Bands{
    @Override
    public List<String> getBands() {
        return Arrays.asList("Amplitude","Intensity");
    }
}
