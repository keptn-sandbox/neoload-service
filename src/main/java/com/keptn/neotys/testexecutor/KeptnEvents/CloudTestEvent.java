package com.keptn.neotys.testexecutor.KeptnEvents;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class CloudTestEvent {
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
//      "start": "2019-10-20T07:57:27.152330783Z",
//      "end": "2019-10-20T08:57:27.152330783Z",
//      "gitCommit": "ca82a6dff817gc66f44342007202690a93763949"
//    }
//  },
//  "datacontenttype": "application/json",
//  "id": "c4d3a334-6cb9-4e8c-a372-7e0b45942f53",
//  "shkeptncontext": "a3e5f16d-8888-4720-82c7-6995062905c1",
//  "source": "source-service",
//  "specversion": "1.0",
//  "triggeredid": "3f9640b6-1d2a-4f11-95f5-23259f1d82d6",
//  "type": "sh.keptn.event.test.finished"
//}
    private String type;
    private static final String KEY_type="type";

    private String contenttype;
    private static final String KEY_contenttype="contenttype";

    private String shkeptncontext;
    private static final String KEY_shkeptncontext="shkeptncontext";

    private String time;
    private static final String KEY_time="time";

    private String shkeptnspecversion;
    private static final String KEY_shkeptnspecversion="shkeptnspecversion";


    private String specversion;
    private static final String KEY_specversion="specversion";

    private String source;
    private static final String KEY_source="source";

    private String id;
    private static final String KEY_id="id";

    private String triggeredid;
    private static final String KEY_triggeredid="triggeredid";

    private JsonObject data;
    private static final String KEY_data="data";

    public CloudTestEvent(String type, String contenttype, String shkeptncontext, String specversion, String source, String id, String triggeredid, String shkeptnspecversion, JsonObject data) {
        this.type = type;
        this.contenttype = contenttype;
        this.shkeptncontext = shkeptncontext;
        OffsetDateTime now = OffsetDateTime.now( ZoneOffset.UTC );
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
                .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXXXX");

        this.time =   now.format(DATE_TIME_FORMATTER);
        this.specversion = specversion;
        this.source = source;
        this.id = id;
        this.data = data;
        this.triggeredid=triggeredid;
        this.shkeptnspecversion=shkeptnspecversion;
    }

    public String getShkeptnspecversion() {
        return shkeptnspecversion;
    }

    public void setShkeptnspecversion(String shkeptnspecversion) {
        this.shkeptnspecversion = shkeptnspecversion;
    }

    public String getTriggeredid() {
        return triggeredid;
    }

    public void setTriggeredid(String triggeredid) {
        this.triggeredid = triggeredid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContenttype() {
        return contenttype;
    }

    public void setContenttype(String contenttype) {
        this.contenttype = contenttype;
    }

    public String getShkeptncontext() {
        return shkeptncontext;
    }

    public void setShkeptncontext(String shkeptncontext) {
        this.shkeptncontext = shkeptncontext;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSpecversion() {
        return specversion;
    }

    public void setSpecversion(String specversion) {
        this.specversion = specversion;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }


    public JsonObject toJson()
    {
        JsonObject result=new JsonObject();
        result.put(KEY_contenttype,contenttype);
        result.put(KEY_data,data);
        result.put(KEY_id,id);
        result.put(KEY_shkeptncontext,shkeptncontext);
        result.put(KEY_source,source);
        result.put(KEY_specversion,specversion);
        result.put(KEY_time,time);
        result.put(KEY_type,type);
        result.put(KEY_shkeptnspecversion,shkeptnspecversion);
        result.put(KEY_triggeredid,triggeredid);

        return result;
    }
    /*
"contenttype": "application/json",
"data": {
"deploymentstrategy": "direct",
"end": "2019-12-06T14:53:29Z",
"project": "sockshop",
"service": "carts-db",
"stage": "dev",
"start": "2019-12-06T14:53:29Z",
"teststrategy": "functional"
},
"id": "0e5849d7-3170-41e7-a8da-ad41b00af9c1",
"source": "jmeter-service",
"specversion": "0.2",
"time": "2019-12-06T14:53:29.770Z",
"type": "sh.keptn.events.tests-finished",
"shkeptncontext": "eb716030-aa01-409f-8490-b33d525b329f"*/
}
