package model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import model.products.ProductDTO;
import model.products.Sentinel1ProductDTO;
import model.products.Sentinel2ProductDTO;
import model.products.SentinelProductDTO;
import services.entities.Product;

import javax.xml.bind.DatatypeConverter;
import java.util.Calendar;

public class SentinelData {
    public static final String TITLE = "S1A_IW_RAW__0SDV_20200910T070039_20200910T070111_034293_03FC6D_E4C0";
    public static final String ID = "2f4f7537-1711-4871-af8b-9f3ce98a1f9a";
    public static final String INGESTION_TIME = "2020-09-10T08:50:57.572Z";
    public static final String FOOTPRINT = "MULTIPOLYGON (((-13.3525 34.187, -12.8959 36.142, -15.6169 36.4117, -16.0074 34.4559, -13.3525 34.187)))";
    public static final String PRODUCT_TYPE = "RAW";
    public static final String PLATFORM_NAME = "Sentinel-1";
    public static final String SIZE = "1.55 GB";
    public static final String STATUS = "ARCHIVED";
    public static final Calendar calendar = DatatypeConverter.parseDateTime(INGESTION_TIME);
    public static final String SENSOR_OPERATIONAL_MODE = "IW";
    public static final String POLARISATION_MODE = "VH VV";
    public static final double CLOUD_PERCENTAGE_COVERAGE = 67.98189;
    public static final double VEGETATION_PERCENTAGE_COVERAGE = 0.002322;
    public static final double NOT_VEGETATION_PERCENTAGE_COVERAGE = 0.002412;
    public static final double WATER_PERCENTAGE_COVERAGE = 32.013375;

    public static ProductDTO getProduct() {
        return new SentinelProductDTO(
                new SimpleStringProperty(SentinelData.ID),new SimpleStringProperty(SentinelData.TITLE),
                new SimpleStringProperty(SentinelData.PLATFORM_NAME),new SimpleStringProperty(SentinelData.PRODUCT_TYPE),
                new SimpleStringProperty(SentinelData.FOOTPRINT),new SimpleStringProperty(SentinelData.SIZE),
                new SimpleStringProperty(SentinelData.STATUS),new SimpleObjectProperty<>(SentinelData.calendar));
    }

    public static Sentinel1ProductDTO getSentinel1Product() {
        return new Sentinel1ProductDTO(
                new SimpleStringProperty(SentinelData.ID),new SimpleStringProperty(SentinelData.TITLE),
                new SimpleStringProperty(SentinelData.PLATFORM_NAME),new SimpleStringProperty(SentinelData.PRODUCT_TYPE),
                new SimpleStringProperty(SentinelData.FOOTPRINT),new SimpleStringProperty(SentinelData.SIZE),
                new SimpleStringProperty(SentinelData.STATUS),new SimpleObjectProperty<>(SentinelData.calendar),
                new SimpleStringProperty(SentinelData.SENSOR_OPERATIONAL_MODE),new SimpleStringProperty(SentinelData.POLARISATION_MODE));
    }

    public static Sentinel2ProductDTO getSentinel2Product() {
        return new Sentinel2ProductDTO(
                new SimpleStringProperty(SentinelData.ID),new SimpleStringProperty(SentinelData.TITLE),
                new SimpleStringProperty("Sentinel-2"),new SimpleStringProperty(SentinelData.PRODUCT_TYPE),
                new SimpleStringProperty(SentinelData.FOOTPRINT),new SimpleStringProperty(SentinelData.SIZE),
                new SimpleStringProperty(SentinelData.STATUS),new SimpleObjectProperty<>(SentinelData.calendar),
                new SimpleDoubleProperty(SentinelData.CLOUD_PERCENTAGE_COVERAGE),new SimpleDoubleProperty(SentinelData.VEGETATION_PERCENTAGE_COVERAGE),
                new SimpleDoubleProperty(SentinelData.NOT_VEGETATION_PERCENTAGE_COVERAGE),new SimpleDoubleProperty(SentinelData.WATER_PERCENTAGE_COVERAGE));
    }
}
