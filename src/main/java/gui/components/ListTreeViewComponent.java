package gui.components;

import controller.SatelliteApplicationController;
import controller.list.ListInformationController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.list.ProductListDTO;

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

        OnMouseClickedOpenListController();
        getChildren().addListener((ListChangeListener<Node>) c -> {
            System.out.println("CHANGE?");
        });
    }

    private void setOnMouseEntered() {
        setOnMouseEntered(event -> {
            TreeItem<Object> selectedItem = getFocusModel().getFocusedItem();
            ProductListDTO productListDTO = null;
            try {
                productListDTO = mainController.getUserProductList().stream()
                        .filter(p -> p.getName().equals(selectedItem.getValue()))
                        .findAny()
                        .orElse(null);
            } finally {
                ;
            }

        });
    }

    private void OnMouseClickedOpenListController() {
        setOnMouseClicked(event -> {
            TreeItem<Object> selectedItem = getSelectionModel().getSelectedItem();
            if (selectedItem != null && !selectedItem.isLeaf())
                mainController.getUserProductList().stream()
                    .filter(p -> p.getName().equals(selectedItem.getValue()))
                    .findAny().ifPresent(productList -> mainController.getTabController().load(new ListInformationController(productList)));

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

    public void reload() {
        Platform.runLater(this::reloadTree);
    }

    private void reloadTree() {
        getRoot().getChildren().clear();
        for (ProductListDTO pL : mainController.getUserProductList()) {
            TreeItem<Object> treeItem = new TreeItem<>(pL.getName());
            GlyphsDude.setIcon(treeItem, FontAwesomeIcon.FOLDER);

            if (pL.getProducts().size() > 0) {
                pL.getProducts().forEach(p->{
                    TreeItem<Object> item = new TreeItem<>(p.getPlatformName() + "-" + p.getProductType());
                    GlyphsDude.setIcon(item, FontAwesomeIcon.IMAGE);
                    treeItem.getChildren().add(item);
                });
            }
            getRoot().getChildren().add(treeItem);
        }
        getRoot().setExpanded(true);
    }
}
