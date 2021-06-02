package com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel;

import java.util.ArrayList;
import java.util.List;

public class Apm {
    List<String> dynatrace_tags;
    List<DynatraceAnomalyRule> dynatrace_anomaly_rules;

    public Apm()
    {
        dynatrace_tags=new ArrayList<>();
        dynatrace_tags=new ArrayList<>();
    }

    public List<String> getDynatrace_tags() {
        return dynatrace_tags;
    }

    public void setDynatrace_tags(List<String> dynatrace_tags) {
        this.dynatrace_tags = dynatrace_tags;
    }

    public List<DynatraceAnomalyRule> getDynatrace_anomaly_rules() {
        return dynatrace_anomaly_rules;
    }

    public void setDynatrace_anomaly_rules(List<DynatraceAnomalyRule> dynatrace_anomaly_rules) {
        this.dynatrace_anomaly_rules = dynatrace_anomaly_rules;
    }
}
