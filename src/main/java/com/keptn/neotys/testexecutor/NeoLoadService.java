package com.keptn.neotys.testexecutor;

import com.keptn.neotys.testexecutor.cloudevent.CloudEventNeoload;
import io.vertx.core.Vertx;


public class NeoLoadService {

    private static Vertx vertx;
    private static CloudEventNeoload cloudEventNeoload;
    private static io.vertx.reactivex.core.Vertx rxVertx;
    public static void main(String[] args) {

        vertx = Vertx.vertx();
        rxVertx = io.vertx.reactivex.core.Vertx.newInstance(vertx);
        cloudEventNeoload = new CloudEventNeoload();
        cloudEventNeoload.init(vertx, vertx.getOrCreateContext());
        cloudEventNeoload.start();
        Vertx.vertx().deployVerticle(new CloudEventNeoload());
    }
}
