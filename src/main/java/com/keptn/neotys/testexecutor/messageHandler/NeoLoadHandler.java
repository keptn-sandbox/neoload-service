package com.keptn.neotys.testexecutor.messageHandler;

import com.keptn.neotys.testexecutor.EventSender.NeoLoadEndEvent;
import com.keptn.neotys.testexecutor.KeptnEvents.KeptnEventFinished;
import com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel.NeoLoadDataModel;
import com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel.NeoLoadTest;
import com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel.NeoLoadTestStep;
import com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel.Project;
import com.keptn.neotys.testexecutor.cloudevent.KeptnExtensions;
import com.keptn.neotys.testexecutor.exception.NeoLoadJgitExeption;
import com.keptn.neotys.testexecutor.exception.NeoLoadSerialException;
import com.keptn.neotys.testexecutor.kubernetes.NeoLoadKubernetesClient;
import com.keptn.neotys.testexecutor.log.KeptnLogger;
import com.neotys.ascode.swagger.client.ApiClient;
import com.neotys.ascode.swagger.client.ApiException;
import com.neotys.ascode.swagger.client.api.RuntimeApi;
import com.neotys.ascode.swagger.client.model.ProjectDefinition;
import com.neotys.ascode.swagger.client.model.RunTestDefinition;
import io.cloudevents.CloudEvent;
import io.vertx.core.json.JsonObject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.apache.commons.io.FileUtils;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    public NeoLoadHandler(KeptnEventFinished keptnEventFinishedCloudEvent, KeptnExtensions extensions, String eventid) throws IOException, NeoLoadJgitExeption {
        this.keptnEventFinished=keptnEventFinishedCloudEvent;
        logger = new KeptnLogger(this.getClass().getName());
        logger.setKepncontext(extensions.getShkeptncontext());
        keptncontext=extensions.getShkeptncontext();
        eventid=eventid;
        gitfolder= getNeoLoadTestFolder();
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
    					.forEach(path -> {
    						ZipEntry zipEntry = new ZipEntry(pp.relativize(path).toString());
    						try {
    							zs.putNextEntry(zipEntry);
    							Files.copy(path, zs);
    							zs.closeEntry();
    						} catch (IOException e) {
    							System.err.println(e);
    						}
    					});
    		}
    		return dist;
    	}

    private void deletetempfolder() throws IOException {
        Path path = Paths.get(gitfolder.toAbsolutePath()+"/tempneoload");
        Files.delete(path);
    }

    private String getAsCodeFiles(List<Project> projectPath)
    {
        List<String> projectwithoutnlp=projectPath.stream().map(pro->{return pro.getPath();}).filter(file->!file.toLowerCase().contains(NLP_EXTENSION)).collect(Collectors.toList());
        return projectwithoutnlp.stream().map(project->{return new File(project).getName();}).collect(Collectors.joining(","));
    }

    private String createZipFile(List<String> projectPath, String projectName) throws IOException, NeoLoadJgitExeption {
        Path path = Paths.get(gitfolder.toAbsolutePath()+"/tempneoload");
        if(!Files.exists(path))
            Files.createDirectory(path);

        List<String> nlproject=projectPath.stream().filter(file->file.toLowerCase().contains(NLP_EXTENSION)).collect(Collectors.toList());
        if(nlproject.size()>1)
            throw new NeoLoadJgitExeption("Each project can have only one .nlp defined");

        if(nlproject.size()>0)
        {
            Path nlp_project=Paths.get(nlproject.get(0)).getParent().toAbsolutePath();
            logger.debug("Create ZipFile - found a nlp project copy the folder");
            FileUtils.copyDirectory(nlp_project.toAbsolutePath().toFile(),path.toFile());
        }
        List<String> projectwithoutnlp=projectPath.stream().filter(file->!file.toLowerCase().contains(NLP_EXTENSION)).collect(Collectors.toList());
        List<Exception> error=new ArrayList<>();
        projectwithoutnlp.stream().forEach(file->
        {
            try {
                FileUtils.copyFileToDirectory(new File(file),path.toFile());
            } catch (IOException e) {
                error.add(e);
            }
        });
        if(error.size()>0)
        {
            throw new NeoLoadJgitExeption("several tecnical error : "+ error.stream().map(e -> {
                return e.getLocalizedMessage();
            }).collect(Collectors.joining("\n")));
        }

        return compressNLProject(path.toAbsolutePath().toString(),projectName);

    }
    private NeoLoadWebTest RunTest(File zipfile, NeoLoadTest test, Optional<String> nlapi, Optional<String> nlapitoken, Optional<String> nlurl, Optional<String> uploadurl, Optional<String> nlzoneid, int size) throws ApiException, NeoLoadJgitExeption {
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

        ApiClient nlWebApiClient=new ApiClient();
        nlWebApiClient.setApiKey(nlapitoken.get());
        nlWebApiClient.setBasePath(uploadurl.get());

        RuntimeApi runtimeApi=new RuntimeApi(nlWebApiClient);
        Calendar cal = Calendar.getInstance();
        cal.setTime(Date.from(Instant.now()));

        if(zipfile.exists())
        {
            ProjectDefinition projectDefinition = runtimeApi.postUploadProject(zipfile);
            nlWebApiClient.setBasePath(nlapi.get());
            nlWebApiClient.setApiKey(nlapitoken.get());
            runtimeApi=new RuntimeApi(nlWebApiClient);

            RunTestDefinition runTestDefinition = runtimeApi.getTestsRun(KEPTN_EVENT_URL+"_"+keptnEventFinished.getProject()+"_"+keptnEventFinished.getService()+"_"+test.getScenario(), projectDefinition.getProjectId(), test.getScenario(), test.getDescription(),getAsCodeFiles(test.getProject()),null,null,null,null,nlzoneid.get(),nlzoneid.get()+":"+String.valueOf(size));
            NeoLoadWebTest neoLoadWebTest=new NeoLoadWebTest(runTestDefinition.getTestId(),nlurl.get() + "/#!trend/?scenario=" + test.getScenario() + "&limit=-1&project=" + projectDefinition.getProjectId(),nlurl.get() + "/#!result/" + runTestDefinition.getTestId() + "/overview");


            logger.info("Trending URL : " + neoLoadWebTest.getTrendingurl());
            logger.info("Testing result url : " + neoLoadWebTest.getTesturl());
            logger.info("Init of the Test..... ");
            NLWebTestStatus status = new NLWebTestStatus(nlWebApiClient.getBasePath(), nlapitoken.get(), neoLoadWebTest.getTestid(), logger);
            String teststatus = status.getFinalTestStatus();
            neoLoadWebTest.setTestStatus(status.getFinalTestStatus());
            if (teststatus.equalsIgnoreCase(TEST_STATUS_FAIL))
            {
                logger.info("Test has FAILED");
            } else
                logger.info("Test has finished with sucess");

            keptnEventFinished.setTeststatus(status.getFinalTestStatus());
            return neoLoadWebTest;


        }
        else
        {
            throw new NeoLoadJgitExeption("No zip file found");
        }


    }

    private void deleteGitFolder() throws IOException {
        Files.delete(gitfolder);
    }
    private void runNLScenario(NeoLoadTest test)
    {
        final NeoLoadKubernetesClient neoLoadKubernetesClient=new NeoLoadKubernetesClient(keptncontext);
        List<String> machinelist = null;
        try {
            if (test.checkProject(gitfolder)) {
                machinelist=test.getNameOfLGtoStart(gitfolder);
                logger.debug("This scenario "+test.getScenario()+" requires " + machinelist.size()+ " NeoLoad LoadGenerator");
                //----start machines----

                neoLoadKubernetesClient.deployController();

                machinelist.stream().forEach(machine->{
                    neoLoadKubernetesClient.deployLG(machine);
                });

                List<String> projectspath=test.getProject().stream().map(project -> project.getPath()).collect(Collectors.toList());
                String zipfilepath=createZipFile(projectspath, keptnEventFinished.getProject());

                NeoLoadWebTest loadWebTest=RunTest(new File(zipfilepath),test,neoLoadKubernetesClient.getNeoloadweb_apiurl(),Optional.ofNullable(neoLoadKubernetesClient.getNeoloadAPitoken()),neoLoadKubernetesClient.getNeoloadweb_url(),neoLoadKubernetesClient.getNeoloadweb_uploadurl(),neoLoadKubernetesClient.getNeoloadZoneid(),machinelist.size());
                keptnEventFinished.setTestid(loadWebTest.getTestid());
                keptnEventFinished.setNeoloadURL(loadWebTest.getTesturl());
                ///---
                NeoLoadEndEvent endEvent=new NeoLoadEndEvent(logger,eventid);
                endEvent.endevent(keptnEventFinished,extensions);
                //--send end event-------------

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
        } catch (ApiException e) {
            logger.error("RUnNLScenario , api exception",e);
        } finally {
            if(neoLoadKubernetesClient!=null)
            {
                //delete infra
                if(machinelist!=null) {
                    machinelist.stream().forEach(machine->{
                        neoLoadKubernetesClient.deleteLG(machine);
                    });
                }
                neoLoadKubernetesClient.deleteController();
            }
            try {
                deletetempfolder();
            } catch (IOException e) {
                logger.error("Unable to delete temp folder ",e);
            }
        }
    }

    public void runNeoLoadTest() throws NeoLoadJgitExeption, NeoLoadSerialException, IOException {
        gitfolder = getNeoLoadTestFolder();
        List<NeoLoadTestStep> neoLoadTestStepList=getNeoLoadTest();


        //---for each test start test -----
        neoLoadTestStepList.stream().forEach(step->{
            runNLScenario(step.getStep());

        });

        deleteGitFolder();
    }
    private List<NeoLoadTestStep> getNeoLoadTest() throws NeoLoadJgitExeption, NeoLoadSerialException {
        if(gitfolder!=null)
        {
            logger.debug("getNeoLoadTest - loading yaml file "+gitfolder.toAbsolutePath()+"/"+NEOLOAD_FOLDER+"/"+NEOLOAD_CONFIG_FILE);
            Yaml yaml = new Yaml(new Constructor(NeoLoadDataModel.class));
            InputStream inputStream = this.getClass()
                    .getClassLoader()
                    .getResourceAsStream(gitfolder.toAbsolutePath()+"/"+NEOLOAD_FOLDER+"/"+NEOLOAD_CONFIG_FILE);
            NeoLoadDataModel neoLoadDataModel = yaml.load(inputStream);
            if(neoLoadDataModel==null) {
                logger.debug("getNeoLoadTest - no able to deserialize the yaml file");
                throw new NeoLoadSerialException("Unable to deserialize YAML file ");
            }
            if(neoLoadDataModel.getSteps().size()<0)
            {
                logger.debug("getNeoLoadTest - there is no testing steps");
                throw new NeoLoadJgitExeption("There is no testing steps define ");
            }


            return neoLoadDataModel.getSteps();

        }
        else throw  new NeoLoadJgitExeption("no git folder define ");

            }
    private Path getNeoLoadTestFolder()
    {
        try {
            Path localPath = Files.createTempDirectory("Gitfolder_" + keptncontext);
            logger.debug("getNeoLoadTestFolder - local directory created "+localPath);

            if(keptnEventFinished!=null) {
                logger.debug("getNeoLoadTestFolder - start clonning repo  "+getGitHubFolder());

                Git result = Git.cloneRepository()
                        .setURI(getGitHubFolder())
                        .setDirectory(localPath.toFile())
                        .setBranch(keptnEventFinished.getStage())
                        .call();

                logger.debug("getNeoLoadTestFolder - end clonning repo  "+getGitHubFolder());

                if(!hasNeoLoadFolder(localPath)) {
                    logger.debug("No " + NEOLOAD_FOLDER + "is not available in +" + localPath.toAbsolutePath());

                    throw new NeoLoadJgitExeption("No " + NEOLOAD_FOLDER + "is not available in +" + localPath.toAbsolutePath());
                }

                 if(!hasNeoLoadKeptn(localPath)) {
                     logger.debug("No " + localPath.toAbsolutePath() + "/" + NEOLOAD_FOLDER + "/" + NEOLOAD_CONFIG_FILE + "is not available in +" + localPath.toAbsolutePath());

                     throw new NeoLoadJgitExeption("No " + localPath.toAbsolutePath() + "/" + NEOLOAD_FOLDER + "/" + NEOLOAD_CONFIG_FILE + "is not available in +" + localPath.toAbsolutePath());
                 }

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
        File NeoLoadPath= new File(path.toAbsolutePath()+"/"+ NEOLOAD_FOLDER);
        if(NeoLoadPath.exists())
            return true;
        else
            return false;
    }

    private boolean hasNeoLoadKeptn(Path path)
    {
        File neoloadConfigFile = new File(path.toAbsolutePath()+"/"+NEOLOAD_FOLDER+"/"+NEOLOAD_CONFIG_FILE);
        if(neoloadConfigFile.exists())
            return true;
        else
            return false;
    }

    private String getGitHubFolder() throws IOException, NeoLoadJgitExeption {
        String gitFolder;

        if (keptnEventFinished !=null) {
            gitFolder = GITHUB + keptnEventFinished.getGithuborg() + "/" + keptnEventFinished.getService() +".git";
            return gitFolder;

        } else {
            throw new NeoLoadJgitExeption("No data in Event");

        }

    }

}
