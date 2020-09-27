package controller.search;

import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.io.IOException;

public interface SearchController {
    void search();
    Parent getView();
    Task<Parent> start();
    String getId();
}
