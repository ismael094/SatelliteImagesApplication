package model;

import java.lang.reflect.Field;

public class Product {
    private String Id;
    private int ContentLength ;
    private String Name;
    private String CreationDate;
    private String IngestionDate;
    private String ModificationDate;
    private String Footprint;
    private boolean Online;

    public Product() {

    }

    public void setIntField(String fieldName, int value) {
        try {
            Field field = getClass().getDeclaredField(fieldName);
            field.setInt(this, value);
        } catch (Exception e) {

        }

    }

    public void setStringField(String fieldName, String value) {
        try {
            Field field = getClass().getDeclaredField(fieldName);
            field.set(this, value);
        } catch (Exception e) {

        }
    }

    public String getId() {
        return Id;
    }

    public int getContentLength() {
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
                 ", ContentLength=" + ContentLength +'\n'+
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
}
