package com.keptn.neotys.testexecutor.NeoLoadFolder.variables;

import com.keptn.neotys.testexecutor.NeoLoadFolder.ProjectSettings;

import java.util.ArrayList;
import java.util.List;

public class NeoLoadModel {
    private List<NlConstants> variables;
    private ProjectSettings project_settings;

    public NeoLoadModel(List<NlConstants> variables) {
        this.variables = variables;
    }
    public NeoLoadModel() {
        this.variables = new ArrayList<>();
    }

    public ProjectSettings getProject_settings() {
        return project_settings;
    }

    public void setProject_settings(ProjectSettings project_settings) {
        this.project_settings = project_settings;
    }

    public List<NlConstants> getVariables() {
        return variables;
    }

    public void setVariables(List<NlConstants> variables) {
        this.variables = variables;
    }
}
