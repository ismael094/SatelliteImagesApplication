package gui.components;

import com.google.common.collect.ImmutableSortedMap;
import controller.SatelliteApplicationController;
import controller.cell.ListTreeViewCell;
import controller.cell.ProductResultListCellController;
import controller.list.ListInformationController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gui.components.listener.ComponentChangeListener;
import gui.components.listener.ComponentEventType;
import gui.components.listener.ToolbarComponentEvent;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Pair;
import model.list.ProductListDTO;
import services.entities.ProductList;

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
                mainController.getUserProductList().stream()
                        .filter(p -> p.getId().equals(productListDTO.getId()))
                        .findAny().ifPresent(productList -> mainController.getTabController().load(new ListInformationController(productList)));
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
    public void addComponentListener(ComponentEventType type, ComponentChangeListener listener) {
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
                pL.getProducts().forEach(p->{
                    TreeItem<Pair<String,Object>> item = new TreeItem<>(new Pair<>(p.getPlatformName() + "-" + p.getProductType(),p));
                    GlyphsDude.setIcon(item, FontAwesomeIcon.IMAGE);
                    treeItem.getChildren().add(item);
                });
            }
            getRoot().getChildren().add(treeItem);
        }
        getRoot().setExpanded(true);
    }
}
