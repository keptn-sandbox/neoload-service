package com.keptn.neotys.testexecutor.messageHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.keptn.neotys.testexecutor.EventSender.NeoLoadEvent;
import com.keptn.neotys.testexecutor.KeptnEvents.KeptnEventFinished;
import com.keptn.neotys.testexecutor.NeoLoadFolder.ProjectSettings;
import com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel.*;
import com.keptn.neotys.testexecutor.NeoLoadFolder.variables.NeoLoadModel;
import com.keptn.neotys.testexecutor.NeoLoadFolder.variables.NlConstants;
import com.keptn.neotys.testexecutor.cloudevent.KeptnExtensions;
import com.keptn.neotys.testexecutor.exception.NeoLoadJgitExeption;
import com.keptn.neotys.testexecutor.exception.NeoLoadSerialException;
import com.keptn.neotys.testexecutor.kubernetes.NeoLoadKubernetesClient;
import com.keptn.neotys.testexecutor.log.KeptnLogger;
import com.keptn.neotys.testexecutor.ressource.ConfigurationApi;
import com.keptn.neotys.testexecutor.ressource.KeptnRessource;
import com.neotys.ascode.swagger.client.ApiClient;
import com.neotys.ascode.swagger.client.ApiException;
import com.neotys.ascode.swagger.client.api.ResultsApi;
import com.neotys.ascode.swagger.client.api.RuntimeApi;
import com.neotys.ascode.swagger.client.model.ProjectDefinition;
import com.neotys.ascode.swagger.client.model.RunTestDefinition;
import com.neotys.ascode.swagger.client.model.TestDefinition;
import io.cloudevents.CloudEvent;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Future;
import io.vertx.reactivex.core.Vertx;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.*;

public class NeoLoadHandler {

    KeptnLogger logger;
    private String keptncontext;
    private KeptnExtensions extensions;
    private KeptnEventFinished keptnEventFinished;
    Path gitfolder;
    private String eventid;
    private String stage;
    private Optional<String> tempfile;
    private List<NeoLoadTestStep> neoLoadTestStepList;

    public NeoLoadHandler( KeptnEventFinished keptnEventFinishedCloudEvent, KeptnExtensions extensions, String eventid) throws IOException, NeoLoadJgitExeption, NeoLoadSerialException {
        this.keptnEventFinished=keptnEventFinishedCloudEvent;
        logger = new KeptnLogger(this.getClass().getName());
        logger.setKepncontext(extensions.getShkeptncontext());
        keptncontext=extensions.getShkeptncontext();
        this.stage=keptnEventFinished.getStage();
        this.eventid=eventid;
        tempfile=Optional.empty();
        this.extensions=extensions;


    }

