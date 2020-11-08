package gui.events;

import controller.MainController;
import controller.interfaces.ProcessingResultsTabItem;
import controller.interfaces.TabItem;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import model.postprocessing.ProcessingResults;
import model.postprocessing.algorithms.Algorithm;
import model.postprocessing.algorithms.MedianFilterAlgorithm;
import org.apache.commons.io.IOUtils;
import services.algorithms.AlgorithmsExecutor;
import utils.AlertFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class ExecuteAlgorithmEvent extends Event {
    private Algorithm algorithm;

    public ExecuteAlgorithmEvent(MainController controller, Algorithm algorithm) {
        super(controller);
        this.algorithm = algorithm;
    }

    @Override
    public void handle(ActionEvent event) {
        TabItem controllerOf = mainController.getTabComponent().getControllerOf(mainController.getTabComponent().getActive());
        if (controllerOf instanceof ProcessingResultsTabItem) {
            ProcessingResults processingResults = ((ProcessingResultsTabItem) controllerOf).getProcessingResults();
            Task<Boolean> task = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return new AlgorithmsExecutor().execute(algorithm, processingResults);
                }
            };

            task.setOnFailed(e->{
                AlertFactory.showErrorDialog("Error","Error  with algorithm","Error while executing algorithm "+algorithm.getName().split("\\.")[0]);
            });

            task.setOnSucceeded(e->{
                if (task.getValue())
                    AlertFactory.showSuccessDialog("Algorithm completed","Algorithm completed",
                        "Algorithm successfully executed. Output is: " + task.getValue());
                else
                    AlertFactory.showErrorDialog("Error","Error  with algorithm","Error while executing algorithm "+algorithm.getName().split("\\.")[0]);
            });

            new Thread(task).start();
        }
    }
}
