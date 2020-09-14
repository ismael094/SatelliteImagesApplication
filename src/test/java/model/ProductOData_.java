package model;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ProductOData_ {
    public static final String NAME = "S2A_MSIL1C_20160722T012702_N0204_R074_T57WXT_20160722T012659";
    public static final String ID = "d046d2c8-edc8-4fd1-919e-71ce14b8b1ed";
    public static final String CREATION_TIME = "2018-10-12T18:28:37.251Z";
    public static final long CONTENT_LENGTH = 156842856;
    ProductOData productOData;

    @Before
    public void initMockup() {
        productOData = mock(ProductOData.class);
        doReturn(CONTENT_LENGTH).when(productOData).getContentLength();
        doReturn(ID).when(productOData).getId();
        doReturn(NAME).when(productOData).getName();
        doReturn(CREATION_TIME).when(productOData).getCreationDate();

    }

    @Test
    public void should_return_same_id_as_seted() {
        doReturn(ID).when(productOData).getId();
        assertThat(productOData.getId()).isEqualTo(ID);
    }

    @Test
    public void should_return_same_content_length_as_seted() {
        doReturn(CONTENT_LENGTH).when(productOData).getContentLength();
        assertThat(productOData.getContentLength()).isEqualTo(CONTENT_LENGTH);
    }

    @Test
    public void should_return_same_name_as_seted() {
        doReturn(NAME).when(productOData).getName();
        assertThat(productOData.getName()).isEqualTo(NAME);
    }

    @Test
    public void setField_should_set_values() throws NoSuchFieldException, IllegalAccessException {

        productOData.setField("ContentLength", CONTENT_LENGTH);
        assertThat(productOData.getContentLength()).isEqualTo(CONTENT_LENGTH);

        productOData.setField("Id", ID);
        assertThat(productOData.getId()).isEqualTo(ID);

        productOData.setField("Name",  NAME);
        assertThat(productOData.getName()).isEqualTo(NAME);
    }

    @Test
    public void with_content_length_in_bytes_should_return_gigabytes() throws NoSuchFieldException, IllegalAccessException {
        ProductOData productOData = new ProductOData();
        productOData.setField("ContentLength", CONTENT_LENGTH);
        assertThat(productOData.getGigaBytes()).isEqualTo(0.156);

        productOData.setField("ContentLength", (long)534825600);
        assertThat(productOData.getGigaBytes()).isEqualTo(0.534);

        productOData.setField("ContentLength",  (long)1007388501);
        assertThat(productOData.getGigaBytes()).isEqualTo(1.007);

        productOData.setField("ContentLength",  (long)4452877361.0);
        assertThat(productOData.getGigaBytes()).isEqualTo(4.452);
    }



}
