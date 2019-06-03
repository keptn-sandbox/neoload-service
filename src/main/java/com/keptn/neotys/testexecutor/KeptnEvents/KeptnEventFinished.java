package com.keptn.neotys.testexecutor.KeptnEvents;

public class KeptnEventFinished {

    //  "githuborg":"keptn-tiger",
    //      "project":"sockshop",
    //      "teststrategy":"functional",
    //      "deploymentstrategy":"direct",
    //      "stage":"dev",
    //      "service":"carts",
    //      "image":"10.11.245.27:5000/sockshopcr/carts",
    //      "tag":"0.6.7-16"
    String githuborg;
    String project;
    String teststrategy;
    String deploymentstrategy;
    String stage;
    String service;
    String image;
    String tag;

    public KeptnEventFinished(String githuborg, String project, String teststrategy, String deploymentstrategy, String stage, String service, String image, String tag) {
        this.githuborg = githuborg;
        this.project = project;
        this.teststrategy = teststrategy;
        this.deploymentstrategy = deploymentstrategy;
        this.stage = stage;
        this.service = service;
        this.image = image;
        this.tag = tag;
    }

    public String getGithuborg() {
        return githuborg;
    }

    public void setGithuborg(String githuborg) {
        this.githuborg = githuborg;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getTeststrategy() {
        return teststrategy;
    }

    public void setTeststrategy(String teststrategy) {
        this.teststrategy = teststrategy;
    }

    public String getDeploymentstrategy() {
        return deploymentstrategy;
    }

    public void setDeploymentstrategy(String deploymentstrategy) {
        this.deploymentstrategy = deploymentstrategy;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String toString()
    {
        String output;
        return output="\"githuborg\":\""+this.getGithuborg()+"\",\n" +
                "      \"project\":\""+this.getProject()+"\",\n" +
                "      \"teststrategy\":\""+this.getTeststrategy()+"\",\n" +
                "      \"deploymentstrategy\":\""+this.getDeploymentstrategy()+"\",\n" +
                "      \"stage\":\""+this.getStage()+"\",\n" +
                "      \"service\":\""+this.getService()+"\",\n" +
                "      \"image\":\""+this.getImage()+"\",\n" +
                "      \"tag\":\""+this.getTag()+"\"";
    }
}
