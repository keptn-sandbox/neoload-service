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


import java.io.EOFException;
import java.util.HashMap;
import java.util.Optional;

import static com.keptn.neotys.testexecutor.KeptnEvents.EventType.KEPTN_TEST_STARTING;
import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.*;
import static java.lang.System.getenv;

public class CloudEventNeoload extends AbstractVerticle {

	KeptnLogger loger;

	private  Vertx rxvertx;

	public void start() {
		rxvertx= Vertx.newInstance(this.getVertx());
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
					System.out.print(req.toString());
					VertxCloudEvents.create().rxReadFromRequest(req,new Class[]{KeptnExtensions.class})
							.subscribe((receivedEvent, throwable) -> {
								if(throwable!=null)
								{
									throwable.printStackTrace();
									req.response().setStatusCode(400).end(throwable.getMessage());
									return;
								}
								if (receivedEvent != null) {
									// I got a CloudEvent object:
									System.out.println("The event type: " + receivedEvent.getType());
									if(receivedEvent.getType().equalsIgnoreCase(KEPTN_TEST_STARTING))
									{

										if(receivedEvent.getData().isPresent())
										{
											Object obj=receivedEvent.getData().get();
											try {
												JsonObject data = new JsonObject((HashMap<String,Object>)obj);
												if (data instanceof JsonObject)
												{
													KeptnEventFinished eventFinished = new KeptnEventFinished(data);
													KeptnExtensions keptnExtensions = null;
													if (receivedEvent.getExtensions().isPresent() && receivedEvent.getExtensions().get().size() > 0) {

														keptnExtensions = (KeptnExtensions) receivedEvent.getExtensions().get().get(0);
													}
													else
													{
														Optional<String> kepncontext = Optional.ofNullable(req.getHeader(HEADER_KEPTNCONTEXT));
														Optional<String> datacontent = Optional.ofNullable(req.getHeader(HEADER_datacontentype));
														if(kepncontext.isPresent()&& datacontent.isPresent())
															keptnExtensions=new KeptnExtensions(kepncontext.get(),datacontent.get());
													}

													if(keptnExtensions!=null)
													{
														String keptncontext = keptnExtensions.getShkeptncontext();
														loger.setKepncontext(keptncontext);
														loger.debug("Received data " + eventFinished.toString());
														KeptnExtensions finalKeptnExtensions = keptnExtensions;
														req.response().setStatusCode(200).putHeader("content-type", "text/plain").end("event received");

														vertx.<String>executeBlocking(
																future -> {
																	String result;
																	try {
																		NeoLoadHandler neoLoadHandler = new NeoLoadHandler(rxvertx,eventFinished, finalKeptnExtensions, receivedEvent.getId());


																		neoLoadHandler.runNeoLoadTest(rxvertx,receivedEvent);
																		result="test has finished";
																		future.complete(result);
																	}
																	catch (Exception e)
																	{
																		result="Exception :"+e.getMessage();
																		future.fail(result);
																	}
																},res->
																{
																	if (res.succeeded()) {

																		//req.response().setStatusCode(200).putHeader("content-type", "text/plain").end(res.result());

																	} else {
																		res.cause().printStackTrace();
																	}
																}

														);


													}
													else
													{
														req.response().setStatusCode(401).end("Unable to find Extensions in CLoud evnet");
														return;
													}
												}
											}
											catch (Exception e)
											{
												req.response().setStatusCode(410).end("Exception :"+e.getMessage());
											}
										}


									}
									else{
										req.response().setStatusCode(203).end("Not Supported event type");
									}

								}
								else
								{
									req.response().setStatusCode(400).end("UNsupported cloud event format");
								}

							});
				})
				.rxListen(KEPTN_PORT)
				.subscribe(server -> {
					System.out.println("Server running!");});

	}


}