package gui.components;

import controller.SatelliteApplicationController;
import controller.cell.ListTreeViewCell;
import controller.interfaces.ProductTabItem;
import controller.interfaces.TabItem;
import controller.list.ListInformationController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.collections.FXCollections;
import javafx.scene.control.Tab;
import model.events.EventType;
import model.listeners.ComponentChangeListener;
import model.events.ToolbarComponentEvent;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Pair;
import model.list.ProductListDTO;
import model.products.ProductDTO;
import utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class ListTreeViewComponent extends TreeView<Pair<String,Object>> implements Component  {

    private final SatelliteApplicationController mainController;
    private final List<ComponentChangeListener> listTreeViewListener;

    public ListTreeViewComponent(SatelliteApplicationController mainController) {
        super();
        this.mainController = mainController;
        this.listTreeViewListener = new ArrayList<>();
    }

    @Override
    public void init() {

        TreeItem<Pair<String,Object>> pL = new TreeItem<>(new Pair<>("My list",null));
        setRoot(pL);
        setCellFactory(e -> new ListTreeViewCell());
        OnMouseClickedOpenListController();
        getChildren().addListener((ListChangeListener<Node>) c -> {
            System.out.println("CHANGE?");
        });
    }

    private void setOnMouseEntered() {
        setOnMouseEntered(event -> {
            TreeItem<Pair<String,Object>> selectedItem = getFocusModel().getFocusedItem();
            ProductListDTO productListDTO = null;
            try {
                productListDTO = mainController.getUserProductList().stream()
                        .filter(p -> selectedItem.getValue().getValue() instanceof ProductListDTO)
                        .findAny()
                        .orElse(null);
            } finally {
                ;
            }

        });
    }

    private void OnMouseClickedOpenListController() {
        setOnMouseClicked(event -> {
            TreeItem<Pair<String,Object>> selectedItem = getSelectionModel().getSelectedItem();
            if (selectedItem.getValue().getValue() instanceof ProductListDTO) {
                ProductListDTO productListDTO = (ProductListDTO)selectedItem.getValue().getValue();
                mainController.getTabController().load(new ListInformationController(productListDTO,mainController.getDownload()));
            } else {
                TreeItem<Pair<String, Object>> parent = selectedItem.getParent();
                if (parent != null && parent.getValue().getValue() instanceof ProductListDTO) {
                    ProductListDTO productListDTO = (ProductListDTO)parent.getValue().getValue();
                    Tab tab = mainController.getTabController().get(productListDTO.getId().toString());
                    TabItem controllerOf = mainController.getTabController().getControllerOf(tab);
                    ((ProductTabItem)controllerOf).setSelectedProducts(FXCollections.observableArrayList((ProductDTO)selectedItem.getValue().getValue()));
                }
            }

        });
    }

    @Override
    public Parent getView() {
        return this;
    }

    @Override
    public SatelliteApplicationController getMainController() {
        return mainController;
    }

    @Override
    public void addComponentListener(EventType.ComponentEventType type, ComponentChangeListener listener) {
        this.listTreeViewListener.add(listener);
    }

    @Override
    public void fireEvent(ToolbarComponentEvent event) {
        this.listTreeViewListener.forEach(l-> l.onComponentChange(event));
    }

    public void reload() {
        Platform.runLater(this::reloadTree);
    }

    private void reloadTree() {
        getRoot().getChildren().clear();
        for (ProductListDTO pL : mainController.getUserProductList()) {
            TreeItem<Pair<String,Object>> treeItem = new TreeItem<>(new Pair<>(pL.getName(),pL));
            GlyphsDude.setIcon(treeItem, FontAwesomeIcon.FOLDER);
            if (pL.getProducts().size() > 0) {
                int i = 1;
                for (ProductDTO p : pL.getProducts()) {
                    TreeItem<Pair<String,Object>> item = new TreeItem<>(new Pair<>(""+i+" " + p.getPlatformName() + "-" + p.getProductType(),p));
                    GlyphsDude.setIcon(item, FontAwesomeIcon.IMAGE);
                    treeItem.getChildren().add(item);
                    i++;
                }

            }
            getRoot().getChildren().add(treeItem);
        }
        getRoot().setExpanded(true);
    }
}
