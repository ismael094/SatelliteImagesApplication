package model;

import model.processing.workflow.Operation;
import model.processing.Operator;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class Operation_ {
    @Test
    public void add_new_operation() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("orbitType", "Sentinel Precise (Auto Download)");
        parameters.put("polyDegree", 3);
        Operation operation = new Operation(Operator.APPLY_ORBIT_FILE, parameters);
        assertThat(operation.getName()).isEqualTo(Operator.APPLY_ORBIT_FILE);
        assertThat(operation.getParameters()).isNotNull();
        assertThat(operation.getParameters().size()).isGreaterThan(0);
        assertThat(operation.getParameters().get("polyDegree")).isEqualTo(3);
    }
}
