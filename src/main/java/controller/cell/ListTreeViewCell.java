package controller.cell;

import controller.GTMapSearchController;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.util.Pair;
import model.list.ProductListDTO;
import model.products.ProductDTO;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListTreeViewCell extends TreeCell<Pair<String,Object>> {
    private FXMLLoader loader;

    @FXML
    private Pane root;
    @FXML
    private Label title;

    private Object product;

    @Override
    protected void updateItem(Pair<String,Object> product, boolean empty) {
        super.updateItem(product, empty);

        if(empty || product == null) {

            setText(null);
            setGraphic(null);

        } else {
            this.product = product;
            if (loader == null) {
                loader = new FXMLLoader(getClass().getResource("/fxml/ListTreeViewCell.fxml"));
                loader.setController(this);
                try {
                    loader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (product.getValue() == null)
                title.setText("My lists");
            else if (product.getValue() instanceof ProductListDTO) {
                ProductListDTO productDTO = (ProductListDTO)product.getValue();
                title.setText(productDTO.getName());
                GlyphsDude.setIcon(title, FontAwesomeIcon.FOLDER);
            } else if (product.getValue() instanceof ProductDTO) {
                ProductDTO productDTO = (ProductDTO)product.getValue();
                title.setText(product.getKey());
                GlyphsDude.setIcon(title, FontAwesomeIcon.IMAGE);
            }
            setText(null);
            setGraphic(root);
        }

    }

}
