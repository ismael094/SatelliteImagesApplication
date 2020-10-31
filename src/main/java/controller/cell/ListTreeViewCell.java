package controller.cell;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import gui.components.treeViewComponent.TreeViewNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import model.list.ProductListDTO;
import model.products.ProductDTO;

import java.io.IOException;

public class ListTreeViewCell extends TreeCell<TreeViewNode> {
    private FXMLLoader loader;

    @FXML
    private Pane root;
    @FXML
    private Label title;

    @Override
    protected void updateItem(TreeViewNode node, boolean empty) {
        super.updateItem(node, empty);

        if(empty || node == null) {

            setText(null);
            setGraphic(null);

        } else {
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/fxml/ListTreeViewCell.fxml"));
                loader.setController(this);
                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            prefWidthProperty().bind(getTreeView().prefWidthProperty().subtract(2));
            setMaxWidth(Control.USE_PREF_SIZE);
            title.setText(node.getName());
            if (node.getNode() instanceof ProductListDTO) {
                GlyphsDude.setIcon(title, FontAwesomeIcon.FOLDER);
            } else if (node.getNode() instanceof ProductDTO) {
                GlyphsDude.setIcon(title, FontAwesomeIcon.IMAGE);
            }
            setText(null);
            setGraphic(root);
        }

    }

}
