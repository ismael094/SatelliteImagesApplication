package controller.processing;

import controller.interfaces.TabItem;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gui.components.TabPaneComponent;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.processing.workflow.Sentinel1GRDDefaultWorkflowDTO;
import model.processing.workflow.WorkflowDTO;
import model.processing.workflow.WorkflowType;
import model.processing.workflow.operation.Operator;
import model.products.ProductDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.io.ParseException;
import utils.AlertFactory;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PreviewController implements TabItem {
    @FXML
    private Button generatePreview;
    @FXML
    private AnchorPane map;
    @FXML
    private PreviewMapController mapController;
    @FXML
    private AnchorPane image;
    @FXML
    private PreviewImageController imageController;
    @FXML
    private HBox bands;

    private final ProductDTO product;
    private final String area;
    private WorkflowDTO workflowDTO;
    private String path;
    private TabPaneComponent tabComponent;
    private Parent parent;
    private List<RadioButton> bandsCheckout;

    static final Logger logger = LogManager.getLogger(PreviewController.class.getName());

    public PreviewController(ProductDTO product, String area, WorkflowDTO workflowDTO, String path) {
        this.product = product;
        this.area = area;
        this.workflowDTO = workflowDTO;
        this.path = path;
        parent = null;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PreviewView.fxml"));
        fxmlLoader.setController(this);
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //JMetro jMetro = ThemeConfiguration.getJMetroStyled();
    }

    public void initData() throws ParseException {
        mapController.setAreaOfWork(area);
        GlyphsDude.setIcon(generatePreview, FontAwesomeIcon.ROCKET);
        createBands();

        generatePreview.setOnAction(e->{
            String area = mapController.getArea();
            try {
                process(area);
            } catch (Exception exception) {
                Platform.runLater(()->{
                    logger.atError().log("Error while generating the preview of product {}",product.getTitle());
                    AlertFactory.showErrorDialog("Error", "Processing Error", "Error while processing");
                });
            }
        });
    }

    private void createBands() {
        if (workflowDTO == null)
            workflowDTO = new Sentinel1GRDDefaultWorkflowDTO();
        this.bandsCheckout = new ArrayList<>();
        RadioButton sigma0_vv = new RadioButton("Sigma0_VV");
        sigma0_vv.setOnAction(e->{
            workflowDTO.getOperation(Operator.SUBSET).getParameters().put("sourceBands", sigma0_vv.getText());
        });
        RadioButton sigma0_vh = new RadioButton("Sigma0_VH");
        sigma0_vh.setOnAction(e->{
            workflowDTO.getOperation(Operator.SUBSET).getParameters().put("sourceBands", sigma0_vh.getText());
        });

        bandsCheckout.add(sigma0_vh);
        bandsCheckout.add(sigma0_vv);
        loadBands(bandsCheckout);

    }

    private void loadBands(List<RadioButton> checkboxes) {
        ToggleGroup toggleGroup = new ToggleGroup();
        toggleGroup.getToggles().addAll(checkboxes);
        bands.getChildren().clear();
        bands.getChildren().addAll(checkboxes);
    }

    @Override
    public void setTabPaneComponent(TabPaneComponent component) {
        this.tabComponent = component;
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
                initData();
                return getView();
            }
        };
    }

    @Override
    public String getName() {
        return "Preview " + product.getProductType();
    }

    @Override
    public String getItemId() {
        return "Preview";
    }

    private void process(String area) throws Exception {
        Task<BufferedImage> task = tabComponent.getMainController().getProcessor().process(product, Collections.singletonList(area), workflowDTO, path, true);

        task.setOnFailed(e->{
            AlertFactory.showErrorDialog("Error","","Error while setting preview image");
            logger.atError().log("Error processing preview {}",e.getSource().getException().getLocalizedMessage());
        });

        task.setOnSucceeded(e-> {
            try {
                imageController.setImage(SwingFXUtils.toFXImage(task.get(),null));
            } catch (InterruptedException | ExecutionException interruptedException) {
                interruptedException.printStackTrace();
            }
        });
        new Thread(task).start();
    }
}
