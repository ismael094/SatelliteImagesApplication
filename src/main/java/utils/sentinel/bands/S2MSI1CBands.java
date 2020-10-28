package utils.sentinel.bands;

import java.util.Arrays;
import java.util.List;

public class S2MSI1CBands implements Bands{
    @Override
    public List<String> getBands() {
        return Arrays.asList("B1","Intensity");
    }
}
