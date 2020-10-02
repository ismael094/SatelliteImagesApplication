package gui.components;

import controller.SatelliteApplicationController;
import controller.list.ListController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.EventType;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import jfxtras.styles.jmetro.MDL2IconFont;
import model.ProductList;
import model.products.Product;

import java.awt.event.MouseEvent;

public class ListTreeViewComponent extends TreeView<Object> implements Component  {

    private final SatelliteApplicationController mainController;

    public ListTreeViewComponent(SatelliteApplicationController mainController) {
        super();
        this.mainController = mainController;
    }

    @Override
    public void init() {

        TreeItem<Object> pL = new TreeItem<>("My lists");
        setRoot(pL);
        //setShowRoot(false);
        //pL.getChildren().add(new TreeItem<>(mainController.getUserProductList()));

        setOnMouseClicked();
        //setOnMouseEntered();
    }

    private void setOnMouseEntered() {
        setOnMouseEntered(event -> {
            TreeItem<Object> selectedItem = getFocusModel().getFocusedItem();
            ProductList productList = null;
            try {
                productList = mainController.getUserProductList().stream()
                        .filter(p -> p.getName().equals(selectedItem.getValue()))
                        .findAny()
                        .orElse(null);
            } finally {

            }

        });
    }

    private void setOnMouseClicked() {
        setOnMouseClicked(event -> {
            TreeItem<Object> selectedItem = getSelectionModel().getSelectedItem();
            if (selectedItem != null && !selectedItem.isLeaf())
                mainController.getUserProductList().stream()
                    .filter(p -> p.getName().equals(selectedItem.getValue()))
                    .findAny().ifPresent(productList -> mainController.getTabController().load(new ListController(productList)));

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

    public void reloadTree() {
        TreeItem<Object> root = getRoot();
        root.getChildren().clear();
        for (ProductList pL : mainController.getUserProductList()) {
            TreeItem<Object> treeItem = new TreeItem<>(pL.getName());
            GlyphsDude.setIcon(treeItem, FontAwesomeIcon.FOLDER);
            if (pL.getProducts().size() > 0) {
                pL.getProducts().forEach(p->{
                    TreeItem<Object> item = new TreeItem<>(p.getPlatformName() + "-" + p.getProductType());
                    GlyphsDude.setIcon(item, FontAwesomeIcon.IMAGE);
                    treeItem.getChildren().add(item);
                });
            }
            root.getChildren().add(treeItem);
        }
        root.setExpanded(true);
    }
}
