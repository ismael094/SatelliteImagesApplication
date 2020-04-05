package gui.dialog;

import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Product;

public class ProductDialog extends Stage {
    private Product product;
    private VBox group;

    public ProductDialog(Product product) {
        this.product = product;
        this.group = new VBox();
    }

    public void init() {
        initStyle(StageStyle.UTILITY);
        addLabelAndText("Name",product.getName());
        addLabelAndText("ID",product.getId());
        addLabelAndText("CreationDate",product.getCreationDate());
        addLabelAndText("URL",product.getUrlImg());

        Scene scene = new Scene(group);
        setSize();
        setScene(scene);
    }

    public void addLabelAndText(String label, String text) {
        Label lb = new Label(label);
        lb.setFont(new Font("Arial", 18));
        group.getChildren().addAll(new HBox(lb,new Text(text)));
    }

    private void setSize() {
        setWidth(600);
        setHeight(400);
    }


}
