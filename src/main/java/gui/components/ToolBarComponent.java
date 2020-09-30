package gui.components;

import controller.SatelliteApplicationController;
import controller.list.CreateListController;
import controller.search.SearchController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jfxtras.styles.jmetro.Style;
import model.products.Product;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class ToolBarComponent extends ToolBar implements Component{

    private final SatelliteApplicationController mainController;

    public ToolBarComponent(SatelliteApplicationController mainController) {
        super();
        this.mainController = mainController;
    }

    @Override
    public void init() {
        Button createList = new Button("");
        createList.setId("createList");
        createList.setTooltip(new Tooltip("Create new list"));
        Button addToList = new Button("");
        addToList.setId("addToList");
        Button selectAll = new Button("");
        selectAll.setId("selectAll");
        Button downloadSingle = new Button("");
        downloadSingle.setId("downloadSingle");
        Button downloadAll = new Button("");
        downloadAll.setId("downloadAll");
        getItems().addAll(createList,addToList,selectAll,downloadSingle,downloadAll);


        createList.setOnAction(j->{
            URL location = getClass().getResource("/fxml/CreateListView.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(location);
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            new JMetro(Style.LIGHT).setScene(scene);
            CreateListController controller = fxmlLoader.getController();
            Stage stage = new Stage();
            stage.setTitle("Create list");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.showAndWait();
            mainController.getUserProductList().add(controller.getProductList());
            mainController.getListTreeViewController().reloadTree();
        });

        addToList.setOnAction(e->{
            if (mainController.getUserProductList().size()>0) {
                SearchController searchController = mainController.getTabController().getSearchController();
                List<Product> openSearcher = searchController.getSelectedProducts();
                openSearcher.forEach(p->mainController.getUserProductList().get(0).addProduct(p));
                mainController.getListTreeViewController().reloadTree();
                //ListController listController = new ListController(products);
                //initControllerStage(listController);
            }

        });
    }

    @Override
    public Parent getView() {
        return this;
    }
}
