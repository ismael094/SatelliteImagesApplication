package gui.events;

import controller.MainController;
import controller.interfaces.ProcessingResultsTabItem;
import controller.interfaces.TabItem;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import model.postprocessing.ProcessingResults;
import org.apache.commons.io.IOUtils;
import services.algorithms.AlgorithmsExecutor;
import utils.AlertFactory;

import java.io.File;

public class ExecuteAlgorithmEvent extends Event {
    private File algorithm;

    public ExecuteAlgorithmEvent(MainController controller, File algorithm) {
        super(controller);
        this.algorithm = algorithm;
    }

    @Override
    public void handle(ActionEvent event) {
        TabItem controllerOf = mainController.getTabComponent().getControllerOf(mainController.getTabComponent().getActive());
        if (controllerOf instanceof ProcessingResultsTabItem) {
            ProcessingResults processingResults = ((ProcessingResultsTabItem) controllerOf).getProcessingResults();
            ProcessBuilder execute = new AlgorithmsExecutor().execute(algorithm, processingResults);
            Task<String> task = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    Process start = execute.start();
                    String output = IOUtils.toString(start.getInputStream(),"UTF_8");
                    int i = start.waitFor();
                    if (i!=0)
                        throw new Exception("Error executing algorithm");
                    return output;
                }
            };

            task.setOnFailed(e->{
                AlertFactory.showErrorDialog("Error","Error  with algorithm","Error while executing algorithm "+algorithm.getName().split("\\.")[0]);
            });

            task.setOnSucceeded(e->{

                AlertFactory.showSuccessDialog("Algorithm completed","Algorithm completed",
                        "Algorithm successfully executed. Output is: " + task.getValue());
            });

            new Thread(task).start();
        }
    }
}
