package com.keptn.neotys.testexecutor;

import com.keptn.neotys.testexecutor.cloudevent.CloudEventNeoload;
import io.vertx.core.Vertx;


public class NeoLoadService {



    public static void main(String[] args) {

        Vertx.vertx().deployVerticle(new CloudEventNeoload());


    }
}
