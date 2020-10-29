package gui.components;

import controller.MainController;
import controller.cell.ListTreeViewCell;
import controller.interfaces.ProductListTabItem;
import controller.interfaces.TabItem;
import controller.list.ListInformationController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gui.components.listener.ComponentEvent;
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

public class ListTreeViewComponent extends TreeView<Pair<String,Object>> implements Component  {

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
        TreeItem<Pair<String,Object>> pL = new TreeItem<>(new Pair<>("My list",null));
        setRoot(pL);
        setCellFactory(e -> new ListTreeViewCell());
        OnMouseClickedOpenListController();
        getChildren().addListener((ListChangeListener<Node>) c -> System.out.println("CHANGE?"));
        reload();
    }

    private void OnMouseClickedOpenListController() {
        setOnMouseClicked(event -> {
            TreeItem<Pair<String, Object>> selectedItem = getSelectionModel().getSelectedItem();
            if (event.getClickCount() > 1 && selectedItem.getValue().getValue() instanceof ProductListDTO) {
                ProductListDTO productListDTO = (ProductListDTO) selectedItem.getValue().getValue();
                mainController.getTabComponent().load(new ListInformationController(productListDTO, mainController.getDownloader()));
            } else {
                TreeItem<Pair<String, Object>> parent = selectedItem.getParent();
                if (parent != null && parent.getValue().getValue() instanceof ProductListDTO) {
                    ProductListDTO productListDTO = (ProductListDTO) parent.getValue().getValue();
                    TabItem controller = mainController.getTabComponent().getControllerOf(productListDTO.getId().toString());
                    if (controller != null) {
                        ((ProductListTabItem) controller).setSelectedProducts(FXCollections.observableArrayList((ProductDTO) selectedItem.getValue().getValue()));
                    }

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

    public void reload() {
        Platform.runLater(this::reloadTree);
    }

    private void reloadTree() {
        getRoot().getChildren().clear();
        for (ProductListDTO pL : mainController.getUserManager().getUser().getProductListsDTO()) {
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

    public ProductListDTO getSelected() {
        ObservableList<TreeItem<Pair<String, Object>>> selectedItems = getSelectionModel().getSelectedItems();
        if (selectedItems.size() > 0 && selectedItems.get(0).getValue().getValue() instanceof ProductListDTO) {
            return (ProductListDTO) selectedItems.get(0).getValue().getValue();
        }
        return null;
    }
}
