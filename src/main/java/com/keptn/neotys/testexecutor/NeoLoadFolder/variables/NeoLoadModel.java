package com.keptn.neotys.testexecutor.NeoLoadFolder.variables;

import java.util.List;

public class NeoLoadModel {
    private List<NlConstants> variables;

    public NeoLoadModel(List<NlConstants> variables) {
        this.variables = variables;
    }

    public List<NlConstants> getVariables() {
        return variables;
    }

    public void setVariables(List<NlConstants> variables) {
        this.variables = variables;
    }
}
