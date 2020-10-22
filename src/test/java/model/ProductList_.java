package model;

import javafx.beans.property.SimpleStringProperty;
import model.list.ProductListDTO;
import model.processing.Sentinel1GRDDefaultWorkflowDTO;
import model.processing.WorkflowType;
import model.products.ProductDTO;
import model.products.Sentinel1ProductDTO;
import model.products.Sentinel2ProductDTO;
import model.restriction.PlatformRestriction;
import model.restriction.ProductTypeRestriction;
import org.assertj.core.data.Percentage;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ProductList_ {
    public static final String PRODUCT_ID = "06ffb973-2be6-4ace-b813-5d6e24792af2";
    public static final String FOOTPRINT_INVALID = "POLYGON((-9.630307693957912 32.909380961554916,-8.355893631457912 32.909380961554916,-8.355893631457912 31.87037104706671,-9.630307693957912 31.87037104706671,-9.630307693957912 32.909380961554916))";
    private ProductListDTO productListDTO;
    private ProductDTO productMock;
    private ProductDTO product;
    private ProductDTO p1;
    private final String FOOTPRINT = "POLYGON((-18.393555946045392 29.37323662909026,-13.262940711670392 29.37323662909026,-13.262940711670392 27.32362244850862,-18.393555946045392 27.32362244850862,-18.393555946045392 29.37323662909026))";
    private final String AREA_OF_WORK = "POLYGON((-18.037842482007054 28.874114470373776,-17.683533399975804 28.874114470373776,-17.683533399975804 28.406474298401793,-18.037842482007054 28.406474298401793,-18.037842482007054 28.874114470373776))";

    @Before
    public void init() {
        product = SentinelData.getProduct();
        productMock = mock(ProductDTO.class);
        p1 = mock(ProductDTO.class);
        productListDTO = new ProductListDTO(
                new SimpleStringProperty("List1"),
                new SimpleStringProperty("description"));
    }

    @Test
    public void with_name_list1_should_return_list1() {
        assertThat(productListDTO.getName()).isEqualTo("List1");
    }

    @Test
    public void with_name_list2_should_return_list2() {
        productListDTO = new ProductListDTO(new SimpleStringProperty("List2")
                ,new SimpleStringProperty("description"));
        assertThat(productListDTO.getName()).isEqualTo("List2");
    }

    @Test
    public void with_description_description_should_return_description() {
        assertThat(productListDTO.getDescription()).isEqualTo("description");
    }

    @Test
    public void when_count_list_with_one_element_should_return_1() {
        ProductDTO productOData = mock(ProductDTO.class);
        productListDTO.addProduct(productOData);
        assertThat(productListDTO.count()).isEqualTo(1);
    }

    @Test
    public void when_get_product_by_id_in_list_should_return_one_product() {
        doReturn(PRODUCT_ID).when(productMock).getId();
        productListDTO.addProduct(productMock);
        assertThat(productListDTO.getProductById(PRODUCT_ID)).isEqualTo(productMock);
    }

    @Test
    public void when_get_product_by_id_not_in_list_should_return_null() {
        doReturn(PRODUCT_ID).when(productMock).getId();
        productListDTO.addProduct(productMock);
        assertThat(productListDTO.getProductById("no_id")).isEqualTo(null);
    }

    @Test
    public void when_add_repeated_product_should_skipped() {
        doReturn(PRODUCT_ID).when(productMock).getId();
        productListDTO.addProduct(productMock);
        productListDTO.addProduct(productMock);
        assertThat(productListDTO.count()).isEqualTo(1);
    }

    @Test
    public void remove_product_in_list_should_remove_product() {
        doReturn(PRODUCT_ID).when(productMock).getId();
        productListDTO.addProduct(productMock);
        doReturn("4.65 GB").when(p1).getSize();
        doReturn("1").when(p1).getId();
        productListDTO.addProduct(p1);
        assertThat(productListDTO.count()).isEqualTo(2);
        List<ProductDTO> list = new ArrayList<>();
        list.add(productMock);
        list.add(p1);
        productListDTO.remove(list);
        assertThat(productListDTO.count()).isEqualTo(0);
    }

    @Test
    public void remove_collection_of_product_should_remove_all_products_in_collection() {
        doReturn(PRODUCT_ID).when(productMock).getId();
        productListDTO.addProduct(productMock);
        assertThat(productListDTO.count()).isEqualTo(1);
        productListDTO.remove(productMock);
        assertThat(productListDTO.count()).isEqualTo(0);
    }

    @Test
    public void get_products_from_list_empty_should_return_empty_list() {
        productListDTO.addProduct(productMock);
        assertThat(productListDTO.count()).isEqualTo(1);
    }

    @Test
    public void with_one_product_should_return_the_product_size() {
        doReturn(4.65).when(p1).getSizeAsDouble();
        productListDTO.addProduct(p1);
        assertThat(productListDTO.productSize()).isEqualTo(4.65);
    }

    @Test
    public void with_more_than_one_products_should_return_the_sum_of_all_products() {

        doReturn(4.65).when(p1).getSizeAsDouble();
        doReturn("1").when(p1).getId();
        ProductDTO p2 = mock(ProductDTO.class);
        doReturn(4.65).when(p2).getSizeAsDouble();
        doReturn("p2").when(p2).getId();
        ProductDTO p3 = mock(ProductDTO.class);
        doReturn(4.65).when(p3).getSizeAsDouble();
        doReturn("p3").when(p3).getId();
        productListDTO.addProduct(p1);
        productListDTO.addProduct(p2);
        productListDTO.addProduct(p3);

        assertThat(productListDTO.productSize()).isCloseTo(13.95,Percentage.withPercentage(1.0));
    }

    @Test
    public void with_more_than_one_products_in_MB_should_return_the_sum_of_all_products() {
        doReturn(0.755).when(p1).getSizeAsDouble();
        doReturn("1").when(p1).getId();

        ProductDTO p2 = mock(ProductDTO.class);
        doReturn(4.65).when(p2).getSizeAsDouble();
        doReturn("p2").when(p2).getId();

        ProductDTO p3 = mock(ProductDTO.class);
        doReturn(4.65).when(p3).getSizeAsDouble();
        doReturn("p3").when(p3).getId();

        productListDTO.addProduct(p1);
        productListDTO.addProduct(p2);
        productListDTO.addProduct(p3);
        assertThat(productListDTO.productSize()).isCloseTo(10.037,Percentage.withPercentage(1.0));
    }

    @Test
    public void list_with_sentinel1_restriction_should_not_add_sentinel2_product() {
        productListDTO = new ProductListDTO(
                new SimpleStringProperty("List1"),
                new SimpleStringProperty("description"));
        PlatformRestriction platFormRestriction = new PlatformRestriction();
        platFormRestriction.add("Sentinel-1");
        productListDTO.addRestriction(platFormRestriction);

        Sentinel2ProductDTO s2 = mock(Sentinel2ProductDTO.class);
        doReturn("Sentinel-2").when(s2).getPlatformName();
        doReturn("1").when(s2).getId();

        Sentinel1ProductDTO s1 = mock(Sentinel1ProductDTO.class);
        doReturn("Sentinel-1").when(s1).getPlatformName();
        doReturn("2").when(s1).getId();
        productListDTO.addProduct(s1);
        productListDTO.addProduct(s2);
        assertThat(productListDTO.count()).isEqualTo(1);
        assertThat(productListDTO.getProducts().get(0)).isInstanceOf(Sentinel1ProductDTO.class);
    }

    @Test
    public void list_with_sentinel1_and_sentinel2_restriction_should_add_sentinel2_and_sentinel1_products() {
        productListDTO = new ProductListDTO(
                new SimpleStringProperty("List1"),
                new SimpleStringProperty("description"));
        PlatformRestriction platFormRestriction = new PlatformRestriction();
        platFormRestriction.add("Sentinel-1");
        platFormRestriction.add("Sentinel-2");
        productListDTO.addRestriction(platFormRestriction);
        Sentinel2ProductDTO s2 = mock(Sentinel2ProductDTO.class);
        doReturn("Sentinel-2").when(s2).getPlatformName();
        doReturn("1").when(s2).getId();

        Sentinel1ProductDTO s1 = mock(Sentinel1ProductDTO.class);
        doReturn("Sentinel-1").when(s1).getPlatformName();
        doReturn("2").when(s1).getId();
        productListDTO.addProduct(s1);
        productListDTO.addProduct(s2);
        assertThat(productListDTO.count()).isEqualTo(2);
        assertThat(productListDTO.getProducts().get(0)).isInstanceOf(Sentinel1ProductDTO.class);
        assertThat(productListDTO.getProducts().get(1)).isInstanceOf(Sentinel2ProductDTO.class);
    }

    @Test
    public void list_with_GRD_producttype_restriction_should_add_only_grd_products() {
        productListDTO = new ProductListDTO(
                new SimpleStringProperty("List1"),
                new SimpleStringProperty("description"));
        ProductTypeRestriction productTypeRestriction = new ProductTypeRestriction();
        productTypeRestriction.add("GRD");
        productListDTO.addRestriction(productTypeRestriction);
        Sentinel2ProductDTO s2 = mock(Sentinel2ProductDTO.class);
        doReturn("Sentinel-2").when(s2).getPlatformName();
        doReturn("MS1").when(s2).getPlatformName();
        doReturn("1").when(s2).getId();

        Sentinel1ProductDTO s1 = mock(Sentinel1ProductDTO.class);
        doReturn("Sentinel-1").when(s1).getPlatformName();
        doReturn("GRD").when(s1).getProductType();
        doReturn("2").when(s1).getId();
        productListDTO.addProduct(s2);
        assertThat(productListDTO.count()).isEqualTo(0);
        productListDTO.addProduct(s1);
        assertThat(productListDTO.count()).isEqualTo(1);
        assertThat(productListDTO.getProducts().get(0)).isInstanceOf(Sentinel1ProductDTO.class);
    }

    @Test
    public void add_grd_workflow_to_list() {
        productListDTO = new ProductListDTO(
                new SimpleStringProperty("List1"),
                new SimpleStringProperty("description"));
        ProductTypeRestriction productTypeRestriction = new ProductTypeRestriction();
        productTypeRestriction.add("GRD");
        productListDTO.addRestriction(productTypeRestriction);

        productListDTO.addProduct(SentinelData.getSentinel1Product());
        productListDTO.addWorkflow(new Sentinel1GRDDefaultWorkflowDTO());
        assertThat(productListDTO.getWorkflow(WorkflowType.GRD).getType()).isEqualTo(WorkflowType.GRD);

    }

    @Test
    public void set_default_area_of_work() {
        productListDTO.addAreaOfWork("POLYGON");
        assertThat(productListDTO.getAreasOfWork().get(0)).isEqualTo("POLYGON");
    }

    @Test
    public void no_area_of_work_should_return_null() {
        assertThat(productListDTO.areasOfWorkOfProduct("POLYGON")).isNull();
    }

    @Test
    public void set_specific_area_of_work() {
        productListDTO.addAreaOfWork(AREA_OF_WORK);
        assertThat(productListDTO.areasOfWorkOfProduct(FOOTPRINT).size()).isEqualTo(1);
        assertThat(productListDTO.areasOfWorkOfProduct(FOOTPRINT).get(0)).isEqualTo(AREA_OF_WORK);
    }

    @Test
    public void with_footprint_not_containing_any_areas_of_work_should_return_empty_list() {
        productListDTO.addAreaOfWork(AREA_OF_WORK);
        assertThat(productListDTO.areasOfWorkOfProduct(FOOTPRINT_INVALID).size()).isEqualTo(0);
    }

    @Test
    public void with_product_containing_area_of_work_should_return_map_with_list_of_area_of_work() {
        product.setId("id");
        product.setSize("755 MB");
        product.setFootprint(FOOTPRINT);
        productListDTO.addProduct(product);
        productListDTO.addAreaOfWork(AREA_OF_WORK);
        assertThat(productListDTO.getProductsAreasOfWorks().size()).isEqualTo(1);
        assertThat(productListDTO.getProductsAreasOfWorks().get("id").size()).isEqualTo(1);
        assertThat(productListDTO.getProductsAreasOfWorks().get("id").get(0)).isEqualTo(AREA_OF_WORK);
    }

    @Test
    public void to_string_should_return_name_and_description() {
        productListDTO = new ProductListDTO(
                new SimpleStringProperty("List1"),
                new SimpleStringProperty("description"));
        assertThat(productListDTO.toString()).isEqualTo("List1");
        /*assertThat(productList.toString()).isEqualTo("ProductList {" +
                "name='List1"  + '\'' +
                ", description='description");
        productList = new ProductList("List5","description5");
        assertThat(productList.toString()).isEqualTo("ProductList {" +
                "name='List5"  + '\'' +
                ", description='description5");*/
    }
}
