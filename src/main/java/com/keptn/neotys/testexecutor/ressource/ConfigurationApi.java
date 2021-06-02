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
    private String keptnNAMESPACE;
    private String keptn_apiToken;
    public ConfigurationApi(KeptnLogger logger, Vertx vertx, String projectname, String stagename,String service) {
        this.logger = logger;
        this.vertx = vertx;
        this.projectname = projectname;
        this.stagename = stagename;
        this.servicename=service;
        client=WebClient.create(vertx);
        this.keptnNAMESPACE=System.getenv(SECRET_KEPTN_NAMESPACE);
        this.keptn_apiToken=System.getenv(SECRET_KEPTN_API_TOKEN);
        logger.debug("API TOKEN FOUND "+ keptn_apiToken );
    }

    private KeptnRessource toKeptnRessource(String content)
    {
        Gson gson = new Gson();
        return gson.fromJson(content, KeptnRessource.class);
    }

    public Future<KeptnRessource> getRessource(String ressource, String neoloadConfigFileOption2) throws NeoLoadSerialException
    {
        Future<KeptnRessource> keptnRessourceFuture=Future.future();
        //----let's search at the service level---
        logger.debug("try tro retrieve from project, stage and service");
        String uri="/"+CONFIGURATION_VERSION+"/"+CONFIGURATION_PROJECT+"/"+projectname+"/"+CONFIGURATION_STAGE+"/"+stagename+"/"+CONFIGURATION_SERVICE+"/"+servicename+"/"+CONFIGURATION_RESSOURCE+"/";
        Future<String> content;
        content=getRessourceByURL(uri, ressource,neoloadConfigFileOption2);
        content.setHandler(keptnRessourceAsyncResult -> {
            if(keptnRessourceAsyncResult.succeeded())
            {
                logger.debug("Found the workload file");
                keptnRessourceFuture.complete(toKeptnRessource(keptnRessourceAsyncResult.result()));
            }
            else
            {
                logger.debug("workload file not found , trying on project/stage");
                String url="/"+CONFIGURATION_VERSION+"/"+CONFIGURATION_PROJECT+"/"+projectname+"/"+CONFIGURATION_STAGE+"/"+stagename+"/"+CONFIGURATION_RESSOURCE+"/";
                 try {

                     Future<String> content2=getRessourceByURL(url, ressource, neoloadConfigFileOption2);
                     content2.setHandler(keptnRessourceAsyncResult1 -> {
                        if(keptnRessourceAsyncResult1.succeeded())
                        {
                            logger.debug("Found the workload file");
                            keptnRessourceFuture.complete(toKeptnRessource(keptnRessourceAsyncResult1.result()));
                        }
                        else
                        {
                            logger.debug("workload file not found , trying on project");
                            String urlproject="/"+CONFIGURATION_VERSION+"/"+CONFIGURATION_PROJECT+"/"+projectname+"/"+CONFIGURATION_RESSOURCE+"/";
                            try
                            {
                                Future<String> content3=getRessourceByURL(urlproject, ressource, neoloadConfigFileOption2);
                                content3.setHandler(keptnRessourceAsyncResult2 -> {
                                    if(keptnRessourceAsyncResult2.succeeded())
                                    {
                                        keptnRessourceFuture.complete(toKeptnRessource(keptnRessourceAsyncResult2.result()));
                                    }
                                    else
                                    {
                                        logger.debug("workload file not found");
                                        logger.info("Trying to retrieve the workload file for "+neoloadConfigFileOption2);


                                        keptnRessourceFuture.fail(keptnRessourceAsyncResult2.cause());
                                    }
                                });
                            } catch (NeoLoadSerialException e) {
                                logger.error("ERROR to get workload file",e);
                            }
                        }
                    });
                } catch (NeoLoadSerialException e) {
                    logger.error("ERROR to get the workload file",e);
                }

            }

        });

        return keptnRessourceFuture;
    }

    public Future<String> getRessourceByURL(String url, String ressource, String ressourceoption2) throws NeoLoadSerialException
    {
        Future<String> stringFuture=Future.future();
        HttpRequest<Buffer> request = client.get(CONFIGURAITON_PORT,CONFIGURATIONAPI_HOST+keptnNAMESPACE+KEPTN_END_URL,url+ressource);
        request.putHeader(HEADER_ACCEPT,HEADER_APPLICATIONJSON);
        request.putHeader(HEADER_KEPTN_TOKEN,keptn_apiToken);
        request.expect(ResponsePredicate.status(200));
        logger.debug("Sending GET Request : "+url + " for ressource "+ ressource);
        AtomicReference<String> error=new AtomicReference<>();
        request.send(httpResponseAsyncResult -> {
            if (httpResponseAsyncResult.succeeded()) {
                // Obtain response
                HttpResponse<Buffer> response = httpResponseAsyncResult.result();
                logger.debug("Received response : "+ response.toString());
                logger.debug("REceived following body:"+response.bodyAsString());

                stringFuture.complete( response.bodyAsString());
            }
            if(httpResponseAsyncResult.failed())
            {
                logger.error("Request failed");
                if(ressourceoption2!=null) {
                    logger.info("Trying the second path of the workload " + ressourceoption2);
                    HttpRequest<Buffer> requestoption2 = client.get(CONFIGURAITON_PORT, CONFIGURATIONAPI_HOST + keptnNAMESPACE + KEPTN_END_URL, url + ressourceoption2);
                    requestoption2.putHeader(HEADER_ACCEPT, HEADER_APPLICATIONJSON);
                    requestoption2.putHeader(HEADER_KEPTN_TOKEN,keptn_apiToken);
                    requestoption2.expect(ResponsePredicate.status(200));
                    requestoption2.send(httpResponseAsyncResult1 -> {
                        if (httpResponseAsyncResult1.succeeded()) {
                            HttpResponse<Buffer> response = httpResponseAsyncResult1.result();
                            logger.debug("Received response : " + response.toString());
                            logger.debug("REceived following body:" + response.bodyAsString());

                            stringFuture.complete(response.bodyAsString());
                        } else {
                            logger.error("Request failed");
                            error.set("The API ressource failed");
                            stringFuture.fail("The API ressource failed");
                        }
                    });
                }
                else
                {
                    logger.error("Request failed");
                    error.set("The API ressource failed");
                    stringFuture.fail("The API ressource failed");
                }
            }
        });

        return stringFuture;
    }
}
