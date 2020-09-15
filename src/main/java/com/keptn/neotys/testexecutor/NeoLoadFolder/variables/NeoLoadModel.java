package com.keptn.neotys.testexecutor.NeoLoadFolder.variables;

import com.keptn.neotys.testexecutor.NeoLoadFolder.ProjectSettings;

import java.util.ArrayList;
import java.util.List;

public class NeoLoadModel {
    private List<NlConstants> variables;
    private ProjectSettings projectSettings;

    public NeoLoadModel(List<NlConstants> variables) {
        this.variables = variables;
    }
    public NeoLoadModel() {
        this.variables = new ArrayList<>();
    }

    public ProjectSettings getProjectSettings() {
        return projectSettings;
    }

    public void setProjectSettings(ProjectSettings projectSettings) {
        this.projectSettings = projectSettings;
    }

    public List<NlConstants> getVariables() {
        return variables;
    }

    public void setVariables(List<NlConstants> variables) {
        this.variables = variables;
    }
}
