import model.ProductOData;
import model.ProductList;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ProductODataList_ {
    public static final String PRODUCT_ID = "06ffb973-2be6-4ace-b813-5d6e24792af2";
    private ProductList productList;

    @Test
    public void with_name_list1_should_return_list1() {
        productList = new ProductList("List1","description");
        assertThat(productList.getName()).isEqualTo("List1");
    }

    @Test
    public void with_name_list2_should_return_list2() {
        productList = new ProductList("List2","description");
        assertThat(productList.getName()).isEqualTo("List2");
    }

    @Test
    public void with_description_description_should_return_description() {
        productList = new ProductList("List1","description");
        assertThat(productList.getDescription()).isEqualTo("description");
    }

    @Test
    public void when_count_list_with_one_element_should_return_1() {
        productList = new ProductList("List1","description");
        ProductOData productOData = mock(ProductOData.class);
        productList.addProduct(productOData);
        assertThat(productList.count()).isEqualTo(1);
    }

    @Test
    public void when_get_product_by_id_in_list_should_return_one_product() {
        productList = new ProductList("List1","description");
        ProductOData productOData = mock(ProductOData.class);
        doReturn(PRODUCT_ID).when(productOData).getId();
        productList.addProduct(productOData);
        assertThat(productList.getProductById(PRODUCT_ID)).isEqualTo(productOData);
    }

    @Test
    public void when_get_product_by_id_not_in_list_should_return_null() {
        productList = new ProductList("List1","description");
        ProductOData productOData = mock(ProductOData.class);
        doReturn(PRODUCT_ID).when(productOData).getId();
        productList.addProduct(productOData);
        assertThat(productList.getProductById("no_id")).isEqualTo(null);
    }

    @Test
    public void get_products_from_list_empty_should_return_empty_list() {
        productList = new ProductList("List1","description");
        ProductOData pr = mock(ProductOData.class);
        productList.addProduct(pr);
        List<ProductOData> productOData = productList.getProducts();
        assertThat(productOData.size()).isEqualTo(1);
    }

    @Test
    public void to_string_should_return_name_and_description() {
        productList = new ProductList("List1","description");
        assertThat(productList.toString()).isEqualTo("ProductList {" +
                "name='List1"  + '\'' +
                ", description='description");
        productList = new ProductList("List5","description5");
        assertThat(productList.toString()).isEqualTo("ProductList {" +
                "name='List5"  + '\'' +
                ", description='description5");
    }
}
