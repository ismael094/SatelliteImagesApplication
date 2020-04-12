import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class Product_ {
    public static final String NAME = "S2A_MSIL1C_20160722T012702_N0204_R074_T57WXT_20160722T012659";
    public static final String ID = "d046d2c8-edc8-4fd1-919e-71ce14b8b1ed";
    public static final String CREATION_TIME = "2018-10-12T18:28:37.251Z";
    public static final long CONTENT_LENGTH = 156842856;
    Product product;
    @Before
    public void initMockup() {
        product = mock(Product.class);
        doReturn(CONTENT_LENGTH).when(product).getContentLength();
        doReturn(ID).when(product).getId();
        doReturn(NAME).when(product).getName();
        doReturn(CREATION_TIME).when(product).getCreationDate();

    }

    @Test
    public void should_return_same_id_as_seted() {
        doReturn(ID).when(product).getId();
        assertThat(product.getId()).isEqualTo(ID);
    }

    @Test
    public void should_return_same_content_length_as_seted() {
        doReturn(CONTENT_LENGTH).when(product).getContentLength();
        assertThat(product.getContentLength()).isEqualTo(CONTENT_LENGTH);
    }

    @Test
    public void should_return_same_name_as_seted() {
        doReturn(NAME).when(product).getName();
        assertThat(product.getName()).isEqualTo(NAME);
    }

    @Test
    public void setField_should_set_values() throws NoSuchFieldException, IllegalAccessException {

        product.setField("ContentLength", CONTENT_LENGTH);
        assertThat(product.getContentLength()).isEqualTo(CONTENT_LENGTH);

        product.setField("Id", ID);
        assertThat(product.getId()).isEqualTo(ID);

        product.setField("Name",  NAME);
        assertThat(product.getName()).isEqualTo(NAME);
    }

    @Test
    public void with_content_length_in_bytes_should_return_gigabytes() throws NoSuchFieldException, IllegalAccessException {
        Product product = new Product();
        product.setField("ContentLength", CONTENT_LENGTH);
        assertThat(product.getGigaBytes()).isEqualTo(0.156);

        product.setField("ContentLength", (long)534825600);
        assertThat(product.getGigaBytes()).isEqualTo(0.534);

        product.setField("ContentLength",  (long)1007388501);
        assertThat(product.getGigaBytes()).isEqualTo(1.007);

        product.setField("ContentLength",  (long)4452877361.0);
        assertThat(product.getGigaBytes()).isEqualTo(4.452);
    }



}
