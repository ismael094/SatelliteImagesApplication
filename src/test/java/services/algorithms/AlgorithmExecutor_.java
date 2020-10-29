package services.algorithms;

import model.postprocessing.ProcessingResults;
import org.junit.Before;
import org.junit.Test;
import services.algorithms.AlgorithmsExecutor;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class AlgorithmExecutor_ {
    private AlgorithmsExecutor executor;

    @Before
    public void init() {
        executor = new AlgorithmsExecutor();
    }

    @Test
    public void empty_algorithm_and_processing_results() {
        assertThat(executor.execute(new File("hello"),new ProcessingResults())).isNull();
    }

    @Test
    public void valid_data() {
        ProcessingResults processingResults = new ProcessingResults();
        processingResults.addFile(new File("src/test/java/services/algorithms/image.PNG"));
        assertThat(executor.execute(new File("src/test/java/services/algorithms/test.bat"),processingResults)).isNotNull();
        ProcessBuilder execute = executor.execute(new File("src/test/java/services/algorithms/test.bat"), processingResults);
        assertThat(execute.command().get(0)).isEqualTo("C:\\Users\\Ismael2\\IdeaProjects\\SatelliteImagesApplication\\src\\test\\java\\services\\algorithms\\test.bat");
        assertThat(execute.command().get(1)).isEqualTo("C:\\Users\\Ismael2\\IdeaProjects\\SatelliteImagesApplication\\src\\test\\java\\services\\algorithms\\image.PNG");
    }
}
