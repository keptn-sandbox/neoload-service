package com.keptn.neotys.testexecutor.KeptnEvents;


import io.vertx.core.json.JsonObject;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class KeptnEventFinished {

    /*

  "type": "sh.keptn.events.tests-finished",
  "specversion": "0.2",
  "source": "https://github.com/keptn/keptn/jmeter-service",
  "id": "49ac0dec-a83b-4bc1-9dc0-1f050c7e781b",
  "time": "2019-06-07T07:02:15.64489Z",
  "contenttype": "application/json",
  "shkeptncontext":"49ac0dec-a83b-4bc1-9dc0-1f050c7e789b",
  "data": {
    "project": "sockshop",
    "stage": "staging",
    "service": "carts",
    "testStrategy": "performance",
    "deploymentStrategy": "direct",
    "startedat": "2019-09-01 12:03"
  }
}
     */

    //  "githuborg":"keptn-tiger",
    //      "project":"sockshop",
    //      "teststrategy":"functional",
    //      "deploymentstrategy":"direct",
    //      "stage":"dev",
    //      "service":"carts",
    //      "image":"10.11.245.27:5000/sockshopcr/carts",
    //      "tag":"0.6.7-16"


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


    private List<String> knowkeys= Arrays.asList(new String[]{KEY_deploymentstrategy, KEY_project, KEY_service, KEY_stage,  KEY_teststrategy});
    private HashMap<String,Object> otherdata;

    private String testid;
    private static final String KEY_testid="neoload_testid";

    private JsonObject label;
    private static final String KEY_label="label";

    private String neoloadURL;
    private static final String KEY_nlurl="neoload_url";

    private String teststatus;
    private static final String KEY_nlstatus="neoload_testStatus";

    private String start;
    private static final String KEY_start="start";

    private String end;
    private static final String KEY_end="end";

    public KeptnEventFinished(JsonObject object)
    {


        if(object.getValue(KEY_project) instanceof  String)
            project=object.getString(KEY_project);

        if(object.getValue(KEY_deploymentstrategy) instanceof String)
            deploymentstrategy=object.getString(KEY_deploymentstrategy);




        if(object.getValue(KEY_service) instanceof String)
            service=object.getString(KEY_service);


        if(object.getValue(KEY_stage) instanceof String)
            stage=object.getString(KEY_stage);



        if(object.getValue(KEY_teststrategy) instanceof String)
            teststrategy=object.getString(KEY_teststrategy);

        getOtherData(object);

    }

    public KeptnEventFinished(String project, String teststrategy, String deploymentstrategy, String stage, String service) {
        this.project = project;
        this.teststrategy = teststrategy;
        this.deploymentstrategy = deploymentstrategy;
        this.stage = stage;
        this.service = service;

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

    public String getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = convertDateLongToString(start);
    }

    private  String convertDateLongToString(long longdate)
    {
        Date date=new Date(longdate);

        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
        return df2.format(date);
    }
    public String getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = convertDateLongToString(end);
    }

    public JsonObject toJsonObject()
    {
        JsonObject jsonObject=new JsonObject();
        jsonObject.put(KEY_teststrategy,teststrategy);
        jsonObject.put(KEY_stage,stage);
        jsonObject.put(KEY_service,service);
        jsonObject.put(KEY_deploymentstrategy,deploymentstrategy);
        jsonObject.put(KEY_project,project);

        HashMap<String,String> neoloaddata=new HashMap<>();

        if(testid!=null)
            neoloaddata.put(KEY_testid,testid);
        if(neoloadURL!=null)
            neoloaddata.put(KEY_nlurl,neoloadURL);

        if(teststatus!=null)
            neoloaddata.put(KEY_nlstatus,teststatus);

        if(start!=null)
            jsonObject.put(KEY_start,start);

        if(end!=null)
            jsonObject.put(KEY_end,end);

        if(neoloaddata.size()>0)
        {
            label=new JsonObject();
            neoloaddata.forEach((s, s2) -> {
                label.put(s,s2);
            });
            jsonObject.put(KEY_label, label);

        }



        return jsonObject;
    }

    public String toString()
    {

        return toJsonObject().toString();

    }
}
