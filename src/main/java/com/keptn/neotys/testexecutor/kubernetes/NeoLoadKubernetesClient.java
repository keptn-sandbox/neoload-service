package com.keptn.neotys.testexecutor.kubernetes;


import com.keptn.neotys.testexecutor.log.KeptnLogger;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.*;
import static com.keptn.neotys.testexecutor.kubernetes.DockerConstants.*;

public class NeoLoadKubernetesClient {



    private String masterurl;
    private Config config;
    private KubernetesClient client;
    private Optional<String> neoloadZoneid;
    private String neoloadAPitoken;
    private Optional<String> neoloadweb_url;
    private Optional<String> neoloadweb_apiurl;
    private Optional<String> neoloadweb_uploadurl;
    private String context;
    private KeptnLogger logger;
    private static final String DEFAULT_MASTER_URL="https://kubernetes.default.svc";

    public NeoLoadKubernetesClient(String masterurl,String kepncontext) {
        this.masterurl = masterurl;
        config = new ConfigBuilder().withMasterUrl(masterurl).build();
        context=kepncontext;
        logger =new KeptnLogger(this.getClass().getName());
        logger.setKepncontext(kepncontext);
        getSecrets();
    }

    public NeoLoadKubernetesClient(String kepncontext) {
        this.masterurl = DEFAULT_MASTER_URL;
        config = new ConfigBuilder().withMasterUrl(masterurl).build();
        context=kepncontext;
        logger =new KeptnLogger(this.getClass().getName());
        logger.setKepncontext(kepncontext);

        getSecrets();
    }

    public Optional<String> getNeoloadZoneid() {
        return neoloadZoneid;
    }

    public void setNeoloadZoneid(Optional<String> neoloadZoneid) {
        this.neoloadZoneid = neoloadZoneid;
    }

    public String getNeoloadAPitoken() {
        return neoloadAPitoken;
    }

    public void setNeoloadAPitoken(String neoloadAPitoken) {
        this.neoloadAPitoken = neoloadAPitoken;
    }

    public Optional<String> getNeoloadweb_url() {
        return neoloadweb_url;
    }

    public void setNeoloadweb_url(Optional<String> neoloadweb_url) {
        this.neoloadweb_url = neoloadweb_url;
    }

    public Optional<String> getNeoloadweb_apiurl() {
        return neoloadweb_apiurl;
    }

    public Optional<String> getNeoloadweb_uploadurl() {
        return neoloadweb_uploadurl;
    }

