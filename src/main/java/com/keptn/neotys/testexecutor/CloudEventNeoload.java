package com.keptn.neotys.testexecutor;

import com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration;
import com.keptn.neotys.testexecutor.log.NeoLoadServiceLoggingHandler;
import io.cloudevents.http.reactivex.vertx.VertxCloudEvents;
import io.vertx.core.http.HttpHeaders;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static com.keptn.neotys.testexecutor.KeptnEvents.EventType.KEPTN_TEST_STARTING;
import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.*;
import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.KEPTN_PORT;
import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.PORT;
import static java.lang.System.getenv;

public class CloudEventNeoload extends AbstractVerticle {
	static Logger rootLogger;
	public static void main(String[] args) {
		LogManager.getLogManager().reset();
		rootLogger = LogManager.getLogManager().getLogger("");
	//	rootLogger.setLevel(new Level(getenv(LOGING_LEVEL_KEY),0));
		rootLogger.addHandler(new NeoLoadServiceLoggingHandler());
		Vertx.vertx().deployVerticle(new CloudEventNeoload());
	}


	public void start() {

		vertx.createHttpServer()
				.requestHandler(req -> VertxCloudEvents.create().rxReadFromRequest(req)
						.subscribe((receivedEvent, throwable) -> {
							if (receivedEvent != null) {
								// I got a CloudEvent object:
								System.out.println("The event type: " + receivedEvent.getType());
								if(receivedEvent.getType()==KEPTN_TEST_STARTING)
								{
									//----launch the test--------
								    //--retrieve data from Git

									//--check test zone id----

									//---run test ------

								}

							}
						}))
				.rxListen(KEPTN_PORT)
				.subscribe(server -> {
					rootLogger.info("Server running!");
				});
	}
}