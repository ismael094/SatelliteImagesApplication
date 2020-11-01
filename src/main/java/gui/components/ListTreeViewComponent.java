package gui.components;

import controller.MainController;
import controller.cell.ListTreeViewCell;
import controller.interfaces.ProductListTabItem;
import controller.interfaces.TabItem;
import controller.list.ListInformationController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gui.components.listener.ComponentEvent;
import gui.components.treeViewComponent.TreeViewNode;
import gui.events.LoadTabItemEvent;
import gui.events.SelectProductInProductListEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.events.EventType;
import model.listeners.ComponentChangeListener;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Pair;
import model.list.ProductListDTO;
import model.products.ProductDTO;
import utils.gui.Observer;

import java.util.ArrayList;
import java.util.List;

public class ListTreeViewComponent extends TreeView<TreeViewNode> implements Component  {

    protected final MainController mainController;
    protected final List<ComponentChangeListener> listeners;
    protected List<Observer> observers;

    public ListTreeViewComponent(MainController mainController) {
        super();
        this.mainController = mainController;
        this.listeners = new ArrayList<>();
        observers = new ArrayList<>();
    }

    @Override
    public void init() {
        mainController.addListener(l->{
            if (l.getType() == EventType.LIST)
                reload();
        });
        TreeItem<TreeViewNode> pL = new TreeItem<>(new TreeViewNode("My list",null));
        setRoot(pL);
        setCellFactory(e -> new ListTreeViewCell());
        OnMouseClickedOpenListController();
        getChildren().addListener((ListChangeListener<Node>) c -> System.out.println("CHANGE?"));
        reload();
    }

    private void OnMouseClickedOpenListController() {
        setOnMouseClicked(event -> {
            TreeItem<TreeViewNode> selectedItem = getSelectionModel().getSelectedItem();
            if (event.getClickCount() > 1 && selectedItem.getValue().getNode() instanceof ProductListDTO) {
                ProductListDTO productListDTO = (ProductListDTO) selectedItem.getValue().getNode();
                new LoadTabItemEvent(mainController,new ListInformationController(productListDTO, mainController.getDownloader())).handle(null);
                //mainController.getTabComponent().load();
            } else {
                TreeItem<TreeViewNode> parent = selectedItem.getParent();
                if (parent != null && parent.getValue().getNode() instanceof ProductListDTO) {
                    new SelectProductInProductListEvent(mainController,(ProductListDTO) parent.getValue().getNode(),FXCollections.observableArrayList((ProductDTO) selectedItem.getValue().getNode())).handle(null);
                }
            }

        });
    }

    @Override
    public Parent getView() {
        return this;
    }

    @Override
    public MainController getMainController() {
        return mainController;
    }

    @Override
    public void addComponentListener(ComponentChangeListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void fireEvent(ComponentEvent event) {
        this.listeners.forEach(l-> l.onComponentChange(event));
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void updateObservers(Object args) {
        observers.forEach(o->o.update(args));
    }

    public void reload() {
        Platform.runLater(this::reloadTree);
    }

    private void reloadTree() {
        getRoot().getChildren().clear();
        for (ProductListDTO pL : mainController.getUserManager().getUser().getProductListsDTO()) {
            TreeItem<TreeViewNode> treeItem = new TreeItem<>(new TreeViewNode(pL.getName(),pL));
            GlyphsDude.setIcon(treeItem, FontAwesomeIcon.FOLDER);
            if (pL.getProducts().size() > 0) {
                int i = 1;
                for (ProductDTO p : pL.getProducts()) {
                    TreeItem<TreeViewNode> item = new TreeItem<>(new TreeViewNode(""+i+" " + p.getPlatformName() + "-" + p.getProductType(),p));
                    GlyphsDude.setIcon(item, FontAwesomeIcon.IMAGE);
                    treeItem.getChildren().add(item);
                    i++;
                }
            }
            getRoot().getChildren().add(treeItem);
        }
        getRoot().setExpanded(true);
    }

    public ProductListDTO getSelected() {
        ObservableList<TreeItem<TreeViewNode>> selectedItems = getSelectionModel().getSelectedItems();
        if (selectedItems.size() > 0 && selectedItems.get(0).getValue().getNode() instanceof ProductListDTO) {
            return (ProductListDTO) selectedItems.get(0).getValue().getNode();
        }
        return null;
    }
}
