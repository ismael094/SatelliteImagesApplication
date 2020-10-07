package utils.http;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Pair;
import services.CopernicusService;

import java.util.Map;

public class ServiceFactory extends Service<String> {

    private Map<String, Pair<String,String>> credentials;

    public static CopernicusService getCopernicusService() {
        return null;
    }

    @Override
    protected Task<String> createTask() {
        return null;
    }
}
