package services;

import org.junit.Test;
import utils.DownloadConfiguration;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;

public class Processor_ {

    @Test
    public void test() throws IOException {
        File file = new File(DownloadConfiguration.getListDownloadFolderLocation() + "\\Canarias\\GRD_Gamma0_VH,Gamma0_VV_2020_10_27_16_50_13_3.tif");
        getMetadata(file);

        System.out.println();
        //getMetadata();
    }

    private void getMetadata(File read) throws IOException {
        ImageReader reader = ImageIO.getImageReadersByFormatName("tiff").next();
        ImageInputStream iis = ImageIO.createImageInputStream(read);

        reader.setInput(iis);

        int numImages = reader.getNumImages(true);
        //BufferedImage read1 = reader.read(1);
        System.out.println(numImages);
        //BufferedImage img = new BufferedImage(reader.heig)
        //WritableImage writableImage = SwingFXUtils.toFXImage(read1, null);
    }
}
