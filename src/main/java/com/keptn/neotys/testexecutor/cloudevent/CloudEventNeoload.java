package com.keptn.neotys.testexecutor.cloudevent;

import com.keptn.neotys.testexecutor.KeptnEvents.KeptnEventFinished;
import com.keptn.neotys.testexecutor.log.KeptnLogger;
import com.keptn.neotys.testexecutor.messageHandler.NeoLoadHandler;
import io.cloudevents.http.reactivex.vertx.VertxCloudEvents;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.healthchecks.HealthCheckHandler;
import io.vertx.reactivex.ext.web.Router;


import static com.keptn.neotys.testexecutor.KeptnEvents.EventType.KEPTN_TEST_STARTING;
import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.HEALTH_PATH;
import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.KEPTN_PORT;
import static java.lang.System.getenv;

public class CloudEventNeoload extends AbstractVerticle {

	KeptnLogger loger;

	private  Vertx rxvertx;

	public void start() {
		rxvertx=io.vertx.reactivex.core.Vertx.newInstance(this.getVertx());
		loger=new KeptnLogger(this.getClass().getName());
		if(rxvertx ==null)
			System.out.println("Issues during init");

		rxvertx.createHttpServer()
				.requestHandler(req ->
				{
					if(req.path().equalsIgnoreCase(HEALTH_PATH)) {
						req.response().end("Status:OK");
						return;
					}
					VertxCloudEvents.create().rxReadFromRequest(req,new Class[]{KeptnExtensions.class})
							.subscribe((receivedEvent, throwable) -> {
								if (receivedEvent != null) {
									// I got a CloudEvent object:
									System.out.println("The event type: " + receivedEvent.getType());
									if(receivedEvent.getType()==KEPTN_TEST_STARTING)
									{

										//----launch the test--------
										//--retrieve data from Git
										//KeptnExtensions extensions=receivedEvent.getExtensions().ifPresent();
										if(receivedEvent.getData().isPresent()) {
											//new JsonObject(receivedEvent.getData().get().toString());
											Object obj=receivedEvent.getData().get();
											JsonObject data = new JsonObject(obj.toString());
											if ( obj instanceof JsonObject) {
												KeptnEventFinished eventFinished = new KeptnEventFinished(data);
												if(receivedEvent.getExtensions().isPresent() && receivedEvent.getExtensions().get().size()>0) {
													KeptnExtensions keptnExtensions = (KeptnExtensions) receivedEvent.getExtensions().get().get(0);
													String keptncontext = keptnExtensions.getShkeptncontext();
													loger.setKepncontext(keptncontext);
													loger.debug("Received data " + eventFinished.toString());
													NeoLoadHandler neoLoadHandler=new NeoLoadHandler(eventFinished,keptnExtensions,receivedEvent.getId());
													neoLoadHandler.runNeoLoadTest();
												}
											}
										}


									}

								}
							});
				})
				.rxListen(KEPTN_PORT)
				.subscribe(server -> {
					System.out.println("Server running!");});

	}


}