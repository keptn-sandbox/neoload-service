package com.keptn.neotys.testexecutor.ressource;

import com.google.gson.Gson;
import com.keptn.neotys.testexecutor.exception.NeoLoadSerialException;
import com.keptn.neotys.testexecutor.log.KeptnLogger;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.client.HttpRequest;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;

import java.util.concurrent.atomic.AtomicReference;

import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.*;

public class ConfigurationApi {
    private KeptnLogger logger;
    private Vertx vertx;

    private String projectname;
    private String stagename;
    private WebClient client;

    public ConfigurationApi(KeptnLogger logger, Vertx vertx, String projectname, String stagename) {
        this.logger = logger;
        this.vertx = vertx;
        this.projectname = projectname;
        this.stagename = stagename;
        client=WebClient.create(vertx);
    }

    public KeptnRessource getRessource(String ressource) throws NeoLoadSerialException
    {
        String uri="/"+CONFIGURATION_VERSION+"/"+CONFIGURATION_PROJECT+"/"+projectname+"/"+CONFIGURATION_STAGE+"/"+stagename+"/"+CONFIGURATION_RESSOURCE+"/"+ressource;
        KeptnRessource result = null;
        HttpRequest<io.vertx.reactivex.core.buffer.Buffer> request = client.get(CONFIGURAITON_PORT,CONFIGURATIONAPI_HOST,uri);
        request.putHeader(HEADER_ACCEPT,HEADER_APPLICATIONJSON);
        logger.debug("Sending GET Request : "+uri);
        AtomicReference<String> jsonBody=null;
        AtomicReference<String> error=null;
        request.send(ar -> {
            if (ar.succeeded()) {
                // Obtain response
                HttpResponse<Buffer> response = ar.result();
                logger.debug("Received response : "+ response.toString());
                logger.debug("REceived following body:"+response.bodyAsString());
                jsonBody.set(response.bodyAsString());
            } else {
                logger.error("ERROR while trying to get ressource file");
                error.set("Error while trying to get the test ressource");

            }
    });
        if(error.get()!=null)
            throw new NeoLoadSerialException(error.get());
        if(jsonBody.get()!=null) {
            Gson gson = new Gson();
            result = gson.fromJson(jsonBody.get(), KeptnRessource.class);
            return result;
        }
        else
            return null;
    }
}
