package controller.processing;

import com.google.common.collect.Lists;
import com.jfoenix.controls.JFXSpinner;
import controller.cell.ProcessingProductCell;
import controller.interfaces.TabItem;
import gui.components.TabPaneComponent;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import model.list.ProductListDTO;
import model.processing.ProcessorManager;
import model.processing.workflow.WorkflowType;
import model.products.ProductDTO;

import java.awt.image.BufferedImage;
import java.io.IOException;


public class ProcessingPreviewController implements TabItem {
    @FXML
    private ListView<ProductDTO> productListView;
    @FXML
    private AnchorPane imagePreview;
    @FXML
    private AnchorPane spinnerPane;
    @FXML
    private ImageView image;
    @FXML
    private JFXSpinner spinnerWait;
    @FXML
    private Button executeProcessing;

    private ProductListDTO productListDTO;
    private ProcessorManager processor;
    private Point2D dragInitialCoordinate;
    private TabPaneComponent tabPaneComponent;
    private Parent parent;

    public ProcessingPreviewController(ProductListDTO productListDTO, ProcessorManager processor) {
        this.productListDTO = productListDTO;
        this.processor = processor;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/ProcessingPreviewView.fxml"));
        fxmlLoader.setController(this);
        parent = null;
        try {
            parent = fxmlLoader.load();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public void setTabPaneComponent(TabPaneComponent component) {
        this.tabPaneComponent = component;
    }

    @Override
    public Parent getView() {
        return parent;
    }

    @Override
    public Task<Parent> start() {
        return new Task<Parent>() {
            @Override
            protected Parent call() throws Exception {
                init();
                return getView();
            }
        };
    }

    @Override
    public String getName() {
        return "Processing " + productListDTO.getName();
    }

    @Override
    public String getItemId() {
        return "P:"+productListDTO.getId();
    }

    public void init() {
        productListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        productListView.setCellFactory(e->new ProcessingProductCell());
        Image image = this.image.getImage();
        productListView.setItems(FXCollections.observableArrayList(productListDTO.getValidProducts()));
        this.image.setViewport(new Rectangle2D(0,0,image.getWidth(),image.getHeight()));
        hideSpinner();
        onSelectionInProductListViewStartPreviewProcessing();
        onScrollInImageViewZoomImage();
        onDragInImageViewMoveImage();
        onDragMoveDragImage();
    }

    private void onSelectionInProductListViewStartPreviewProcessing() {
        productListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                showSpinner();
                Task<BufferedImage> task = tabPaneComponent.getMainController().getProcessor().process(newValue, Lists.newArrayList(productListDTO.getProductsAreasOfWorks().get(newValue).get(0)), productListDTO.getWorkflow(WorkflowType.valueOf(newValue.getProductType())),productListDTO.getName(), true);

                task.setOnSucceeded(e-> {
                    WritableImage writableImage = SwingFXUtils.toFXImage(task.getValue(), null);
                    image.setImage(writableImage);
                    image.setFitWidth(812.0);
                    image.setFitHeight(512.0);
                    image.setViewport(new Rectangle2D(0,0,812,512));
                    hideSpinner();
                });
                new Thread(task).start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void onScrollInImageViewZoomImage() {
        image.setOnScroll(event -> {
            Rectangle2D viewport = image.getViewport();
            double percent = (event.getDeltaY() / image.getImage().getWidth());
            double deltaW = viewport.getWidth() * percent;
            double deltaH = viewport.getHeight() * percent;
            image.setViewport(new Rectangle2D(viewport.getMinX(),viewport.getMinY(),viewport.getWidth()-deltaW,viewport.getHeight()-deltaH));
        });
    }

    public void onDragInImageViewMoveImage() {
        image.setOnMousePressed(this::setBaseDraggedPosition);
    }

    public void onDragMoveDragImage() {
        image.setOnMouseDragged(event -> {
            Rectangle2D viewport = image.getViewport();
            double difX = event.getSceneX() - dragInitialCoordinate.getX();
            double difY = event.getSceneY() - dragInitialCoordinate.getY();
            image.setViewport(new Rectangle2D(viewport.getMinX()-difX*2.0, viewport.getMinY()-difY*2.0,viewport.getWidth(),viewport.getHeight()));
            setBaseDraggedPosition(event);
        });
    }

    private void setBaseDraggedPosition(MouseEvent e) {
        dragInitialCoordinate = new Point2D(e.getSceneX(),e.getSceneY());
    }

    public void setProductList(ProductListDTO productListView) {
        this.productListDTO = productListView;
        this.productListView.setItems(FXCollections.observableArrayList(productListDTO.getValidProducts()));
    }

    public void setProcessing(ProcessorManager processor) {
        this.processor = processor;
    }

    private void showSpinner(){
        setSpinnerManaged(true);
    }

    private void hideSpinner(){
        setSpinnerManaged(false);
    }

    private void setSpinnerManaged(boolean managed) {
        spinnerPane.setVisible(managed);
        spinnerPane.setManaged(managed);
        spinnerWait.setVisible(managed);
        spinnerWait.setManaged(managed);
    }


}