    private void getSecrets()
    {

        logger.debug("retrieve the environement variables for neoload  neoload service ");
        neoloadAPitoken=System.getenv(SECRET_API_TOKEN);
        neoloadweb_apiurl=Optional.ofNullable(System.getenv(SECRET_NL_API_HOST));
        neoloadweb_url=Optional.ofNullable(System.getenv(SECRET_NL_WEB_HOST));
        neoloadZoneid=Optional.ofNullable(System.getenv(SECRET_NL_ZONEID));
        neoloadweb_uploadurl=Optional.ofNullable(System.getenv(SECRET_NL_UPLOAD_HOST));
    }
    public void deleteController()
    {
        try
        {
            this.client = new DefaultKubernetesClient(config);

            logger.debug("deleteController - : delete service with label :"+NEOLOAD+"_"+CONTROLLER+","+NEOLOAD+context );
            client.services().inNamespace(KEPTN_EVENT_URL)
                    .withName(NEOLOAD+context)
                    .delete();
            logger.debug("deleteController - : delete pod with label :"+NEOLOAD+"_"+CONTROLLER+","+NEOLOAD+context );

            client.pods().inNamespace(KEPTN_EVENT_URL)
                    .withName(NEOLOAD+context)
                    .delete();


        }
        catch (Exception e)
        {
            logger.error("deleteController error ",e);
        }
    }
    public void deployController()
    {
        try
        {
            this.client = new DefaultKubernetesClient(config);

            logger.debug("deployController - : deploying pod with label :"+NEOLOAD+"_"+CONTROLLER+","+NEOLOAD+context );
            client.pods().inNamespace(KEPTN_EVENT_URL).createNew()
                        .withNewMetadata()
                        .withName(NEOLOAD+context)
                        .addToLabels(NEOLOAD+"_"+CONTROLLER,NEOLOAD+context)
                        .endMetadata()
                        .withNewSpec()
                        .addNewContainer()
                        .withNewImage(NEOLAOD_CTL_DOCKER)
                        .withEnv(controllerEnv())
                        .endContainer()
                        .endSpec()
                        .done();
            logger.debug("deployController - : deploying service with label :"+NEOLOAD+"_"+CONTROLLER+","+NEOLOAD+context );

            client.services().inNamespace(KEPTN_EVENT_URL).createNew()
                    .withNewMetadata()
                    .withName(NEOLOAD+context)
                    .addToLabels(NEOLOAD+"_"+CONTROLLER,NEOLOAD+context)
                    .endMetadata()
                    .withNewSpec()
                    .addNewPort().withPort(7400).withNewTargetPort().withIntVal(7400).endTargetPort().endPort()
                    .addNewPort().withPort(443).withNewTargetPort().withIntVal(443).endTargetPort().endPort()
                    .addNewPort().withPort(7200).withNewTargetPort().withIntVal(7200).endTargetPort().endPort()
                    .addNewPort().withPort(4569).withNewTargetPort().withIntVal(4569).endTargetPort().endPort()
                    .addToSelector(NEOLOAD+"_"+CONTROLLER,NEOLOAD+context)
                    .withType(CLUSTER_IP)
                    .endSpec()
                    .done();

        }
        catch (Exception e)
        {
            logger.error("deployController error ",e);
        }
    }
    public void deleteLG(String suffix)
    {
        try
        {
            this.client = new DefaultKubernetesClient(config);

            logger.debug("deleteLG - : delete Servvice with label :"+NEOLOAD+"_"+LG+","+NEOLOAD+context+suffix );

            client.services().inNamespace(KEPTN_EVENT_URL)
                    .withName(NEOLOAD+context+suffix)
                    .delete();


            logger.debug("deleteLG - : delete pods with label :"+NEOLOAD+"_"+LG+","+NEOLOAD+context +suffix);

            client.pods().inNamespace(KEPTN_EVENT_URL)
                    .withName(NEOLOAD+context+suffix)
                    .delete();


        }
        catch (Exception e)
        {
            logger.error("deleteLG error ",e);
        }
    }
    public void deployLG(String suffix)
    {
        try
        {
            this.client = new DefaultKubernetesClient(config);

            logger.debug("deployLG - : deploying pod with label :"+NEOLOAD+"_"+LG+","+NEOLOAD+context+suffix );

            client.pods().inNamespace(KEPTN_EVENT_URL).createNew()
                    .withNewMetadata()
                    .withName(NEOLOAD+context+suffix)
                    .addToLabels(NEOLOAD+"_"+LG,NEOLOAD+context+suffix)
                    .endMetadata()
                    .withNewSpec()
                    .addNewContainer()
                    .withNewImage(NEOLAOD_LG_DOCKER)
                    .withEnv(lgenv())
                    .endContainer()
                    .endSpec()
                    .done();
            logger.debug("deployLG - : deploying service with label :"+NEOLOAD+"_"+LG+","+NEOLOAD+context +suffix);

            client.services().inNamespace(KEPTN_EVENT_URL).createNew()
                    .withNewMetadata()
                    .withName(NEOLOAD+context+suffix)
                    .addToLabels(NEOLOAD+"_"+LG,NEOLOAD+context+suffix)
                    .endMetadata()
                    .withNewSpec()
                     .addNewPort().withPort(7100).withNewTargetPort().withIntVal(7100).endTargetPort().endPort()
                    .addToSelector(NEOLOAD+"_"+LG,NEOLOAD+context+suffix)
                    .withType(CLUSTER_IP)
                    .endSpec()
                    .done();

        }
        catch (Exception e)
        {
            logger.error("deployLG error ",e);
        }
    }

    public void deleteService(String servicename)
    {
        //----#TODO delete the LG Services or Controller

    }

    private List<EnvVar> controllerEnv()
    {
       List<EnvVar> list=new ArrayList<>();

       createEnvList(list);
       list.add(new EnvVar(ENV_MODE,ENV_MANAGED,null));
       list.add(new EnvVar(ENV_LEASE_SERVER,LEASE_SERVER,null));
       return list;

    }

    private List<EnvVar> lgenv()
    {
        List<EnvVar> list=new ArrayList<>();

        createEnvList(list);
        list.add(new EnvVar(ENV_MODE,ENV_MANAGED,null));
        list.add(new EnvVar(ENV_LG_HOST,generateServiceName(LG),null));
        list.add(new EnvVar(ENV_LG_PORT,LG_PORT,null));
        return list;

    }

    private String generateServiceName(String type)
    {
        return NEOLOAD+"-"+type+"-"+context+"."+KEPTN_EVENT_URL+".svc";
    }

    private void createEnvList(final List<EnvVar> list )
    {

        if(neoloadweb_url.isPresent())
            list.add(new EnvVar(ENV_NEOLOADWEB_URL,neoloadweb_url.get(),null));

        list.add(new EnvVar(ENV_NEOLOADWEB_TOKEN,neoloadAPitoken,null));

        if(neoloadZoneid.isPresent())
            list.add(new EnvVar(ENV_ZONE,neoloadZoneid.get(),null));
    }
}
