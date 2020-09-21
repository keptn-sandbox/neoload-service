package com.keptn.neotys.testexecutor.kubernetes;


import com.github.rholder.retry.*;
import com.google.common.base.Predicates;
import com.keptn.neotys.testexecutor.exception.NeoLoadJgitExeption;
import com.keptn.neotys.testexecutor.log.KeptnLogger;
import io.fabric8.kubernetes.api.model.ContainerPort;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
    private Optional<String> dynatrace_api_token;
    private Optional<String> dynatrace_tenant;
    private String keptn_NAMESPACE;
    private String context;
    private KeptnLogger logger;
    private static final String DEFAULT_MASTER_URL="https://kubernetes.default.svc";
    private static final String DEFAULT_API_SAAS="neoload-api.saas.neotys.com";
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


    public Optional<String> getDynatrace_api_token() {
        return dynatrace_api_token;
    }

    public void setDynatrace_api_token(Optional<String> dynatrace_api_token) {
        this.dynatrace_api_token = dynatrace_api_token;
    }

    public Optional<String> getDynatrace_tenant() {
        return dynatrace_tenant;
    }

    public void setDynatrace_tenant(Optional<String> dynatrace_tenant) {
        this.dynatrace_tenant = dynatrace_tenant;
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
        dynatrace_api_token=Optional.ofNullable(System.getenv(SECRET_DT_API_TOKEN));
        dynatrace_tenant=Optional.ofNullable(System.getenv(SECRET_DT_TENANT));
        keptn_NAMESPACE=System.getenv(SECRET_KEPTN_NAMESPACE);
    }

    public String getKeptn_NAMESPACE() {
        return keptn_NAMESPACE;
    }

    public void setKeptn_NAMESPACE(String keptn_NAMESPACE) {
        this.keptn_NAMESPACE = keptn_NAMESPACE;
    }

    public void deleteController()
    {
        try
        {
            this.client = new DefaultKubernetesClient(config);

            logger.debug("deleteController - : delete service with label :"+NEOLOAD+"_"+CONTROLLER+","+NEOLOAD+context );
            client.services().inNamespace(keptn_NAMESPACE)
                    .withName(NEOLOAD+context)
                    .delete();
            logger.debug("deleteController - : delete pod with label :"+NEOLOAD+"_"+CONTROLLER+","+NEOLOAD+context );

            client.pods().inNamespace(keptn_NAMESPACE)
                    .withName(NEOLOAD+context)
                    .delete();


        }
        catch (Exception e)
        {
            logger.error("deleteController error ",e);
        }
    }
    public void deployController() throws NeoLoadJgitExeption {
        try
        {
            this.client = new DefaultKubernetesClient(config);

            logger.debug("deployController - : deploying pod with label :"+NEOLOAD+"_"+CONTROLLER+","+NEOLOAD+context );
            client.pods().inNamespace(keptn_NAMESPACE).createNew()
                        .withNewMetadata()
                        .withName(NEOLOAD+context)
                        .addToLabels(NEOLOAD+"_"+CONTROLLER,NEOLOAD+context)
                        .endMetadata()
                        .withNewSpec()
                        .addNewContainer()
                        .withName(NEOLOAD+context)
                        .withImagePullPolicy("IfNotPresent")
                        .withPorts(controllerPort())
                        .withNewImage(NEOLAOD_CTL_DOCKER)
                        .withEnv(controllerEnv())
                        .endContainer()
                        .endSpec()
                        .done();
            logger.debug("deployController - : deploying service with label :"+NEOLOAD+"_"+CONTROLLER+","+NEOLOAD+context );

            client.services().inNamespace(keptn_NAMESPACE).createNew()
                    .withNewMetadata()
                    .withName(NEOLOAD+context)
                    .addToLabels(NEOLOAD+"_"+CONTROLLER,NEOLOAD+context)
                    .endMetadata()
                    .withNewSpec()
                    .addNewPort().withPort(7400).withName("nlapi").withProtocol("TCP").withNewTargetPort().withIntVal(7400).endTargetPort().endPort()
                    .addNewPort().withPort(443).withName("nlssl").withProtocol("TCP").withNewTargetPort().withIntVal(443).endTargetPort().endPort()
                    .addNewPort().withPort(7200).withName("nlmon").withProtocol("TCP").withNewTargetPort().withIntVal(7200).endTargetPort().endPort()
                    .addNewPort().withPort(4569).withName("nlpoll").withProtocol("TCP").withNewTargetPort().withIntVal(4569).endTargetPort().endPort()
                    .addToSelector(NEOLOAD+"_"+CONTROLLER,NEOLOAD+context)
                    .withType(CLUSTER_IP)
                    .endSpec()
                    .done();


            Callable<Boolean> callable = new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    if(!client.pods().inNamespace(keptn_NAMESPACE).withName(NEOLOAD+context).isReady())
                    {
                        logger.debug("deployController - : pod controller not ready :");
                        // do something useful here
                        return false;
                    }
                    else
                    {
                        logger.info("deployController - : pod controller is ready ");
                        return true;

                    }

                }
            };
            Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                    .retryIfResult(Predicates.<Boolean>equalTo(false))
                    .retryIfRuntimeException()
                    .withWaitStrategy(WaitStrategies.fixedWait( 3, TimeUnit.SECONDS))
                    .withStopStrategy(StopStrategies.stopAfterDelay(5, TimeUnit.MINUTES))
                    .build();
            try {
                retryer.call(callable);
            } catch (RetryException e) {
                logger.error("deployController error ",e);
                throw new NeoLoadJgitExeption("Unable to get the pod controller ready");
            } catch (ExecutionException e) {
                logger.error("deployController error ",e);
                throw new NeoLoadJgitExeption("Unable to get the pod controller ready");
            }
            /*while(!client.pods().inNamespace(KEPTN_EVENT_URL).withName(NEOLOAD+context).isReady())
            {
                logger.debug("deployController - : pods controller not ready ");
                Thread.sleep(1000);
            }*/

        }
        catch (NeoLoadJgitExeption e)
        {
            logger.error("deployController error ",e);
            throw new NeoLoadJgitExeption("Unable to get the pod controller ready");
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



            logger.debug("deleteLG - : delete pods with label :"+NEOLOAD+"_"+LG+","+NEOLOAD+context +suffix);

            client.pods().inNamespace(keptn_NAMESPACE)
                    .withName(context+suffix)
                    .delete();


        }
        catch (Exception e)
        {
            logger.error("deleteLG error ",e);
        }
    }
    public void deployLG(String suffix) throws NeoLoadJgitExeption {
        try
        {
            this.client = new DefaultKubernetesClient(config);

            logger.debug("deployLG - : deploying pod with label :"+NEOLOAD+"_"+LG+","+NEOLOAD+LGname+context+suffix );

            client.pods().inNamespace(keptn_NAMESPACE).createNew()
                    .withNewMetadata()
                    .withName(context+suffix)
                    .addToLabels(NEOLOAD+"_"+LG,context+suffix)
                    .endMetadata()
                    .withNewSpec()
                    .addNewContainer()
                    .withName(context+suffix)
                    .withNewImage(NEOLAOD_LG_DOCKER)
                    .withEnv(lgenv(suffix))
                    .withImagePullPolicy("IfNotPresent")
                    .withPorts(lgPort())
                    .endContainer()
                    .withHostname(context+suffix)
                    .withHostNetwork(true)
                    .withDnsPolicy("ClusterFirstWithHostNet")
                    .endSpec()
                    .done();


            Callable<Boolean> callable = new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    if(!client.pods().inNamespace(keptn_NAMESPACE).withName(NEOLOAD+LGname+context+suffix).isReady())
                    {
                        logger.info("deployLG - : pod LG not ready :");
                        return false;
                    }
                    else {
                        logger.info("deployLG - : pod LG  is ready :");
                        return true;
                    }

                }
            };

            Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                    .retryIfResult(Predicates.<Boolean>equalTo(false))
                    .retryIfRuntimeException()
                    .withWaitStrategy(WaitStrategies.fixedWait(3, TimeUnit.SECONDS))
                    .withStopStrategy(StopStrategies.stopAfterDelay(5, TimeUnit.MINUTES))
                    .build();
            try {
                retryer.call(callable);
                logger.debug(client.pods().inNamespace(keptn_NAMESPACE).withName(NEOLOAD+LGname+context+suffix).get().getStatus().getPodIP());

            } catch (RetryException e) {
                logger.error("deployLG error ",e);
                throw new NeoLoadJgitExeption("Unable to get the pods ready");
            } catch (ExecutionException e) {
                logger.error("deployLG error ",e);
                throw new NeoLoadJgitExeption("Unable to get the pods ready");
            }

        }
        catch (NeoLoadJgitExeption e)
        {
            logger.error("deployLG error ",e);
            throw new NeoLoadJgitExeption("Unable to get the pod controller ready");
        }
        catch (Exception e)
        {
            logger.error("deployLG error ",e);
        }
    }



    private List<ContainerPort> lgPort()
    {
        List<ContainerPort> containerPorts=new ArrayList<>();
        ContainerPort port=new ContainerPort();
        port.setContainerPort(7100);
        port.setHostPort(7100);
        port.setProtocol("TCP");
        port.setName("nllg");
        containerPorts.add(port);
        return containerPorts;
    }

    private List<ContainerPort> controllerPort()
    {
        List<ContainerPort> containerPorts=new ArrayList<>();
        ContainerPort port=new ContainerPort();
        port.setContainerPort(7400);
        port.setHostPort(7400);
        port.setProtocol("TCP");
        port.setName("nlapi");

        containerPorts.add(port);
        ContainerPort mon=new ContainerPort();
        mon.setContainerPort(7200);
        mon.setHostPort(7200);
        mon.setProtocol("TCP");
        mon.setName("nlmon");
        containerPorts.add(mon);


        ContainerPort ssl=new ContainerPort();
        ssl.setContainerPort(443);
        ssl.setHostPort(443);
        ssl.setProtocol("TCP");
        ssl.setName("nlssl");
        containerPorts.add(ssl);

        ContainerPort poll=new ContainerPort();
        poll.setContainerPort(4569);
        poll.setHostPort(4569);
        poll.setProtocol("TCP");
        poll.setName("nlpoll");
        containerPorts.add(poll);
        return containerPorts;
    }

    private List<EnvVar> controllerEnv()
    {
       List<EnvVar> list=new ArrayList<>();

       createEnvList(list);
       list.add(new EnvVar(ENV_MODE,ENV_MANAGED,null));
       list.add(new EnvVar(ENV_LEASE_SERVER,LEASE_SERVER,null));

      logger.debug("controllerenv : "+list.toString());
       return list;

    }

    private List<EnvVar> lgenv(String suffix)
    {
        List<EnvVar> list=new ArrayList<>();

        createEnvList(list);
        list.add(new EnvVar(ENV_MODE,ENV_MANAGED,null));
      //  list.add(new EnvVar(ENV_LG_HOST,generateServiceName(LGname,suffix),null));
           EnvVar env=new EnvVarBuilder()
                .withName(ENV_LG_HOST)
                .withNewValueFrom()
                .withNewFieldRef()
                .withFieldPath("status.podIP")
                .endFieldRef()
                .endValueFrom()
                .build();
         list.add(env);
         list.add(new EnvVar(ENV_LG_PORT,LG_PORT,null));

        logger.debug("lgenv : "+list.toString());

        return list;

    }

    private String generateServiceName(String type,String suffix)
    {
        return NEOLOAD+type+context+suffix+"."+keptn_NAMESPACE+".svc.cluster.local";
    }

    private void createEnvList(final List<EnvVar> list )
    {

        if(neoloadweb_apiurl.isPresent()) {
            if(!neoloadweb_apiurl.get().equalsIgnoreCase(DEFAULT_API_SAAS))
                list.add(new EnvVar(ENV_NEOLOADWEB_URL, "https://" + neoloadweb_apiurl.get() + "/v1", null));
        }
        list.add(new EnvVar(ENV_NEOLOADWEB_TOKEN,neoloadAPitoken,null));

        if(neoloadZoneid.isPresent())
            list.add(new EnvVar(ENV_ZONE,neoloadZoneid.get(),null));
    }
}
