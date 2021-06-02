package com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.DEFAULT_BRANCH;

public class TestingScript {
    String branch;
    String repository;
    Boolean isSecured;
    List<Project> project;

    public TestingScript(String branch, String repository,Boolean isSecured, List<Project> project) {
        this.branch = branch;
        this.repository = repository;
        this.project = project;

        if(isSecured == null)
            this.isSecured=false;
        else
            this.isSecured=isSecured;
    }

    public Boolean isSecured() {
        if(isSecured==null)
            return false;
        else
            return isSecured;
    }

    public void setSecured(Boolean secured) {
        isSecured = secured;
    }

    public TestingScript() {
        project = new ArrayList<>();
    }

    public String getBranch() {
        if(branch==null)
            branch=DEFAULT_BRANCH;

        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public List<Project> getProject() {
        return project;
    }

    public void setProject(List<Project> project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return "TestingScript{" +
                "repository='" + repository + '\'' +
                ",issecured="+isSecured().toString() +
                ",branch='"+ getBranch() +"\'"+
                ", project=" + "[" +project.stream().map(project1 -> {return project1.toString();}).collect(Collectors.joining(",")) +"]"+
                '}';
    }
}
