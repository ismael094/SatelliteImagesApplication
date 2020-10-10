package model.products;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

public class SentinelProductDTO extends ProductDTO {
    public SentinelProductDTO(StringProperty id, StringProperty title, StringProperty platformName, StringProperty productType, StringProperty footprint, StringProperty size, StringProperty status, ObjectProperty<Calendar> ingestionDate) {
        super(id, title, platformName, productType, footprint, size, status, ingestionDate);
    }

    @Override
    public double getSizeAsDouble() {
        if (size == null)
            return 0.0;
        double p;
        if (size.get().contains("MB"))
            return Double.parseDouble(size.get().substring(0,size.get().indexOf(" ")))/1024.0;
        else
            return Double.parseDouble(size.get().substring(0,size.get().indexOf(" ")));
    }

    @Override
    public URL getPreviewURL() {
        try {
            return new URL("https://scihub.copernicus.eu/dhus/odata/v1/Products('"+id.get()+"')/Products('Quicklook')/$value");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public URL getDownloadURL() {
        try {
            return new URL("https://scihub.copernicus.eu/dhus/odata/v1/Products('"+id.get()+"')/$value");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
