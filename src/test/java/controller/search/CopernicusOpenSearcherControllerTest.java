package controller.search;

import controller.processing.workflow.Sentinel1GRDWorkflowController;
import gui.components.TabPaneComponent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Pagination;
import javafx.scene.control.RadioButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.SentinelData;
import model.exception.AuthenticationException;
import model.exception.NotAuthenticatedException;
import model.openSearcher.OpenSearchResponse;
import model.openSearcher.SentinelProductParameters;
import model.products.ProductDTO;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mock;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import services.search.OpenSearcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class CopernicusOpenSearcherControllerTest extends ApplicationTest {
    private OpenSearcher searcher;
    CopernicusOpenSearchController controller;
    private static final String WKT = "POLYGON((-18.10511746017351 28.92606310885101,-17.61073269454851 28.92606310885101,-17.61073269454851 28.337851903184273,-18.10511746017351 28.337851903184273,-18.10511746017351 28.92606310885101))";

    @Override
    public void start (Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Sentinel1GRDWorkflowController.class.getResource("/fxml/CopernicusOpenSearchView.fxml"));
        searcher = mock(OpenSearcher.class);
        initMock();
        controller = new CopernicusOpenSearchController("id2",searcher);
        controller.setTabPaneComponent(mock(TabPaneComponent.class));
        fxmlLoader.setController(controller);
        Parent mainNode = fxmlLoader.load();
        stage.setScene(new Scene(mainNode));
        stage.show();
        controller.initViewData();
        stage.toFront();
    }

    private void initMock() throws NotAuthenticatedException, IOException, AuthenticationException {
        OpenSearchResponse openSearchResponse = new OpenSearchResponse();
        openSearchResponse.setNumOfProducts(4);
        openSearchResponse.setRows(2);
        openSearchResponse.setStartIndex(0);
        openSearchResponse.setProducts(Arrays.asList(SentinelData.getSentinel1Product(),SentinelData.getSentinel2Product()));

        doReturn(openSearchResponse).when(searcher).search();
        doReturn("").when(searcher).getSearchParametersAsString();

    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void set_parameters() {
        clickOn("#productTypeList");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);

        clickOn("#polarisation");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);

        clickOn("#sensorMode");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);

        HashMap<String, String> searchParameters = controller.getSearchParameters();
        assertThat(searchParameters).isNotNull().isNotEmpty();

        assertThat(searchParameters.get(SentinelProductParameters.PRODUCT_TYPE.getParameterName())).isNotNull().isEqualTo("GRD");

        assertThat(searchParameters.get(SentinelProductParameters.POLARISATION_MODE.getParameterName())).isNotNull().isEqualTo("VV");

        assertThat(searchParameters.get(SentinelProductParameters.SENSOR_OPERATIONAL_MODE.getParameterName())).isNotNull().isEqualTo("SM");

    }

    @Test
    public void when_selecting_sentinel2_should_show_cloud_coverage() {
        AnchorPane cloudPane = lookup("#cloudPane").query();
        assertThat(cloudPane.isManaged()).isFalse();

        clickOn("#platformList");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);

        AnchorPane polarisationPane = lookup("#polarisationPane").query();
        assertThat(polarisationPane.isManaged()).isFalse();

        AnchorPane sensorPane = lookup("#sensorPane").query();
        assertThat(sensorPane.isVisible()).isFalse();

        assertThat(cloudPane.isManaged()).isTrue();
    }

    @Test
    public void set_date_range() {
        clickOn("#dateStart");
        write("15/10/2020");
        type(KeyCode.ENTER);

        clickOn("#dateFinish");
        write("20/10/2020");
        type(KeyCode.ENTER);

        HashMap<String, String> searchParameters = controller.getSearchParameters();

        assertThat(searchParameters.get(SentinelProductParameters.INGESTION_DATE.getParameterName()+"_from")).isNotNull().isEqualTo("2020-10-15");
        assertThat(searchParameters.get(SentinelProductParameters.INGESTION_DATE.getParameterName()+"_to")).isNotNull().isEqualTo("2020-10-20");
    }

    @Test
    public void search() {
        AnchorPane pane = lookup("#resultsPane").query();
        assertThat(pane.isVisible()).isFalse();
        clickOn("#search");
        pane = lookup("#resultsPane").query();
        assertThat(pane.isVisible()).isTrue();
        ListView<ProductDTO> view = lookup("#resultProductsList").query();
        assertThat(view.getItems().size()).isEqualTo(2);
        Pagination pagination = lookup("#pagination").query();
        assertThat(pagination.getCurrentPageIndex()).isEqualTo(0);
        assertThat(pagination.getPageCount()).isEqualTo(2);
    }

    @Test
    public void search_with_pagination() {
        clickOn("#search");
        type(KeyCode.ENTER);
        release(KeyCode.ENTER);
        Pagination pagination = lookup("#pagination").query();
        interact(()->{
            pagination.setCurrentPageIndex(1);
        });

        type(KeyCode.ENTER);
        assertThat(pagination.getCurrentPageIndex()).isEqualTo(1);
        assertThat(pagination.getPageCount()).isEqualTo(2);
    }

    @Test
    public void get_selected_products() {
        clickOn("#search");
        type(KeyCode.ENTER);

        clickOn("#resultProductsList");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);

        assertThat(controller.getSelectedProducts().size()).isGreaterThan(0);
    }


}
