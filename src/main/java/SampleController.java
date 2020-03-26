import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.ResourceBundle;

public class SampleController {
    @FXML
    public ImageView image;
    private Searcher searcher;
    @FXML
    public ListView<Product> list;
    @FXML
    public TextArea textArea;

    @FXML
    public ChoiceBox<String> sateliteList;

    @FXML
    public DatePicker dateStart;

    @FXML
    public DatePicker dateFinish;
    @FXML
    public URL location;

    @FXML
    public ResourceBundle resources;

    public SampleController() {
        searcher = new Searcher();
    }

    public void initialize() {
        sateliteList.setItems(FXCollections.observableArrayList(
                "S1", "S2", "S3"));
        sateliteList.setValue("S1");
        list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Product>() {
            @Override
            public void changed(ObservableValue<? extends Product> observable, Product oldValue, Product newValue) {
                textArea.setText(newValue.getInfo());
            }
        });
    }
    public void search(ActionEvent actionEvent) {
        if (dateStart.getValue() != null && dateFinish.getValue() != null && dateFinish.getValue().compareTo(dateStart.getValue()) > 0) {
            list.getItems().clear();
            LocalDate value = dateStart.getValue();
            LocalDate value2 = dateFinish.getValue();
            String value1 = sateliteList.getValue();
            Filter filter = new Filter();
            filter.add(new FilterItemStartWith("Name",value1));
            filter.add(new FilterItemDateTime("IngestionDate",ComparisonOperators.GE,value.atTime(0,0, 0, 1600)));
            System.out.println(value.atTime(0,0, 0, 0000));
            filter.add(new FilterItemDateTime("IngestionDate",ComparisonOperators.LE,value2.atTime(23,59, 59, 1600)));
            System.out.println(filter.evaluate());
            List<Product> images = searcher.getImages(filter);
            list.getItems().addAll(images);
        }
    }

    public void showProduct(MouseEvent mouseEvent) {
        Product selectedItem = list.getSelectionModel().getSelectedItem();

    }
}
