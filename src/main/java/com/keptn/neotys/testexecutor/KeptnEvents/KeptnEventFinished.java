package com.keptn.neotys.testexecutor.KeptnEvents;


import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class KeptnEventFinished {

    //  "githuborg":"keptn-tiger",
    //      "project":"sockshop",
    //      "teststrategy":"functional",
    //      "deploymentstrategy":"direct",
    //      "stage":"dev",
    //      "service":"carts",
    //      "image":"10.11.245.27:5000/sockshopcr/carts",
    //      "tag":"0.6.7-16"
    private String githuborg;
    private static final String KEY_githuborg="githuborg";

    private String project;
    private static final String KEY_project="project";

    private String teststrategy;
    private static final String KEY_teststrategy="teststrategy";

    private String deploymentstrategy;

    private static final String KEY_deploymentstrategy="deploymentstrategy";
    private String stage;
    private static final String KEY_stage="stage";

    private String service;
    private static final String KEY_service="service";

    private String image;
    private static final String KEY_image="image";

    private String tag;
    private static final String KEY_tag="tag";

    private List<String> knowkeys= Arrays.asList(new String[]{KEY_deploymentstrategy, KEY_githuborg, KEY_image, KEY_project, KEY_service, KEY_stage, KEY_tag, KEY_teststrategy});
    private HashMap<String,Object> otherdata;

    private String testid;
    private static final String KEY_testid="neoload_testid";

    private String neoloadURL;
    private static final String KEY_nlurl="neoload_url";

    private String teststatus;
    private static final String KEY_nlstatus="neoload_testStatus";

    public KeptnEventFinished(JsonObject object)
    {
        if(object.getValue(KEY_githuborg) instanceof String)
            githuborg=object.getString(KEY_githuborg);

        if(object.getValue(KEY_project) instanceof  String)
            project=object.getString(KEY_project);

        if(object.getValue(KEY_deploymentstrategy) instanceof String)
            deploymentstrategy=object.getString(KEY_deploymentstrategy);

        if(object.getValue(KEY_image) instanceof String)
            image=object.getString(KEY_image);


        if(object.getValue(KEY_service) instanceof String)
            service=object.getString(KEY_service);


        if(object.getValue(KEY_stage) instanceof String)
            stage=object.getString(KEY_stage);

        if(object.getValue(KEY_tag) instanceof String)
            tag=object.getString(KEY_tag);

        if(object.getValue(KEY_teststrategy) instanceof String)
            teststrategy=object.getString(KEY_teststrategy);

        getOtherData(object);

    }

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

    private void getOtherData(JsonObject object)
    {
        otherdata=new HashMap<String,Object>();

        object.forEach(
                pair->{
                    if(!knowkeys.contains(pair.getKey()))
                        otherdata.put(pair.getKey(),pair.getValue());
                }
        );
    }

    public String getTeststatus() {
        return teststatus;
    }

    public void setTeststatus(String teststatus) {
        this.teststatus = teststatus;
    }

    public String getTestid() {
        return testid;
    }

    public void setTestid(String testid) {
        this.testid = testid;
    }

    public String getNeoloadURL() {
        return neoloadURL;
    }

    public void setNeoloadURL(String neoloadURL) {
        this.neoloadURL = neoloadURL;
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

    public JsonObject toJsonObject()
    {
        JsonObject jsonObject=new JsonObject();
        jsonObject.put(KEY_teststrategy,teststrategy);
        jsonObject.put(KEY_tag,tag);
        jsonObject.put(KEY_stage,stage);
        jsonObject.put(KEY_service,service);
        jsonObject.put(KEY_image,image);
        jsonObject.put(KEY_deploymentstrategy,deploymentstrategy);
        jsonObject.put(KEY_project,project);
        jsonObject.put(KEY_githuborg,githuborg);

        if(testid!=null)
            jsonObject.put(KEY_testid,testid);

        if(neoloadURL!=null)
            jsonObject.put(KEY_nlurl,neoloadURL);

        if(teststatus!=null)
            jsonObject.put(KEY_nlstatus,teststatus);

        otherdata.entrySet().forEach(pair->{
            jsonObject.put(pair.getKey(),pair.getValue());
        });

        return jsonObject;
    }

    public String toString()
    {

        return toJsonObject().toString();

    }
}
