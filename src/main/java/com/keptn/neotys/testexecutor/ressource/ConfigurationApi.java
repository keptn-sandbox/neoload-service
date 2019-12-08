package com.keptn.neotys.testexecutor.ressource;

import com.google.gson.Gson;
import com.keptn.neotys.testexecutor.exception.NeoLoadSerialException;
import com.keptn.neotys.testexecutor.log.KeptnLogger;

import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.client.HttpRequest;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import io.vertx.reactivex.ext.web.client.predicate.ResponsePredicate;

import java.util.concurrent.atomic.AtomicReference;

import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.*;

public class ConfigurationApi {
    private KeptnLogger logger;
    private Vertx vertx;

    private String projectname;
    private String stagename;
    private WebClient client;
    private String servicename;
    public ConfigurationApi(KeptnLogger logger, Vertx vertx, String projectname, String stagename,String service) {
        this.logger = logger;
        this.vertx = vertx;
        this.projectname = projectname;
        this.stagename = stagename;
        this.servicename=service;
        client=WebClient.create(vertx);
    }

    public Future<KeptnRessource> getRessource(String ressource) throws NeoLoadSerialException
    {
        Future<KeptnRessource> keptnRessourceFuture=Future.future();
        String uri="/"+CONFIGURATION_VERSION+"/"+CONFIGURATION_PROJECT+"/"+projectname+"/"+CONFIGURATION_STAGE+"/"+stagename+"/"+CONFIGURATION_SERVICE+"/"+servicename+"/"+CONFIGURATION_RESSOURCE+"/"+ressource;
        KeptnRessource result = null;
        HttpRequest<Buffer> request = client.get(CONFIGURAITON_PORT,CONFIGURATIONAPI_HOST,uri);
        request.putHeader(HEADER_ACCEPT,HEADER_APPLICATIONJSON);
        request.expect(ResponsePredicate.SC_SUCCESS);
        request.expect(ResponsePredicate.JSON);
        request.expect(ResponsePredicate.status(200));
        logger.debug("Sending GET Request : "+uri);
        AtomicReference<String> jsonBody=new AtomicReference<>();
        AtomicReference<String> error=new AtomicReference<>();
        request.send(httpResponseAsyncResult -> {
            if (httpResponseAsyncResult.succeeded()) {
                // Obtain response
                HttpResponse<Buffer> response = httpResponseAsyncResult.result();
                logger.debug("Received response : "+ response.toString());
                logger.debug("REceived following body:"+response.bodyAsString());
                jsonBody.set(response.bodyAsString());
                Gson gson = new Gson();
                keptnRessourceFuture.complete(gson.fromJson(jsonBody.get(), KeptnRessource.class));
            }
            if(httpResponseAsyncResult.failed())
            {
                logger.error("Request failed");
                error.set("The API ressource failed");
                keptnRessourceFuture.fail("The API ressource failed");
            }
        });

        return keptnRessourceFuture;
    }
}
