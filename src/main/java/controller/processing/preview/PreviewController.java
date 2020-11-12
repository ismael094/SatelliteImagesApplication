package controller.processing.preview;

import controller.interfaces.TabItem;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gui.components.TabPaneComponent;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import model.preprocessing.workflow.WorkflowDTO;
import model.preprocessing.workflow.operation.Operation;
import model.preprocessing.workflow.operation.Operator;
import model.products.ProductDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.io.ParseException;
import utils.*;

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
    private ToggleGroup toggleGroup;

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
        bandsCheckout = new ArrayList<>();
        //JMetro jMetro = ThemeConfiguration.getJMetroStyled();
    }

    public void setArea(String area) {
        try {
            mapController.setAreaOfWork(area);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void initData() throws ParseException {
        mapController.setAreaOfWork(area);
        GlyphsDude.setIcon(generatePreview, FontAwesomeIcon.ROCKET);

        getOutputBandsOfWorkflow();

        onClickOnGenerateProcessPreview();
    }

    private void onClickOnGenerateProcessPreview() {
        generatePreview.setOnAction(e->{
            //If there are bands, one must be selected to process preview
            if (toggleGroup != null && !toggleGroup.getToggles().isEmpty() && toggleGroup.getSelectedToggle() == null) {
                AlertFactory.showErrorDialog("Error","Error","Select one band to process");
                return;
            }
            try {
                //Create task and set success and fail events
                process(mapController.getArea());
            } catch (Exception exception) {
                Platform.runLater(()->{
                    logger.atError().log("Error while generating the preview of product {}",product.getTitle());
                    AlertFactory.showErrorDialog("Error", "Processing Error", "Error while processing");
                });
            }
        });
    }

    private void getOutputBandsOfWorkflow() {
        if (workflowDTO == null)
            workflowDTO = ProcessingConfiguration.getDefaultWorkflow(product.getProductType());

        if (SatelliteHelper.isRadar(product.getPlatformName())) {
            setRadarBands();
        } else {
            setOpticalBands();
        }
    }

    private void setOpticalBands() {
        Operation op = workflowDTO.getOperation(Operator.WRITE);
        bands.getChildren().clear();
        if ((Boolean)op.getParameters().getOrDefault("generatePNG",false)) {
            String red = getValue(op.getParameters(),"red");
            String blue = getValue(op.getParameters(),"blue");
            String green = getValue(op.getParameters(),"green");
            bands.getChildren().addAll(new Label("Red: "+red+" - Blue: "+ blue+" - Green: "+green));
        }
    }

    private String getValue(Map<String,Object> parameters, String key) {
        return String.valueOf(parameters.get(key));
    }

    private void setRadarBands() {
        List<String> outputBands = ProductBandUtils.getOutputBands(product, workflowDTO);
        outputBands.forEach(b->{
            RadioButton radioButton = new RadioButton(b);
            radioButton.setId(b);
            radioButton.setOnAction(e->{
                workflowDTO.getOperation(Operator.SUBSET).getParameters().put("sourceBands", b);
            });
            bandsCheckout.add(radioButton);
        });

        loadBands(bandsCheckout);
    }

    private void loadBands(List<RadioButton> checkboxes) {
        toggleGroup = new ToggleGroup();
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
        return "Preview-"+product.getId();
    }

    private void process(String area) throws Exception {
        if (!WKTUtil.workingAreaContains(product.getFootprint(), area)) {
            AlertFactory.showErrorDialog("Error","","Area is not contain in product footprint");
            return;
        }
        Task<BufferedImage> task = tabComponent.getMainController().getProductProcessor().process(product, Collections.singletonList(area), workflowDTO, path, true);

        task.setOnFailed(e->{
            e.getSource().getException().printStackTrace();
            AlertFactory.showErrorDialog("Error","","Error while setting preview image " + e.getSource().getException().getLocalizedMessage());
            logger.atError().log("Error processing preview {}",e.getSource().getException().getLocalizedMessage());
        });

        task.setOnSucceeded(e-> {
            try {
                BufferedImage bufferedImage = task.get();
                if (bufferedImage == null) {
                    AlertFactory.showErrorDialog("Error","","Error while processing preview image ");
                    return;
                }
                imageController.setImage(SwingFXUtils.toFXImage(bufferedImage,null));
                AlertFactory.showSuccessDialog("Preview","Preview","Preview completed!");
            } catch (InterruptedException | ExecutionException interruptedException) {
                interruptedException.printStackTrace();
            }
        });

        AlertFactory.showSuccessDialog("Preview","Preview","Preview has been made! Wait until" +
                " the product is processed. This could last several minutes...");

        new Thread(task).start();
    }
}
