package model;

import javafx.beans.property.SimpleStringProperty;
import model.products.Product;
import model.products.Sentinel1Product;
import model.products.Sentinel2Product;
import model.restriction.PlatFormRestriction;
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
    private ProductList productList;
    private Product product;

    @Before
    public void init() {
        product = mock(Product.class);
        productList = new ProductList(
                new SimpleStringProperty("List1"),
                new SimpleStringProperty("description"));
    }

    @Test
    public void with_name_list1_should_return_list1() {
        assertThat(productList.getName()).isEqualTo("List1");
    }

    @Test
    public void with_name_list2_should_return_list2() {
        productList = new ProductList(new SimpleStringProperty("List2")
                ,new SimpleStringProperty("description"));
        assertThat(productList.getName()).isEqualTo("List2");
    }

    @Test
    public void with_description_description_should_return_description() {
        assertThat(productList.getDescription()).isEqualTo("description");
    }

    @Test
    public void when_count_list_with_one_element_should_return_1() {
        Product productOData = mock(Product.class);
        productList.addProduct(productOData);
        assertThat(productList.count()).isEqualTo(1);
    }

    @Test
    public void when_get_product_by_id_in_list_should_return_one_product() {
        doReturn(PRODUCT_ID).when(product).getId();
        productList.addProduct(product);
        assertThat(productList.getProductById(PRODUCT_ID)).isEqualTo(product);
    }

    @Test
    public void when_get_product_by_id_not_in_list_should_return_null() {
        doReturn(PRODUCT_ID).when(product).getId();
        productList.addProduct(product);
        assertThat(productList.getProductById("no_id")).isEqualTo(null);
    }

    @Test
    public void when_add_repeated_product_should_skipped() {
        doReturn(PRODUCT_ID).when(product).getId();
        productList.addProduct(product);
        productList.addProduct(product);
        assertThat(productList.count()).isEqualTo(1);
    }

    @Test
    public void remove_product_in_list_should_remove_product() {
        doReturn(PRODUCT_ID).when(product).getId();
        productList.addProduct(product);
        Product p1 = new Product();
        p1.setSize("4.65 GB");
        p1.setId("1");
        productList.addProduct(p1);
        assertThat(productList.count()).isEqualTo(2);
        List<Product> list = new ArrayList<>();
        list.add(product);
        list.add(p1);
        productList.remove(list);
        assertThat(productList.count()).isEqualTo(0);
    }

    @Test
    public void remove_collection_of_product_should_remove_all_products_in_collection() {
        doReturn(PRODUCT_ID).when(product).getId();
        productList.addProduct(product);
        assertThat(productList.count()).isEqualTo(1);
        productList.remove(product);
        assertThat(productList.count()).isEqualTo(0);
    }

    @Test
    public void get_products_from_list_empty_should_return_empty_list() {
        productList.addProduct(product);
        assertThat(productList.count()).isEqualTo(1);
    }

    @Test
    public void with_one_product_should_return_the_product_size() {
        Product p1 = new Product();
        p1.setSize("4.65 GB");
        productList.addProduct(p1);
        assertThat(productList.productSize()).isEqualTo(4.65);
    }

    @Test
    public void with_more_than_one_products_should_return_the_sum_of_all_products() {
        Product p1 = new Product();
        p1.setSize("4.65 GB");
        p1.setId("1");
        Product p2 = new Product();
        p2.setSize("4.65 GB");
        p2.setId("p2");
        Product p3 = new Product();
        p3.setSize("4.65 GB");
        p3.setId("p3");
        productList.addProduct(p1);
        productList.addProduct(p2);
        productList.addProduct(p3);

        assertThat(productList.productSize()).isCloseTo(13.95,Percentage.withPercentage(1.0));
    }

    @Test
    public void with_more_than_one_products_in_MB_should_return_the_sum_of_all_products() {
        Product p1 = new Product();
        p1.setId("1");
        p1.setSize("755 MB");
        Product p2 = new Product();
        p2.setId("p2");
        p2.setSize("4.65 GB");
        Product p3 = new Product();
        p3.setId("p3");
        p3.setSize("4.65 GB");
        productList.addProduct(p1);
        productList.addProduct(p2);
        productList.addProduct(p3);
        assertThat(productList.productSize()).isCloseTo(10.037,Percentage.withPercentage(1.0));
    }

    @Test
    public void list_with_sentinel1_restriction_should_not_add_sentinel2_product() {
        productList = new ProductList(
                new SimpleStringProperty("List1"),
                new SimpleStringProperty("description"));
        PlatFormRestriction platFormRestriction = new PlatFormRestriction();
        platFormRestriction.add("Sentinel-1");
        productList.addRestriction(platFormRestriction);
        Sentinel2Product s2 = new Sentinel2Product();
        s2.setPlatformName("Sentinel-2");
        s2.setId("1");
        s2.setSize("755 MB");
        Sentinel1Product s1 = new Sentinel1Product();
        s1.setPlatformName("Sentinel-1");
        s1.setId("p2");
        s1.setSize("4.65 GB");
        productList.addProduct(s1);
        productList.addProduct(s2);
        assertThat(productList.count()).isEqualTo(1);
        assertThat(productList.getProducts().get(0)).isInstanceOf(Sentinel1Product.class);
    }

    @Test
    public void list_with_sentinel1_and_sentinel2_restriction_should_add_sentinel2_and_sentinel1_products() {
        productList = new ProductList(
                new SimpleStringProperty("List1"),
                new SimpleStringProperty("description"));
        PlatFormRestriction platFormRestriction = new PlatFormRestriction();
        platFormRestriction.add("Sentinel-1");
        platFormRestriction.add("Sentinel-2");
        productList.addRestriction(platFormRestriction);
        Sentinel2Product s2 = new Sentinel2Product();
        s2.setPlatformName("Sentinel-2");
        s2.setId("1");
        s2.setSize("755 MB");
        Sentinel1Product s1 = new Sentinel1Product();
        s1.setPlatformName("Sentinel-1");
        s1.setId("p2");
        s1.setSize("4.65 GB");
        productList.addProduct(s1);
        productList.addProduct(s2);
        assertThat(productList.count()).isEqualTo(2);
        assertThat(productList.getProducts().get(0)).isInstanceOf(Sentinel1Product.class);
        assertThat(productList.getProducts().get(1)).isInstanceOf(Sentinel2Product.class);
    }

    @Test
    public void list_with_GRD_producttype_restriction_should_add_only_grd_products() {
        productList = new ProductList(
                new SimpleStringProperty("List1"),
                new SimpleStringProperty("description"));
        ProductTypeRestriction productTypeRestriction = new ProductTypeRestriction();
        productTypeRestriction.add("GRD");
        productList.addRestriction(productTypeRestriction);
        Sentinel2Product s2 = new Sentinel2Product();
        s2.setPlatformName("Sentinel-2");
        s2.setProductType("MS1");
        s2.setId("1");
        s2.setSize("755 MB");
        Sentinel1Product s1 = new Sentinel1Product();
        s1.setPlatformName("Sentinel-1");
        s1.setProductType("GRD");
        s1.setId("p2");
        s1.setSize("4.65 GB");
        productList.addProduct(s2);
        assertThat(productList.count()).isEqualTo(0);
        productList.addProduct(s1);
        assertThat(productList.count()).isEqualTo(1);
        assertThat(productList.getProducts().get(0)).isInstanceOf(Sentinel1Product.class);
    }

    @Test
    public void set_default_area_of_work() {
        Product p1 = new Product();
        p1.setSize("4.65 GB");
        productList.setDefaultAreaOfWork("POLYGON");
        assertThat(productList.getAreaOfWorkOrDefault("default")).isEqualTo("POLYGON");
    }

    @Test
    public void no_area_of_work_should_return_null() {
        Product p1 = new Product();
        p1.setSize("4.65 GB");
        assertThat(productList.getAreaOfWorkOrDefault("default")).isNull();
    }

    @Test
    public void set_specific_area_of_work() {
        Product p1 = new Product();
        p1.setSize("4.65 GB");
        productList.setAreaOfWork(PRODUCT_ID,"POLYGON");
        assertThat(productList.getAreaOfWork(PRODUCT_ID)).isEqualTo("POLYGON");
    }

    @Test
    public void get_default_with_no_specific_area_of_work() {
        Product p1 = new Product();
        p1.setSize("4.65 GB");
        productList.setDefaultAreaOfWork("POLYGON2");
        productList.setAreaOfWork(PRODUCT_ID,"POLYGON");
        assertThat(productList.getAreaOfWorkOrDefault("fff")).isEqualTo("POLYGON2");
    }

    @Test
    public void set_default_and_specific_area_of_work() {
        Product p1 = new Product();
        p1.setSize("4.65 GB");
        productList.setDefaultAreaOfWork("POLYGON2");
        productList.setAreaOfWork(PRODUCT_ID,"POLYGON");
        assertThat(productList.getAreaOfWork(PRODUCT_ID)).isEqualTo("POLYGON");
        assertThat(productList.getAreaOfWork("default")).isEqualTo("POLYGON2");
    }

    @Test
    public void to_string_should_return_name_and_description() {
        productList = new ProductList(
                new SimpleStringProperty("List1"),
                new SimpleStringProperty("description"));
        assertThat(productList.toString()).isEqualTo("List1");
        /*assertThat(productList.toString()).isEqualTo("ProductList {" +
                "name='List1"  + '\'' +
                ", description='description");
        productList = new ProductList("List5","description5");
        assertThat(productList.toString()).isEqualTo("ProductList {" +
                "name='List5"  + '\'' +
                ", description='description5");*/
    }
}
