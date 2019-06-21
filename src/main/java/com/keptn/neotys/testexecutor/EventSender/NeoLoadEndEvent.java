package com.keptn.neotys.testexecutor.EventSender;


import com.keptn.neotys.testexecutor.KeptnEvents.KeptnEventFinished;
import com.keptn.neotys.testexecutor.KeptnEvents.TestFinished;
import com.keptn.neotys.testexecutor.cloudevent.KeptnExtensions;
import com.keptn.neotys.testexecutor.log.KeptnLogger;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventBuilder;
import io.cloudevents.http.reactivex.vertx.VertxCloudEvents;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpClientRequest;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static com.keptn.neotys.testexecutor.KeptnEvents.EventType.KEPTN_TEST_FINISHED;
import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.*;


public class NeoLoadEndEvent extends AbstractVerticle {

    String eventid;
    KeptnLogger logger;

    public NeoLoadEndEvent(KeptnLogger log,String enventid) {
        this.eventid=enventid;
        logger=log;
    }

    public void endevent(KeptnEventFinished data,KeptnExtensions extensions)
    {
        final HttpClientRequest request = vertx.createHttpClient().post(KEPTN_PORT, KEPTN_EVENT_HOST,KEPTN_EVENT_URL);

// add a client response handler
        request.handler(resp -> {
            // react on the server response

        });

// write the CloudEvent to the given HTTP Post request object
        CloudEvent<JsonObject> cloudEvent = new CloudEventBuilder<JsonObject>()
            .type(KEPTN_TEST_FINISHED)
            .id(this.eventid)
            .source(URI.create(NEOLOAD_SOURCE))
            .time(ZonedDateTime.now(ZoneOffset.UTC))
            .data(data.toJsonObject())
            .extension(extensions)
            .build();

        VertxCloudEvents.create().writeToHttpClientRequest(cloudEvent, request);
        request.end();
        logger.info("Request sent " + cloudEvent.getType() +" data "+ cloudEvent.getData().get().toString());
    }



}