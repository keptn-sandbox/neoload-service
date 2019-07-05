package com.keptn.neotys.testexecutor.EventSender;


import com.keptn.neotys.testexecutor.KeptnEvents.KeptnEventFinished;
import com.keptn.neotys.testexecutor.KeptnEvents.TestFinished;
import com.keptn.neotys.testexecutor.cloudevent.KeptnExtensions;
import com.keptn.neotys.testexecutor.log.KeptnLogger;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventBuilder;
import io.cloudevents.http.reactivex.vertx.VertxCloudEvents;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpClientRequest;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static com.keptn.neotys.testexecutor.KeptnEvents.EventType.KEPTN_TEST_FINISHED;
import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.*;


public class NeoLoadEndEvent  {

    String eventid;
    KeptnLogger logger;
    Vertx vertx;
    private final static String CONTENTYPE="aplicaiton/json";

    public NeoLoadEndEvent(KeptnLogger log, String enventid, Vertx rxvertx) {
        this.eventid=enventid;
        logger=log;
        vertx=rxvertx;
    }

    public void endevent(KeptnEventFinished data, KeptnExtensions extensions, CloudEvent<Object> receivedEvent)
    {
        try {
            logger.debug("endevent : Start sending event");
            final HttpClientRequest request = vertx.createHttpClient().post(KEPTN_PORT_EVENT, KEPTN_EVENT_HOST, "/"+KEPTN_EVENT_URL);

            logger.debug("endevent : Defining cloud envet with data:" + data.toJsonObject().toString());

            logger.debug("endevnet specversion : "+receivedEvent.getSpecVersion()+" : source : "+URI.create(NEOLOAD_SOURCE).toString()+ " id :"+this.eventid);
            String id;
            if(receivedEvent.getId()==null)
                id=extensions.getShkeptncontext();
            else
                id=receivedEvent.getId();


            String contentype;
            if(receivedEvent.getContentType().isPresent())
                contentype=receivedEvent.getContentType().get();
            else
                contentype=CONTENTYPE;

            CloudEvent<JsonObject> cloudEvent = new CloudEventBuilder<JsonObject>()
                    .type(KEPTN_TEST_FINISHED)
                    .id(id)
                    .source(URI.create(NEOLOAD_SOURCE))
                    .time(ZonedDateTime.now(ZoneOffset.UTC))
                    .data(data.toJsonObject())
                    .extension(extensions)
                    .specVersion(receivedEvent.getSpecVersion())
                    .contentType(contentype)
                    .build();

            request.handler(resp -> {
                logger.info("endevent : received response code "+String.valueOf(resp.statusCode())+ " message "+ resp.statusMessage());
            });


            VertxCloudEvents.create().writeToHttpClientRequest(cloudEvent, false,request);
            logger.debug("endevent : request write");

            request.end();
            logger.info("Request sent " + cloudEvent.getType() + " data " + cloudEvent.getData().get().toString());
        }
        catch(Exception e)
        {
            logger.error("end event generate exception",e);
        }
    }



}