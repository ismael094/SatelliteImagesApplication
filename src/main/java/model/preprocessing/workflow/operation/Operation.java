package model.preprocessing.workflow.operation;

import java.util.HashMap;
import java.util.Map;

public class Operation {
    private Operator operator;
    private Map<String, Object> parameters;

    public Operation(Operator operator, Map<String, Object> parameters) {
        this.operator = operator;
        this.parameters = parameters;
    }

    public Operation() {
        this.parameters = new HashMap<>();
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public void addParameter(String key, Object value) {
        parameters.putIfAbsent(key,value);
    }

    public void remove(String key) {
        parameters.remove(key);
    }

    public Operator getName() {
        return operator;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return "Operation{" +
                "operator=" + operator +
                ", parameters=" + parameters +
                '}';
    }
}
