package services;

import org.junit.Test;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import utils.DownloadConfiguration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Processor_ {

    @Test
    public void test() throws IOException {
        fromBufferedImage(ImageIO.read(new File(DownloadConfiguration.getListDownloadFolderLocation()+"\\Canarias\\S1A_IW_GRDH_1SDV_20201011T065429_20201011T065454_034745_040C5C_48B4_0.tif")));
    }

    public Mat fromBufferedImage(BufferedImage img) {
        byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(img.getHeight(), img.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, pixels);
        return mat;
    }
}
