package com.keptn.neotys.testexecutor.EventSender;


import com.keptn.neotys.testexecutor.KeptnEvents.KeptnEventFinished;
import com.keptn.neotys.testexecutor.KeptnEvents.TestFinished;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventBuilder;
import io.cloudevents.http.reactivex.vertx.VertxCloudEvents;
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
    Logger logger;

    public NeoLoadEndEvent(String jsondata,String enventid) {
        this.eventid=enventid;
        LogManager.getLogManager().reset();
        logger = LogManager.getLogManager().getLogger("");
    }

    public void endevent(KeptnEventFinished data)
    {
        final HttpClientRequest request = vertx.createHttpClient().post(KEPTN_PORT, KEPTN_EVENT_HOST,KEPTN_EVENT_URL);

// add a client response handler
        request.handler(resp -> {
            // react on the server response

        });

// write the CloudEvent to the given HTTP Post request object
        CloudEvent<TestFinished> cloudEvent = new CloudEventBuilder<TestFinished>()
            .type(KEPTN_TEST_FINISHED)
            .id(this.eventid)
            .source(URI.create(NEOLOAD_SOURCE))
            .time(ZonedDateTime.now(ZoneOffset.UTC))
            .data(new TestFinished(data.getGithuborg(),data.getProject(),data.getTeststrategy(),data.getDeploymentstrategy(),data.getStage(),data.getService(),data.getImage(),data.getTag()))
            .build();

        VertxCloudEvents.create().writeToHttpClientRequest(cloudEvent, request);
        request.end();
        logger.info("Request sent " + cloudEvent.getType() +" data "+ cloudEvent.getData().get().toString());
    }



}