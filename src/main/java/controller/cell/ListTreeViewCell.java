package controller.cell;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Pair;
import model.list.ProductListDTO;
import model.products.ProductDTO;

import java.io.IOException;

public class ListTreeViewCell extends TreeCell<Pair<String,Object>> {
    private FXMLLoader loader;

    @FXML
    private Pane root;
    @FXML
    private Label title;

    @Override
    protected void updateItem(Pair<String,Object> product, boolean empty) {
        super.updateItem(product, empty);

        if(empty || product == null) {

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

            if (product.getValue() == null)
                title.setText("My lists");
            else if (product.getValue() instanceof ProductListDTO) {
                ProductListDTO productDTO = (ProductListDTO)product.getValue();
                title.setText(productDTO.getName());
                GlyphsDude.setIcon(title, FontAwesomeIcon.FOLDER);
            } else if (product.getValue() instanceof ProductDTO) {
                title.setText(product.getKey());
                GlyphsDude.setIcon(title, FontAwesomeIcon.IMAGE);
            }
            setText(null);
            setGraphic(root);
        }

    }

}
