package gui.dialog;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.list.ProductListDTO;

public class CreateProductListDialog extends Stage {

    private final GridPane group;
    private final TextField name;
    private final TextField description;
    private ProductListDTO productListDTO;

    public CreateProductListDialog() {
        this.group = new GridPane();
        this.description = new TextField("Description");
        this.name = new TextField("Name");
        this.productListDTO = null;
    }

    public void init() {
        initStyle(StageStyle.UTILITY);
        Button btn = new Button("Create");
        /*btn.setOnAction(event -> {
            productList = new ProductList(name.getText(),description.getText());
            close();
        });*/

        Button btnCancel = new Button("Cancel");
        btnCancel.setOnAction(event -> {
            close();
        });
        group.add(new Label("Name:"),0,0);
        group.add(name,1,0);
        group.add(new Label("Description:"),0,1);
        group.add(description,1,1);
        group.add(btn,0,2);
        group.add(btnCancel,1,2);
        group.setAlignment(Pos.CENTER);
        Scene scene = new Scene(group);
        setSize();
        setScene(scene);
    }

    private void setSize() {
        setWidth(300);
        setHeight(200);
    }

    public ProductListDTO getProductList() {
        return productListDTO;
    }
}
