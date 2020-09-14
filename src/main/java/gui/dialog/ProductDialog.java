package gui.dialog;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.ProductOData;

public class ProductDialog extends Stage {
    private ProductOData productOData;
    private VBox group;

    public ProductDialog(ProductOData productOData) {
        this.productOData = productOData;
        this.group = new VBox();
    }

    public void init() {
        initStyle(StageStyle.UTILITY);
        addLabelAndText("Name", productOData.getName());
        addLabelAndText("ID", productOData.getId());
        addLabelAndText("CreationDate", productOData.getCreationDate());
        addLabelAndText("URL", productOData.getUrlImg());

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
