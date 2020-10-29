package gui.events;

import controller.MainController;
import controller.interfaces.TabItem;
import controller.list.ListInformationController;
import gui.ExecutedEvent;
import javafx.event.ActionEvent;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import model.events.EventType;
import model.list.ProductListDTO;
import utils.FileUtils;

import java.util.List;

public class DownloadProductListEvent extends Event {

    public DownloadProductListEvent(MainController controller) {
        super(controller);
    }

    @Override
    public void handle(ActionEvent event) {
        Tab active = mainController.getTabComponent().getActive();
        TabItem controllerOf = mainController.getTabComponent().getControllerOf(active);
        ProductListDTO list;
        if (controllerOf instanceof ListInformationController) {
            ListInformationController listController = (ListInformationController)controllerOf;
            list = listController.getProductList();

        } else {
            List<ProductListDTO> productList = showAndGetList(SelectionMode.SINGLE,"Select list to edit");
            if (productList.size() == 0)
                return;
            else
                list = productList.get(0);
        }

        FileUtils.saveObjectToJson(list);

        mainController.fireEvent(new ExecutedEvent(this, EventType.DOWNLOAD,"Downloading list "+list.getName()));

        mainController.getDownloader().download(list);
    }
}
