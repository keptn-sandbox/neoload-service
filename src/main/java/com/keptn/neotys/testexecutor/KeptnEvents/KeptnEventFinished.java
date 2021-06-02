package com.keptn.neotys.testexecutor.KeptnEvents;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.vertx.core.json.JsonObject;

import java.text.SimpleDateFormat;
import java.util.*;

public class KeptnEventFinished {

    //{
    //  "data": {
    //    "project": "sockshop",
    //    "stage": "dev",
    //    "service": "carts",
    //    "labels": {
    //      "label-key": "label-value"
    //    },
    //    "status": "succeeded",
    //    "result": "pass",
    //    "message": "a message",
    //    "test": {
    //      "teststrategy": "functional"
    //    },
    //    "deployment": {
    //      "deploymentURIsLocal": [
    //        "http://carts.sockshop-staging.svc.cluster.local"
    //      ],
    //      "deploymentURIsPublic": [
    //        "http://carts.sockshot.local:80"
    //      ]
    //    }
    //  },
    //  "datacontenttype": "application/json",
    //  "id": "c4d3a334-6cb9-4e8c-a372-7e0b45942f53",
    //  "shkeptncontext": "a3e5f16d-8888-4720-82c7-6995062905c1",
    //  "source": "source-service",
    //  "specversion": "1.0",
    //  "type": "sh.keptn.event.test.triggered"
    //}


    private String project;
    private static final String KEY_project="project";

    private JsonObject test;
    private static final String KEY_test ="test";

    private String teststrategy;
    private static final String KEY_teststrategy="teststrategy";


    private JsonObject deployment;
    private static final String KEY_deployment="deployment";

    private String stage;
    private static final String KEY_stage="stage";

    private String service;
    private static final String KEY_service="service";

    private String message;
    private static final String KEY_message="message";

    private String status;
    private static final String KEY_status="status";

    private String result;
    private static final String KEY_result="result";

    private List<String> knowkeys= Arrays.asList(new String[]{KEY_deployment, KEY_project, KEY_service, KEY_stage, KEY_test,KEY_message,KEY_status,KEY_result});
    private HashMap<String,Object> otherdata;

    private String testid;
    private static final String KEY_testid="neoload_testid";

    private JsonObject label;
    private static final String KEY_label="labels";

    private String neoloadURL;
    private static final String KEY_nlurl="neoload_url";

    private String teststatus;
    private static final String KEY_nlstatus="neoload_testStatus";

    private String start;
    private static final String KEY_start="start";

    private static final String KEY_gitCommit="gitCommit";
    private String end;
    private static final String KEY_end="end";


    private Keptndeployment keptndeployment;

    public KeptnEventFinished(JsonObject object) throws   Exception
    {

        if(object.getValue(KEY_project) instanceof  String)
            project=object.getString(KEY_project);

        System.out.println("Project " + project);

        if(object.containsKey(KEY_deployment))
        {
            if (object.getValue(KEY_deployment) instanceof JsonObject) {
                System.out.println("Found deplopyment");
                deployment = object.getJsonObject(KEY_deployment);
                Gson gson = new GsonBuilder().create();
                System.out.println("Converting pobject "+deployment.toString());
                keptndeployment = gson.fromJson(deployment.toString(), Keptndeployment.class);
                System.out.println("oject converted");

            }
        }


        if(object.getValue(KEY_service) instanceof String)
            service=object.getString(KEY_service);
        System.out.println("serbice " + service);


        if(object.getValue(KEY_stage) instanceof String)
            stage=object.getString(KEY_stage);

        System.out.println("stage " + stage);
        if(object.containsKey(KEY_message)) {
            if (object.getValue(KEY_message) instanceof String)
                message = object.getString(KEY_message);
        }
        if(object.getValue(KEY_status) instanceof String)
            status=object.getString(KEY_status);

        if(object.getValue(KEY_result) instanceof String)
            result=object.getString(KEY_result);

        if(object.getValue(KEY_test) instanceof JsonObject) {
            System.out.println("Getting the testing object");
            test = object.getJsonObject(KEY_test);

            if((test.containsKey(KEY_teststrategy)&& test.getValue(KEY_teststrategy) instanceof String))
                teststrategy=test.getString(KEY_teststrategy);

        }

        if(object.containsKey(KEY_label))
        {
            if(object.getValue(KEY_label) instanceof JsonObject)
            {
                System.out.println("The event has labels fields");
                label=object.getJsonObject(KEY_label);
            }
        }

        getOtherData(object);

    }

