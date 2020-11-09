package services.download;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class ProductDownloadManager_ {

    private final String downloadFolder = System.getProperty("user.home")+"\\Documents\\SatInf";

    @Test
    public void test() {
        File file = new File(downloadFolder);
        if (!file.exists()) {
            boolean mkdir = file.mkdir();
            if (!mkdir)
                System.out.println("ERROR");
        }
    }
}
