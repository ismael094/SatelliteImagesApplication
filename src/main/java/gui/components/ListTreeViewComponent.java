package gui.components;

import controller.SatelliteApplicationController;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import model.ProductList;
import model.products.Product;

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

        getSelectionModel().selectedIndexProperty().addListener(((observable, oldValue, newValue) -> {
            System.out.println(newValue.intValue());
            /*if (newValue.getValue() instanceof ProductList) {
                //ListController controller = new ListController((ProductList)newValue.getValue());
                //listTreeView(controller);
            }*/
        }));
    }

    @Override
    public Parent getView() {
        return this;
    }

    public void reloadTree() {
        TreeItem<Object> root = getRoot();
        root.getChildren().clear();
        for (ProductList pL : mainController.getUserProductList()) {
            TreeItem<Object> treeItem = new TreeItem<>(pL);
            for (Product product : pL.getProducts())
                treeItem.getChildren().add(new TreeItem<>(product));
            root.getChildren().add(treeItem);
        }
    }
}