    public KeptnEventFinished(String project, JsonObject teststrategy, JsonObject deploymentstrategy, String stage, String service) {
        this.project = project;
        this.test = teststrategy;
        this.deployment = deploymentstrategy;
        this.stage = stage;
        this.service = service;

    }

    private void getOtherData(JsonObject object)
    {
        System.out.println("Getting other data");
        otherdata=new HashMap<String,Object>();

        object.forEach(
                pair->{
                    if(!knowkeys.contains(pair.getKey()))
                        otherdata.put(pair.getKey(),pair.getValue());
                }
        );
    }

    public String getStrategy()
    {
        return teststrategy;
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

    public void setTeststrategy(String teststrategy) {
        this.teststrategy = teststrategy;
    }

    public JsonObject getDeployment() {
        return deployment;
    }

    public void setDeployment(JsonObject deployment) {
        this.deployment = deployment;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Keptndeployment getKeptndeployment() {
        return keptndeployment;
    }

    public void setKeptndeployment(Keptndeployment keptndeployment) {
        this.keptndeployment = keptndeployment;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public JsonObject getTeststrategy() {
        return test;
    }

    public void setTeststrategy(JsonObject teststrategy) {
        this.test = teststrategy;
    }

    public JsonObject getDeploymentstrategy() {
        return deployment;
    }

    public void setDeploymentstrategy(JsonObject deploymentstrategy) {
        this.deployment = deploymentstrategy;
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

        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return df2.format(date);
    }
    public String getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = convertDateLongToString(end);
    }
    public JsonObject toJsonEndObject()
    {
        JsonObject jsonObject=new JsonObject();
        jsonObject.put(KEY_stage,stage);
        jsonObject.put(KEY_service,service);
        jsonObject.put(KEY_project,project);
        jsonObject.put(KEY_status,status);
        jsonObject.put(KEY_result,result);
        jsonObject.put(KEY_message,message);

        HashMap<String,String> neoloaddata=new HashMap<>();

        if(testid!=null)
            neoloaddata.put(KEY_testid,testid);
        if(neoloadURL!=null)
            neoloaddata.put(KEY_nlurl,neoloadURL);

        if(teststatus!=null)
            neoloaddata.put(KEY_nlstatus,teststatus);

        if(start!=null)
            test.put(KEY_start,start);

        if(end!=null)
            test.put(KEY_end,end);

        if(deployment.containsKey(KEY_gitCommit))
        {
            test.put(KEY_gitCommit,deployment.getValue(KEY_gitCommit));
        }

        if(neoloaddata.size()>0)
        {
            if(label == null)
                label=new JsonObject();

            neoloaddata.forEach((s, s2) -> {
                label.put(s,s2);
            });
            jsonObject.put(KEY_label, label);

        }
        if(test.containsKey(KEY_teststrategy))
            test.remove(KEY_teststrategy);

        jsonObject.put(KEY_test,test);


        return jsonObject;
    }
    public JsonObject toStartJsonObject()
    {
        JsonObject jsonObject=new JsonObject();
        jsonObject.put(KEY_stage,stage);
        jsonObject.put(KEY_service,service);
        jsonObject.put(KEY_project,project);
        jsonObject.put(KEY_status,status);
        jsonObject.put(KEY_result,result);
        jsonObject.put(KEY_message,message);

        HashMap<String,String> neoloaddata=new HashMap<>();

        if(testid!=null)
            neoloaddata.put(KEY_testid,testid);
        if(neoloadURL!=null)
            neoloaddata.put(KEY_nlurl,neoloadURL);

        if(teststatus!=null)
            neoloaddata.put(KEY_nlstatus,teststatus);

        if(start!=null)
            test.put(KEY_start,start);

        if(end!=null)
            test.put(KEY_end,end);




            if(neoloaddata.size()>0) {
                if(label ==null)
                    label=new JsonObject();

                neoloaddata.forEach((s, s2) -> {
                    label.put(s, s2);
                });
            }

            if(label!=null)
                jsonObject.put(KEY_label, label);


       // jsonObject.put(KEY_deployment,deployment);
       // jsonObject.put(KEY_test,test);


        return jsonObject;
    }

    public String toString()
    {

        return toStartJsonObject().toString();

    }
}
