package com.keptn.neotys.cloudevent;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventBuilder;
import io.cloudevents.Extension;
import io.cloudevents.http.reactivex.vertx.VertxCloudEvents;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpClientRequest;
import org.junit.BeforeClass;
import org.junit.Test;
import rx.Completable;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RxVertxCloudEventsTest {
    private static Vertx vertx;
    private static CloudEventVerticle verticle;
    private static io.vertx.reactivex.core.Vertx rxVertx;

    @BeforeClass
    public static void before() {
        vertx = Vertx.vertx();
        rxVertx = io.vertx.reactivex.core.Vertx.newInstance(vertx);
        verticle = new CloudEventVerticle();
        verticle.init(vertx, vertx.getOrCreateContext());
        verticle.start();
    }

    @Test
    public void test() {

        final KeptExtension keptExtension = new KeptExtension("db51be80-4fee-41af-bb53-1b093d2b694c", "MyFuckingOtherProperty!!!");

        final CloudEvent<String> event = new CloudEventBuilder<String>()
                .id("id")
                .source(URI.create("http://example.com/mypage.html"))
                .data(new JsonObject().put("a", 1).put("b", "a value").toString())
                .time(ZonedDateTime.now())
                .contentType("application/json")
                .schemaURL(URI.create("http://example.com/mypage.html"))
                .specVersion("0.2")
                .type("sh.keptn.events.configuration-changed")
                .extension(keptExtension)
                .build();
        final HttpClientRequest request = rxVertx.createHttpClient().post(4444, "localhost", "/");

// add a client response handler
        request.handler(resp -> {
            // react on the server response
        });

// write the CloudEvent to the given HTTP Post request object
        VertxCloudEvents.create().writeToHttpClientRequest(event, request);
        request.end();


        Completable.timer(10, TimeUnit.SECONDS).await();
        System.out.println("ok");
    }

    public static class CloudEventVerticle extends AbstractVerticle {

        public void start() {
            vertx.createHttpServer()
                    .requestHandler(req ->
                    {
                        System.out.println(req.headers().entries().stream().map(Object::toString).collect(Collectors.joining(",")));
                        VertxCloudEvents.create().rxReadFromRequest(req, new Class[]{KeptExtension.class})
                                .subscribe((receivedEvent, throwable) -> {
                                    if (receivedEvent != null) {
                                        // I got a CloudEvent object:
                                        System.out.println("The event type: " + receivedEvent.getType());
                                        System.out.println(receivedEvent.getData().get().getClass());

                                        System.out.println(receivedEvent.getData().toString());
                                        receivedEvent.getExtensions()
                                                .ifPresent(extensions -> extensions.forEach(System.out::println));
                                    }
                                });
                    })
                    .rxListen(4444)
                    .subscribe(server -> {
                        System.out.println("Server running!");
                    });
        }
    }

    public static class KeptExtension implements Extension {
        private String shkeptncontext;
        private String otherproperty;

        //Mandatory to let cloud-events lib instatiate class
        public KeptExtension() {
        }

        public KeptExtension(final String shkeptncontext, final String otherproperty) {
            this.shkeptncontext = shkeptncontext;
            this.otherproperty = otherproperty;
        }

        public String getOtherproperty() {
            return otherproperty;
        }

        public void setOtherproperty(final String otherproperty) {
            this.otherproperty = otherproperty;
        }

        public String getShkeptncontext() {
            return shkeptncontext;
        }

        public void setShkeptncontext(final String shkeptncontext) {
            this.shkeptncontext = shkeptncontext;
        }

        @Override
        public int hashCode() {
            return Objects.hash(shkeptncontext);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final KeptExtension other = (KeptExtension) obj;
            return Objects.equals(this.shkeptncontext, other.shkeptncontext);
        }

        @Override
        public String toString() {
            return "KeptExtension{" +
                    "shkeptncontext='" + shkeptncontext + '\'' +
                    ", otherproperty='" + otherproperty + '\'' +
                    '}';
        }
    }

}