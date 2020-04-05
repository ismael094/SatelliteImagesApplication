package gui.dialog;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Product;
import model.ProductList;

public class CreateListDialog extends Stage {

    private final VBox group;

    private final ListView<ProductList> productList;

    public CreateListDialog(ListView<ProductList> productList) {
        this.group = new VBox();
        this.productList = productList;
    }

    public void init() {
        initStyle(StageStyle.UTILITY);
        group.getChildren().addAll(new Label("This is a test"), productList);
        Button btn = new Button("Cerrar");
        btn.setOnAction(e -> {
            close();
        });
        Scene scene = new Scene(group);
        setSize();
        setScene(scene);
    }

    private void setSize() {
        setWidth(600);
        setHeight(400);
    }
}
