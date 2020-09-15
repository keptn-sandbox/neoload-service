package com.keptn.neotys.testexecutor.EventSender;


import com.keptn.neotys.testexecutor.KeptnEvents.CloudTestEndEvent;
import com.keptn.neotys.testexecutor.KeptnEvents.KeptnEventFinished;
import com.keptn.neotys.testexecutor.cloudevent.KeptnExtensions;
import com.keptn.neotys.testexecutor.log.KeptnLogger;
import io.cloudevents.CloudEvent;
import io.cloudevents.http.reactivex.vertx.VertxCloudEvents;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.client.HttpRequest;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.core.Vertx;

import io.vertx.reactivex.core.http.HttpClientRequest;

import java.net.URI;

import static com.keptn.neotys.testexecutor.KeptnEvents.EventType.KEPTN_TEST_FINISHED;
import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.*;


public class NeoLoadEndEvent  {

    String eventid;
    KeptnLogger logger;
    Vertx vertx;

    private final static String CONTENTYPE="application/json";
    private final static String CONTENTYPE_CLOUD=" application/cloudevents+json";
    public NeoLoadEndEvent(KeptnLogger log, String enventid, Vertx rxvertx) {
        this.eventid=enventid;
        logger=log;
        vertx=rxvertx;
    }

    //{
    //  "type": "sh.keptn.events.deployment-finished",
    //  "specversion": "0.2",
    //  "source": "https://github.com/keptn/keptn/helm-service",
    //  "id": "f2b878d3-03c0-4e8f-bc3f-454bc1b3d79d",
    //  "time": "2019-06-07T07:02:15.64489Z",
    //  "contenttype": "application/json",
    //  "shkeptncontext": "08735340-6f9e-4b32-97ff-3b6c292bc509",
    //  "data": {
    //    "project": "sockshop",
    //    "stage": "staging",
    //    "service": "carts",
    //    "testStrategy": "performance",
    //    "deploymentStrategy": "direct",
    //    "tag": "0.9.1",
    //    "image": "docker.io/keptnexamples/carts"
    //  }
    //}

    public void endevent(KeptnEventFinished data, KeptnExtensions extensions, CloudEvent<Object> receivedEvent, String keptn_namespace)
    {
        try {
            logger.debug("endevent : Start sending event");
            final HttpClientRequest request = vertx.createHttpClient().post(KEPTN_PORT_EVENT, KEPTN_EVENT_HOST+keptn_namespace+KEPTN_END_URL, "/"+KEPTN_EVENT_URL);

            logger.debug("endevent : Defining cloud envet with data:" + data.toJsonObject().toString());

            logger.debug("endevnet specversion : "+receivedEvent.getSpecVersion()+" : source : "+URI.create(NEOLOAD_SOURCE).toString()+ " id :"+this.eventid);
            String id;
            if(receivedEvent.getId()==null)
                id=extensions.getShkeptncontext();
            else
                id=receivedEvent.getId();

            WebClient client=WebClient.create(vertx);

            HttpRequest<Buffer> httpRequest=client.post(KEPTN_PORT_EVENT, KEPTN_EVENT_HOST+keptn_namespace+KEPTN_END_URL, "/"+KEPTN_EVENT_URL);

            httpRequest.putHeader(CONTENT_TYPE,CONTENTYPE_CLOUD);
            String contentype;
           /* if(receivedEvent.getContentType().isPresent())
                contentype=receivedEvent.getContentType().get();
            else
                contentype=CONTENTYPE;*/
          // contentype=CONTENTYPE_CLOUD;
            CloudTestEndEvent cloudTestEndEvent=new CloudTestEndEvent(KEPTN_TEST_FINISHED,CONTENTYPE,extensions.getShkeptncontext(),receivedEvent.getSpecVersion(),NEOLOAD_SOURCE,id,data.toJsonObject());
            httpRequest.sendJson(cloudTestEndEvent.toJson(),httpResponseAsyncResult -> {
                if(httpResponseAsyncResult.succeeded())
                {
                    logger.info("endevent : received response code "+String.valueOf(httpResponseAsyncResult.result().statusCode())+ " message "+ httpResponseAsyncResult.result().statusMessage());
                }
                else
                {
                    logger.error("ERROR endevent : received response code "+String.valueOf(httpResponseAsyncResult.result().statusCode())+ " message "+ httpResponseAsyncResult.result().statusMessage());
                }
            });
            logger.info("Request sent " + cloudTestEndEvent.toJson().toString() );

            /*CloudEvent<JsonObject> cloudEvent = new CloudEventBuilder<JsonObject>()
                    .type(KEPTN_TEST_FINISHED)
                    .id(id)
                    .source(URI.create(NEOLOAD_SOURCE))
                    .time(ZonedDateTime.now(ZoneOffset.UTC))
                    .data(data.toJsonObject())
                    .extension(extensions)
                    .specVersion(receivedEvent.getSpecVersion())
                    .contentType(CONTENTYPE)
                    .build();

            request.handler(resp -> {
                logger.info("endevent : received response code "+String.valueOf(resp.statusCode())+ " message "+ resp.statusMessage());

            });


            //VertxCloudEvents.create().writeToHttpClientRequest(cloudEvent, false,request);
            logger.debug("endevent : request write");
            */

        }
        catch(Exception e)
        {
            logger.error("end event generate exception",e);
            if(extensions.getShkeptncontext()==null)
                logger.debug("keptn context null");

            if(receivedEvent.getSpecVersion()==null)
                logger.debug("Specversion null");

            if(data.toJsonObject() ==null)
                logger.debug("data null");

        }
    }



}