


import com.sun.media.jai.codec.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;
import org.esa.s2tbx.dataio.openjpeg.OpenJpegExecRetriever;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.gce.geotiff.GeoTiffReader;
import org.junit.Test;
import org.opengis.coverage.grid.Format;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.media.jai.JAI;
import javax.media.jai.RenderedOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;

import static org.esa.snap.lib.openjpeg.utils.OpenJpegExecRetriever.getOpenJPEGAuxDataPath;

public class Test_ {


    @Test
    public void testing() {

        /* client.getConfiguration().setDefaultPubFormat(ContentType.APPLICATION_JSON);
        client.getConfiguration()
                .setHttpClientFactory(new BasicAuthHttpClientFactory("ismael096", "Test_password"));

        String iCrmServiceRoot = "https://scihub.copernicus.eu/dhus/odata/v2";
        //ODataServiceDocumentRequest odClientReq = client.getRetrieveRequestFactory().getServiceDocumentRequest(iCrmServiceRoot);
        ODataEntityRequest<ClientEntity> request = client.getRetrieveRequestFactory()
                .getEntityRequest(getClient().newURIBuilder(iCrmServiceRoot)
                        .appendEntitySetSegment("Products").appendKeySegment("d046d2c8-edc8-4fd1-919e-71ce14b8b1ed")
                        .build());
        ;
        System.out.println(request.getURI().toString());
        final ODataRetrieveResponse<ClientEntity> response = request.execute();
        final ClientEntity entitySet = response.getBody();
        System.out.println(entitySet.getProperties().get(0).getValue());*/
    }

    @Test
    public void testing2() {
        /*Testing a = new Testing();
        try {
            a.readTickets();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Test
    public void testing3() {
        /*GeoTiffReader geoTiffReader = new GeoTiffReader("D:\\TFG_SatelliteImages\\Sentinel 1\\Images\\imag.tif");
        Format format = geoTiffReader.getFormat();
        System.out.println(geoTiffReader.getMetadata().hasModelTrasformation());
        System.out.println(Arrays.toString(geoTiffReader.getMetadata().getGeoKeys().toArray()));
        System.out.println(geoTiffReader.getGroundControlPoints());
        GridCoverage2D coverage = (GridCoverage2D) geoTiffReader.read(null);
        CoordinateReferenceSystem crs = coverage.getCoordinateReferenceSystem2D();
        Envelope env = coverage.getEnvelope();
        System.out.println(env.toString());*/

        /*final BufferedImage tif;
        try {
            FileSeekableStream stream = new FileSeekableStream("D:\\TFG_SatelliteImages\\Sentinel 1\\Images\\imag.tif");
            TIFFDecodeParam decodeParam = new TIFFDecodeParam();
            decodeParam.setDecodePaletteAsShorts(true);
            ParameterBlock params = new ParameterBlock();
            params.add(stream);
            RenderedOp image1 = JAI.create("tiff", params);
            BufferedImage img = image1.getAsBufferedImage();
            System.out.println(Arrays.toString(img.getPropertyNames()));

        } catch (IOException e) {
            e.printStackTrace();
        }
        //WritableImage writableImage = SwingFXUtils.toFXImage(read, null);
        /*String serviceUrl = "https://scihub.copernicus.eu/dhus/odata/v1/Products";
        ODataConsumer consumer = ODataConsumers.create(serviceUrl);
        OClientBehavior basicAuth = OClientBehaviors.basicAuth("ismael096", "Test_password");
        ODataConsumer.Builder builder = ODataConsumers.newBuilder("https://scihub.copernicus.eu/dhus/odata/v1").setClientBehaviors(basicAuth);

        ODataConsumer build = builder.build();
        EdmDataServices metadata = build.getMetadata();

        //OFunctionRequest<OObject> oObjectOFunctionRequest = build.callFunction("Products").pString("Id", "d046d2c8-edc8-4fd1-919e-71ce14b8b1ed");
        //System.out.println(oObjectOFunctionRequest.execute().count());
        OQueryRequest<OEntity> products = build.getEntities("Attributes");

        Enumerable<OEntity> execute = products.model.filter("Id eq 'd046d2c8-edc8-4fd1-919e-71ce14b8b1ed'").execute();
        // list category names
        for (OEntity category : execute) {
            List<OProperty<?>> properties = category.getProperties();
            for (OProperty<?> property :
                    properties) {
                EdmType type = property.getType();

                System.out.println(type.toString());
                System.out.println(property.getName() + " - " + property.getValue());
            }
        }*/


    }
}
