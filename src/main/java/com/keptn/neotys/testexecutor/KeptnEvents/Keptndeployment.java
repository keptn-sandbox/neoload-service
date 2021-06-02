package com.keptn.neotys.testexecutor.KeptnEvents;

import java.util.ArrayList;
import java.util.List;

public class Keptndeployment {
    List<String> deploymentURIsLocal=new ArrayList<>();
    List<String> deploymentURIsPublic=new ArrayList<>();
    List<String> deploymentNames=new ArrayList<>();
    String gitCommit;
    String deploymentstrategy;
    public Keptndeployment(List<String> deploymentURIsLocal, List<String> deploymentURIsPublic, List<String> deploymentNames ,String deploymentstrategy  ,String gitCommit )
    {
        this.gitCommit=gitCommit;
        this.deploymentstrategy=deploymentstrategy;
        this.deploymentURIsPublic = deploymentURIsPublic;
        this.deploymentNames=deploymentNames;
    }

    public List<String> getDeploymentURIsLocal() {
        return deploymentURIsLocal;
    }

    public void setDeploymentURIsLocal(List<String> deploymentURIsLocal) {
        this.deploymentURIsLocal = deploymentURIsLocal;
    }

    public List<String> getDeploymentURIsPublic() {
        return deploymentURIsPublic;
    }

    public void setDeploymentURIsPublic(List<String> deploymentURIsPublic) {
        this.deploymentURIsPublic = deploymentURIsPublic;
    }

    public List<String> getDeploymentNames() {
        return deploymentNames;
    }

    public void setDeploymentNames(List<String> deploymentNames) {
        this.deploymentNames = deploymentNames;
    }

    public String getGitCommit() {
        return gitCommit;
    }

    public void setGitCommit(String gitCommit) {
        this.gitCommit = gitCommit;
    }

    public String getDeploymentstrategy() {
        return deploymentstrategy;
    }

    public void setDeploymentstrategy(String deploymentstrategy) {
        this.deploymentstrategy = deploymentstrategy;
    }
}