    private Future<List<NeoLoadTestStep>> getRessources(Vertx vertx) throws NeoLoadSerialException, NeoLoadJgitExeption {
        ConfigurationApi configurationApi=new ConfigurationApi(logger,vertx,keptnEventFinished.getProject(),keptnEventFinished.getStage(),keptnEventFinished.getService());
        Future<KeptnRessource> keptnRessource=configurationApi.getRessource(NEOLOAD_CONFIG_FILE,NEOLOAD_CONFIG_FILE_OPTION2);
        Future<List<NeoLoadTestStep>> listFuture=Future.future();

        keptnRessource.setHandler(result->{
            if(result.succeeded())
            {
                KeptnRessource resourceObject =result.result();
                logger.debug("Ressource file found " + resourceObject.getResourceURI());
                String yaml = resourceObject.getDecodedRessourceContent();
                logger.debug("YAML received : "+yaml);
                NeoLoadDataModel neoLoadDataModel = new Yaml().loadAs(yaml, NeoLoadDataModel.class);
                if (neoLoadDataModel == null) {
                    logger.debug("getNeoLoadTest - no able to deserialize the yaml file");
                    listFuture.fail(new NeoLoadSerialException("Unable to deserialize YAML file "));
                }
                if (neoLoadDataModel.getWorkloads().size() < 0) {
                    logger.debug("getNeoLoadTest - there is no testing steps");
                    listFuture.fail(new NeoLoadJgitExeption("There is no testing steps define "));

                }

                final ArrayList<NeoLoadTestStep> neoLoadTestSteps = new ArrayList<>();
                listFuture.complete(neoLoadDataModel.getWorkloads());
            } else {
                logger.error("No Ressrouce " + NEOLOAD_CONFIG_FILE + " found for project " + keptnEventFinished.getProject() + " and stage " + keptnEventFinished.getStage());
                logger.info("trying to  " + NEOLOAD_CONFIG_FILE + " found for project " + keptnEventFinished.getProject() + " and stage " + keptnEventFinished.getStage());
                listFuture.fail(new NeoLoadJgitExeption("No Ressrouce " + NEOLOAD_CONFIG_FILE + " found for project " + keptnEventFinished.getProject() + " and stage " + keptnEventFinished.getStage()));

            }
            });

        return listFuture;

    }
    private  String compressNLProject(String sourcefolder,String projectname) throws IOException {
    		String nameofZipfile=new File(sourcefolder).getParentFile().getName();
    		String nameofZipfolder=new File(sourcefolder).getParentFile().getParentFile().toString();
    		Path p;

    		String dist=sourcefolder+"/"+projectname+".zip";

    		try {
    			p = Files.createFile(Paths.get(dist));
    		}
    		catch(FileAlreadyExistsException e)
    		{
    			p=Paths.get(dist);
    		}
    		try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p))) {
    			Path pp = Paths.get(sourcefolder);
    			Files.walk(pp)
    					.filter(path -> !Files.isDirectory(path))
                        .filter(path -> !path.equals(Paths.get(sourcefolder,projectname+".zip")))
    					.forEach(path -> {
    						ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
    						try {
    						    logger.debug("Compress - adding "+pp.relativize(path).toString());
    							zs.putNextEntry(zipEntry);
    							Files.copy(path, zs);
    							zs.closeEntry();
    						} catch (IOException e) {
    						    logger.error("Issue to generate the zip ",e);
    						}
    					});
    		}
    		return dist;
    	}

    private void deletetempfolder() throws IOException {
        boolean delete=deleteDirectory(new File(gitfolder.toAbsolutePath().toString()+TMP_NEOLOAD_FOLDER));
    }

    private void logYamlFile(String path)
    {
        try
        {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            logger.debug("Looking a the content of the file "+path);
            StringBuilder stringBuilder=new StringBuilder();
            while (myReader.hasNextLine()) {
                stringBuilder.append(myReader.nextLine()+"\n") ;
            }
            logger.debug(stringBuilder.toString());
            myReader.close();
        }
     catch (FileNotFoundException e) {
            logger.error("Error while parsing files ",e);
        }

    }
    private File updateNeoLoadAsCodeFile(File path) throws NeoLoadSerialException {
        try
        {
            Yaml yaml = new Yaml();
            FileInputStream input= new FileInputStream(path);

            Map<String, Object> nlascodeObj = yaml.load(input);
            JsonObject jsonObject= JsonObject.mapFrom(nlascodeObj);


            if(jsonObject.containsKey("scenarios"))
            {


                JsonArray jsonArray=jsonObject.getJsonArray("scenarios");

                List<JsonObject> scenarioList=jsonArray.stream().map(o -> {
                            if(o instanceof JsonObject)
                            {
                                JsonObject scenario=(JsonObject) o ;
                                logger.debug("Class properly converted");
                                //--- update the apm settings---
                                JsonArray keptn=new JsonArray();
                                keptn.add(KEPTN_TAG_PROJECT+this.keptnEventFinished.getProject());
                                keptn.add(KEPTN_TAG_STAGE+this.keptnEventFinished.getStage());
                                keptn.add(KEPTN_TAG_SERVICE+this.keptnEventFinished.getService());

                                if( scenario.containsKey("apm_configuration"))
                                {
                                    JsonObject apmconfiguration=scenario.getJsonObject("apm_configuration");
                                    if(apmconfiguration.containsKey("dynatrace_tags"))
                                    {
                                        apmconfiguration.remove("dynatrace_tags");
                                        apmconfiguration.put("dynatrace_tags",keptn);
                                    } else
                                        apmconfiguration.put("dynatrace_tags",keptn);
                                }
                                else
                                {
                                    JsonObject apmconfiguration=new JsonObject();
                                    apmconfiguration.put("dynatrace_tags",keptn);
                                    scenario.put("apm_configuration",apmconfiguration);
                                }
                                return scenario;
                            }
                            else
                            {
                                Scenario scenario = (Scenario) o;
                                logger.debug("TEst cast of scenario "+ scenario.getName());
                                logger.error("Unable to cast object");
                                return null;
                            }
                }).filter(scenario -> scenario!=null).collect(Collectors.toList());
                JsonArray scenaraary=new JsonArray();
                scenarioList.stream().forEach(entries -> scenaraary.add(entries));
                jsonObject.remove("scenarios");
                jsonObject.put("scenarios",scenaraary);
                logger.debug("Modified Json "+ jsonObject.toString());
                String newYaml= asYaml(jsonObject.toString());
                FileWriter fileWriter=new FileWriter(path);
                fileWriter.write(newYaml);
                fileWriter.close();
                logger.debug("YAML : "+newYaml);
                return path;
            }
            else
            {
                return path;
            }

        } catch (FileNotFoundException e) {
            logger.error("Yaml as code file not found",e);
            throw new NeoLoadSerialException("Yaml as code file not found "+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String asYaml(String jsonString) throws JsonProcessingException, IOException {
        // parse JSON
        JsonNode jsonNodeTree = new ObjectMapper().readTree(jsonString);
        // save it as YAML
        String jsonAsYaml = new YAMLMapper().writeValueAsString(jsonNodeTree);
        return jsonAsYaml;
    }
    private String getAsCodeFiles(List<Project> projectPath)
    {
        List<String> projectwithoutnlp=projectPath.stream().map(pro->{return pro.getPath();}).filter(file->!file.toLowerCase().contains(NLP_EXTENSION)).collect(Collectors.toList());
        if(tempfile.isPresent())
            projectwithoutnlp.add(tempfile.get());
        return projectwithoutnlp.stream().map(project->{return new File(project).getName();}).collect(Collectors.joining(","));
    }

    private String createZipFile(List<String> projectPath, String projectName, Optional<List<Constants>> constant_variables,Optional<String> dynatraceTenant,Optional<String> dynatraceToken) throws IOException, NeoLoadJgitExeption {
        Path path = Paths.get(gitfolder.toAbsolutePath().toString()+TMP_NEOLOAD_FOLDER);
        if(!Files.exists(path))
            Files.createDirectory(path);

        List<String> nlproject=projectPath.stream().filter(file->file.toLowerCase().contains(NLP_EXTENSION)).collect(Collectors.toList());
        if(nlproject.size()>1)
            throw new NeoLoadJgitExeption("Each project can have only one .nlp defined");

        if(nlproject.size()>0)
        {
            Path nlp_project=Paths.get(nlproject.get(0)).getParent().toAbsolutePath();
            Path project_folder=Paths.get(gitfolder.toString(),nlp_project.toString()).toAbsolutePath();
            logger.debug("Create ZipFile - found a nlp project copy the folder of folder : "+project_folder.toAbsolutePath().toString());
            FileUtils.copyDirectory(project_folder.toAbsolutePath().toFile(),path.toFile());
        }
        List<String> projectwithoutnlp=projectPath.stream().filter(file->!file.toLowerCase().contains(NLP_EXTENSION)).collect(Collectors.toList());
        List<Exception> error=new ArrayList<>();
        projectwithoutnlp.stream().forEach(file->
        {
            try {
                logger.debug("Yaml file :  "+gitfolder.toAbsolutePath().toString()+file+" has been added in the zip");
                logYamlFile(gitfolder.toAbsolutePath().toString()+file);
                if(dynatraceTenant.isPresent() && dynatraceToken.isPresent())
                {
                    //---if dynatrace integration then add the keptn tags
                    File transformedfile=updateNeoLoadAsCodeFile(new File(gitfolder.toAbsolutePath().toString()+file));
                    logger.debug("neoload as code file modified :");
                    logYamlFile(transformedfile.getAbsolutePath());
                    FileUtils.copyFileToDirectory(transformedfile,path.toFile());
                }
                else
                    FileUtils.copyFileToDirectory(new File(gitfolder.toAbsolutePath().toString()+file),path.toFile());

            } catch (IOException | NeoLoadSerialException e) {
                error.add(e);
            }
        });
        if( constant_variables.isPresent()&&constant_variables.get().size()>0)
        {
            NeoLoadModel model=new NeoLoadModel(constant_variables.get().stream().map(constants -> {
                return new NlConstants(constants);
            }).collect(Collectors.toList()));
            Yaml yaml;
            if(dynatraceTenant.isPresent()&& dynatraceToken.isPresent())
            {
                model.setProject_settings(new ProjectSettings( true,dynatraceTenant.get(),dynatraceToken.get()));
                yaml=new Yaml(model.getProject_settings().getRepresenter());

            }
            else
            {
               yaml=new Yaml();
            }


            tempfile=Optional.of(path.toAbsolutePath().toString()+"/"+keptnEventFinished.getService()+"."+keptncontext+YAML_EXTENSION);
            try {
                yaml.dump(model, new FileWriter(tempfile.get()));
                logger.debug("Constant yaml file created : " + tempfile.get());
                logYamlFile(tempfile.get());
            }
            catch (IOException e)
            {
                logger.error("Issue to create the constant yaml file",e);
                error.add(e);
            }
        }
        else
        {
            if(dynatraceTenant.isPresent()&& dynatraceToken.isPresent())
            {
                NeoLoadModel model=new NeoLoadModel();
                model.setProject_settings(new ProjectSettings( true,dynatraceTenant.get(),dynatraceToken.get()));
                Yaml yaml=new Yaml(model.getProject_settings().getRepresenter());

                tempfile=Optional.of(path.toAbsolutePath().toString()+"/"+keptnEventFinished.getService()+"."+keptncontext+YAML_EXTENSION);
                try {
                    yaml.dump(model, new FileWriter(tempfile.get()));
                    logger.debug("Settings yaml file created : " + tempfile.get());
                    logYamlFile(tempfile.get());
                }
                catch (IOException e)
                {
                    logger.error("Issue to create the constant yaml file",e);
                    error.add(e);
                }
            }
        }
         if(error.size()>0)
        {
            throw new NeoLoadJgitExeption("several tecnical error : "+ error.stream().map(e -> {
                return e.getLocalizedMessage();
            }).collect(Collectors.joining("\n")));
        }

        return compressNLProject(path.toAbsolutePath().toString(),projectName+keptnEventFinished.getService());

    }
    private KeptnEventFinished RunTest(File zipfile, NeoLoadTestStep test, Optional<String> nlapi, Optional<String> nlapitoken, Optional<String> nlurl, Optional<String> uploadurl, Optional<String> nlzoneid, int size, NeoLoadEvent neoLoadEvent, CloudEvent<Object> receivedEvent, String keptn_namespace) throws ApiException, NeoLoadJgitExeption {
       if(!nlapi.isPresent())
           throw new NeoLoadJgitExeption("No API URL Defined. installtion of the neoload service has not been configured properly");

        if(!nlurl.isPresent())
            throw new NeoLoadJgitExeption("No NeoLoad URL Defined. installtion of the neoload service has not been configured properly");

        if(!uploadurl.isPresent())
            throw new NeoLoadJgitExeption("No Upload URL Defined. installtion of the neoload service has not been configured properly");

        if(!nlapitoken.isPresent())
            throw new NeoLoadJgitExeption("No neoload web API token Defined. installtion of the neoload service has not been configured properly");

        if(!nlzoneid.isPresent())
            throw new NeoLoadJgitExeption("No Neoload web zone id Defined. installtion of the neoload service has not been configured properly");

        if(!zipfile.exists())
            throw new NeoLoadJgitExeption("Zip file does not exists "+ zipfile.getAbsolutePath().toString());


        ApiClient nlWebApiClient=new ApiClient();
        nlWebApiClient.setApiKey(nlapitoken.get());
        nlWebApiClient.setBasePath(NLWEB_PROTOCOL+uploadurl.get()+NLWEB_APIVERSION);

        RuntimeApi runtimeApi=new RuntimeApi(nlWebApiClient);
        Calendar cal = Calendar.getInstance();
        cal.setTime(Date.from(Instant.now()));

        if(zipfile.exists())
        {
            try{

            logger.debug("Uploading zip file + "+zipfile.getAbsolutePath().toString());

            logger.debug(zipfile.getAbsolutePath().toString()+" is "+ zipfile.getUsableSpace()+" bytes");

            ProjectDefinition projectDefinition = runtimeApi.postUploadProject(zipfile);
            nlWebApiClient.setBasePath(NLWEB_PROTOCOL+nlapi.get()+NLWEB_APIVERSION);
            nlWebApiClient.setApiKey(nlapitoken.get());
            runtimeApi=new RuntimeApi(nlWebApiClient);

            RunTestDefinition runTestDefinition = runtimeApi.getTestsRun(KEPTN_EVENT_URL+"_"+keptnEventFinished.getProject()+"_"+keptnEventFinished.getService()+"_"+test.getProperties().getScenario(), projectDefinition.getProjectId(), test.getProperties().getScenario(), test.getDescription(),getAsCodeFiles(test.getScript().getProject()),null,null,null,null,nlzoneid.get(),nlzoneid.get()+":"+String.valueOf(size));
            NeoLoadWebTest neoLoadWebTest=new NeoLoadWebTest(runTestDefinition.getTestId(),NLWEB_PROTOCOL+nlurl.get() + "/#!trend/?scenario=" + test.getProperties().getScenario() + "&limit=-1&project=" + projectDefinition.getProjectId(),NLWEB_PROTOCOL+nlurl.get() + "/#!result/" + runTestDefinition.getTestId() + "/overview");

            ///---send the test started event----
            keptnEventFinished.setMessage("Test started : "+neoLoadWebTest.getTesturl());
            keptnEventFinished.setStatus("succeeded");
            keptnEventFinished.setResult("pass");
            neoLoadEvent.sendTestStarted(keptnEventFinished,extensions,receivedEvent,keptn_namespace);


            logger.info("Trending URL : " + neoLoadWebTest.getTrendingurl());
            logger.info("Testing result url : " + neoLoadWebTest.getTesturl());
            logger.info("Init of the Test..... ");
            NLWebTestStatus status = new NLWebTestStatus(nlWebApiClient.getBasePath(), nlapitoken.get(), neoLoadWebTest.getTestid(), logger);
            String teststatus = status.getFinalTestStatus();
            neoLoadWebTest.setTestStatus(teststatus);
            if (teststatus.equalsIgnoreCase(TEST_STATUS_FAIL))
            {
                logger.info("Test has FAILED");
            } else {
                logger.info("Test has finished with sucess");
            }

            keptnEventFinished.setTeststatus(teststatus);
            ResultsApi resultsApi=new ResultsApi(nlWebApiClient);
            TestDefinition testdefinition=resultsApi.getTest(runTestDefinition.getTestId());
            keptnEventFinished.setStart(testdefinition.getStartDate());
            keptnEventFinished.setEnd(testdefinition.getEndDate());
            keptnEventFinished.setTestid(testdefinition.getId());
            keptnEventFinished.setNeoloadURL(neoLoadWebTest.getTesturl());
            keptnEventFinished.setMessage("Test Ended : "+neoLoadWebTest.getTesturl());
            keptnEventFinished.setStatus("succeeded");
            if(teststatus.equalsIgnoreCase("PASSED"))
                keptnEventFinished.setResult("pass");
            else
                keptnEventFinished.setResult("fail");
            return keptnEventFinished;

            }
            catch (ApiException e)
            {
                logger.error("Technical Error Body:"+e.getResponseBody(),e);
                throw e;
            }
            catch(Exception e)
            {
                logger.error("Technical Error ",e);
                throw new NeoLoadJgitExeption("Technical error "+ e.getMessage());
            }

        }
        else
        {
            throw new NeoLoadJgitExeption("No zip file found");
        }


    }

    private void deleteGitFolder() throws IOException {
        logger.debug("deleteGitFolder - delete gitfolder "+gitfolder.toAbsolutePath().toString());
        boolean delete=deleteDirectory(new File(gitfolder.toAbsolutePath().toString()));
        if(delete)
            logger.info("deleteGitFolder - deleted gitfolder "+gitfolder.toAbsolutePath().toString());

    }



    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
    private void runNLScenario(NeoLoadTestStep test, Vertx rxvertx, CloudEvent<Object> receivedEvent)
    {
        NeoLoadEvent neoLoadEvent=new NeoLoadEvent(logger,eventid,rxvertx);

        final NeoLoadKubernetesClient neoLoadKubernetesClient=new NeoLoadKubernetesClient(keptncontext);
        int numberofInstances = 0;
        String zoneid = null;
        if(test.getInfrastructure().isManagedbyKeptn())
        {
            List<String> machinelist = null;
            try {
                if (test.checkProject(gitfolder)) {
                    machinelist = test.getNameOfLGtoStart();
                    logger.debug("This scenario " + test.getProperties().getScenario() + " requires " + machinelist.size() + " NeoLoad LoadGenerator");
                    //----start machines----

                    neoLoadKubernetesClient.deployController();

                    List<String> error = new ArrayList<>();
                    machinelist.stream().forEach(machine -> {
                        try {
                            neoLoadKubernetesClient.deployLG(machine);
                        } catch (NeoLoadJgitExeption neoLoadJgitExeption) {
                            logger.error("deployment issue ", neoLoadJgitExeption);
                            error.add(neoLoadJgitExeption.getMessage());

                        }
                    });
                    if(error.size()>0)
                        throw new NeoLoadJgitExeption("Issue while deploying LGs");

                    zoneid=neoLoadKubernetesClient.getNeoloadZoneid().get();
                    numberofInstances=machinelist.size();
                }
                }
            catch (NeoLoadJgitExeption e)
            {
                logger.error("runNLScenario exepption ",e);
            }
            catch (NeoLoadSerialException e)
            {
                logger.error("runNLScenario exepption ",e);
            } catch (IOException e) {
                logger.error("runNLScenario exepption ",e);
            }
        }
        else {
            numberofInstances=test.getInfrastructure().getNumberOfMachine().intValue();
            zoneid=test.getInfrastructure().getZoneId();

        }
        try
        {

                List<String> projectspath=test.getScript().getProject().stream().map(project -> project.getPath()).collect(Collectors.toList());
                String zipfilepath=createZipFile(projectspath, keptnEventFinished.getProject(),Optional.ofNullable(test.getProperties().getConstant_variables()),neoLoadKubernetesClient.getDynatrace_tenant(),neoLoadKubernetesClient.getDynatrace_api_token());

                Thread.sleep(20000);


                //run the test
                KeptnEventFinished keptnEventFinished=RunTest(new File(zipfilepath),test,neoLoadKubernetesClient.getNeoloadweb_apiurl(),Optional.ofNullable(neoLoadKubernetesClient.getNeoloadAPitoken()),neoLoadKubernetesClient.getNeoloadweb_url(),neoLoadKubernetesClient.getNeoloadweb_uploadurl(),Optional.ofNullable(zoneid),numberofInstances,neoLoadEvent,receivedEvent,neoLoadKubernetesClient.getKeptn_NAMESPACE());

                ///--
                neoLoadEvent.endevent(keptnEventFinished,extensions,receivedEvent,neoLoadKubernetesClient.getKeptn_NAMESPACE());
                //--send end event-------------


        }
        catch (ApiException | InterruptedException e) {
            logger.error("RUnNLScenario , api exception",e);
            keptnEventFinished.setMessage("APIError when running scenario "+e.getMessage());
            keptnEventFinished.setStatus("errored");
            keptnEventFinished.setResult("fail");
            neoLoadEvent.changeevent(keptnEventFinished,extensions,receivedEvent,neoLoadKubernetesClient.getKeptn_NAMESPACE());
        }catch (Exception e)
        {
            logger.error("Technical error ",e);
            keptnEventFinished.setMessage("Technical Error when running scenario "+e.getMessage());
            keptnEventFinished.setStatus("errored");
            keptnEventFinished.setResult("fail");
            neoLoadEvent.changeevent(keptnEventFinished,extensions,receivedEvent,neoLoadKubernetesClient.getKeptn_NAMESPACE());
        }
        finally {
            if(neoLoadKubernetesClient!=null)
            {
                try {
                    //delete infra
                    if (numberofInstances >0) {
                        if (test.getInfrastructure().isManagedbyKeptn()) {
                            test.getNameOfLGtoStart().stream().forEach(machine-> {

                                    neoLoadKubernetesClient.deleteLG(machine);

                              });
                            neoLoadKubernetesClient.deleteController();
                        }

                    }
                }
                catch (Exception e)
                {
                    logger.error("Unable to delete services ",e);
                }
            }
            try {
                deletetempfolder();
                logger.debug("Folder deleted");
            } catch (IOException e) {
                logger.error("Unable to delete temp folder ",e);
            }
        }
    }


    public void runNeoLoadTest(Vertx rxvertx, CloudEvent<Object> receivedEvent) throws NeoLoadJgitExeption, NeoLoadSerialException, IOException {
       // gitfolder = getNeoLoadTestFolder();
        Future<List<NeoLoadTestStep>> listFuture=getRessources(rxvertx);

        listFuture.setHandler(listAsyncResult -> {
            if(listAsyncResult.succeeded())
            {
                neoLoadTestStepList=listAsyncResult.result();
                StringBuilder error=new StringBuilder();

                //---for each test start test -----
                neoLoadTestStepList.stream().filter(neoLoadTestStep -> neoLoadTestStep.getTeststrategy().equalsIgnoreCase(keptnEventFinished.getStrategy())).forEach(step->{
                    logger.debug("Clonning repo :"+step.getScript().getRepository());

                    try {
                        gitfolder=getNeoLoadTestFolder(getGitHubFolder(step.getScript().getRepository()),step.getScript().getBranch(),step.getScript().isSecured());
                    } catch (IOException e) {
                        error.append("Technical Error while retrieveing repository "+e.getMessage());
                    } catch (NeoLoadJgitExeption neoLoadJgitExeption) {
                        error.append("Technical Error while retrieveing repository "+neoLoadJgitExeption.getMessage());
                    }
                    logger.debug("Running step : "+step.getProperties().getScenario());
                    runNLScenario(step,rxvertx,receivedEvent);
                    logger.debug("end step : "+step.getProperties().getScenario());
                    try {
                        deleteGitFolder();
                    } catch (IOException e) {
                        error.append("Technical Error while deleting temporary repository "+e.getMessage());
                    }
                });
            }
            else
            {
                if(listAsyncResult.failed()) {
                    logger.error("Error to receive the test steps",listAsyncResult.cause());
                }
            }
        });






    }



    private Path getNeoLoadTestFolder(String ressource, String branch, Boolean secured)
    {
        try {
             Optional<String> scm_user;
             Optional<String> scm_password;
             scm_user= Optional.ofNullable(System.getenv(SECRET_SCM_USER));
             scm_password=Optional.ofNullable(System.getenv(SECRET_SCM_PASSWORD));
             Path localPath = Files.createTempDirectory("Gitfolder_" + keptncontext);
             logger.debug("getNeoLoadTestFolder - local directory created "+localPath);

             if(keptnEventFinished!=null) {
                logger.debug("getNeoLoadTestFolder - start clonning repo  "+getGitHubFolder(ressource));

                 Git result;
                 if(secured) {
                     if (scm_user.isPresent() && !scm_user.get().isEmpty() && scm_password.isPresent() && scm_password.get().isEmpty()) {
                         logger.debug("Clone the repo without using credentials");
                         result = Git.cloneRepository()
                                 .setURI(getGitHubFolder(ressource))
                                 .setDirectory(localPath.toFile())
                                 .setCredentialsProvider(new UsernamePasswordCredentialsProvider(scm_user.get(), scm_password.get()))
                                 .setBranch(branch)
                                 .call();
                     } else {
                         throw new NeoLoadJgitExeption("Impossible to clone the repo - Secure repository - There is no SCM credentials in the NeoLaod Secret");
                     }
                 }
                else
                {
                    logger.debug("Clone the repo without using no credentials");
                    result = Git.cloneRepository()
                            .setURI(getGitHubFolder(ressource))
                            .setDirectory(localPath.toFile())
                            .setBranch(branch)
                            .call();
                }
                logger.debug("getNeoLoadTestFolder - end clonning repo  "+getGitHubFolder(ressource));

                // Note: the call() returns an opened repository already which needs to be closed to avoid file handle leaks!
                logger.info("Having repository: " + result.getRepository().getDirectory());
                return localPath;
             }
             else
             {
                throw new NeoLoadJgitExeption("THe event data has not been converted");
             }
            } catch (IOException e) {
            logger.error("error on getNeoLoadTestFolder",e);
        } catch (InvalidRemoteException e) {
            logger.error("error on getNeoLoadTestFolder",e);
        } catch (TransportException e) {
            logger.error("error on getNeoLoadTestFolder",e);
        } catch (NeoLoadJgitExeption neoLoadJgitExeption) {
            logger.error("error on getNeoLoadTestFolder",neoLoadJgitExeption);
        } catch (GitAPIException e) {
            logger.error("error on getNeoLoadTestFolder",e);
        }

        return null;
    }

    private boolean hasNeoLoadFolder(Path path)
    {
        File dir=new File(path.toAbsolutePath().toString() + "/" + NEOLOAD_FOLDER);
        if(dir.exists())
            return true;
        else
            return false;
    }

    private boolean hasNeoLoadKeptn(Path path)
    {
        File neoloadConfigFile = new File(path.toAbsolutePath().toString()+"/"+NEOLOAD_FOLDER+"/"+NEOLOAD_CONFIG_FILE);
        if(neoloadConfigFile.exists())
            return true;
        else
            return false;
    }

    private String getGitHubFolder(String repository) throws IOException, NeoLoadJgitExeption {
        String gitFolder;

        if (keptnEventFinished !=null) {
            if(repository.endsWith(".git"))
                gitFolder=repository;
            else
                gitFolder =repository +".git";
            return gitFolder;

        } else {
            throw new NeoLoadJgitExeption("No data in Event");

        }

    }

}
