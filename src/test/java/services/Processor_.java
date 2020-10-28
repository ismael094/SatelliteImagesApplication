package services;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import org.junit.Test;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.w3c.dom.Node;
import utils.DownloadConfiguration;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferFloat;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Iterator;

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

    public Mat fromBufferedImage(BufferedImage img) {
        System.out.println(img.getColorModel());
        float[] pixels = ((DataBufferFloat) img.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(img.getHeight(), img.getWidth(), CvType.CV_64F);
        mat.put(0, 0, pixels);
        return mat;
    }
}
