package com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel;

public class DynatraceAnomalyRule {
    String metric_id;
    enum Operator {
        ABOVE, BELOW
    }
    enum Severity {
        AVAILABILITY, CUSTOM_ALERT, ERROR, INFO, PERFORMANCE, RESOURCE_CONTENTION;
    }
    Operator operator;
    String value;
    Severity severity;


    public String getMetric_id() {
        return metric_id;
    }

    public void setMetric_id(String metric_id) {
        this.metric_id = metric_id;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }
}
