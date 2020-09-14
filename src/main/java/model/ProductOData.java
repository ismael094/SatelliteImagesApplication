package model;

import java.lang.reflect.Field;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class ProductOData {
    private String Id;
    private long ContentLength ;
    private String Name;
    private String CreationDate;
    private String IngestionDate;
    private String ModificationDate;
    private String Footprint;
    private boolean Online;

    public ProductOData() {

    }

    public void setField(String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = getClass().getDeclaredField(fieldName);
        field.set(this, value);
    }

    public String getId() {
        return Id;
    }

    public long getContentLength() {
        return ContentLength;
    }

    public String getName() {
        return Name;
    }

    public String getCreationDate() {
        return CreationDate;
    }

    public String getIngestionDate() {
        return IngestionDate;
    }

    public String getModificationDate() {
        return ModificationDate;
    }

    public String getFootprint() {
        return Footprint;
    }

    public boolean isOnline() {
        return Online;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getInfo() {
        return "model.Product{" +
                "Id='" + Id + '\'' + '\n'+
                 ", ContentLength=" + getGigaBytes() + " Gb" + '\n'+
                ", Name='" + Name + '\'' +'\n'+
                ", CreationDate='" + CreationDate + '\'' +'\n'+
                ", IngestionDate='" + IngestionDate + '\'' +'\n'+
                ", ModificationDate='" + ModificationDate + '\'' +'\n'+
                ", Footprint='" + Footprint + '\'' +'\n'+
                ", Online=" + Online +'\n'+
                '}';
    }

    public String getUrlImg() {
        return "https://scihub.copernicus.eu/dhus/odata/v1/Products('"+getId()+"')/Products('Quicklook')/$value";
    }

    public double getGigaBytes() {
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.DOWN);
        return Double.parseDouble(df.format((double)(((long) this.getContentLength()) / 1000000000.0)).replace(',','.'));
    }

}
