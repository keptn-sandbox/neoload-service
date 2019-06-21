package com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel;


import com.keptn.neotys.testexecutor.NeoLoadFolder.NeoloadInfrastructureModel.NeoLoadInfrastructureFile;
import com.keptn.neotys.testexecutor.NeoLoadFolder.NeoloadInfrastructureModel.NeoloadInfra;
import com.keptn.neotys.testexecutor.exception.NeoLoadJgitExeption;
import com.keptn.neotys.testexecutor.exception.NeoLoadSerialException;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.*;

public class NeoLoadTest {
    List<Project> project;
    String description;
    String scenario;
    Optional<Infrastructure> infrastructure;
    Optional<String> global_infrasctructure;

    public NeoLoadTest(List<Project> project, String description, String scenario, Optional<Infrastructure> infrastructure, Optional<String> global_infrasctructure) {
        this.project = project;
        this.description = description;
        this.scenario = scenario;
        this.infrastructure = infrastructure;
        this.global_infrasctructure = global_infrasctructure;
    }

    public Optional<Infrastructure> getInfrastructure() {
        return infrastructure;
    }

    public void setInfrastructure(Optional<Infrastructure> infrastructure) {
        this.infrastructure = infrastructure;
    }

    public Optional<String> getGlobal_infrasctructure() {
        return global_infrasctructure;
    }

    public void setGlobal_infrasctructure(Optional<String> global_infrasctructure) {
        this.global_infrasctructure = global_infrasctructure;
    }

    public List<Project> getProject() {
        return project;
    }

    public void setProject(List<Project> project){
        this.project = project;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }


    public boolean globalInfastructurefileExists(Path gitrepo)
    {
       return fileExists(gitrepo,global_infrasctructure);
    }


    public boolean isInfastructurefile(Path gitrepo)
    {
        return isANeoLoadInfastructureFile(gitrepo,global_infrasctructure);
    }



    public boolean checkProject(Path gitfolder) throws NeoLoadJgitExeption, NeoLoadSerialException {
        if(project.size()<=0)
            throw new NeoLoadJgitExeption("you need to refer to at least one project  ");
        else
        {
            List<String> error=new ArrayList<>();
            project.stream().forEach(project1 ->
            {
                if(!project1.isANeoLoadProjectFile(gitfolder))
                    error.add("This project path needs to have nlp or yaml extension "+project1.getPath());
                if(!project1.projectExists(gitfolder))
                    error.add("This project file doesn't not exists " +project1.getPath());
            });
            if(error.size()>0)
                throw new NeoLoadJgitExeption(error.stream().collect(Collectors.joining("\n")));

        }

        if(scenario==null)
            throw new NeoLoadJgitExeption("Scenario cannot be null ");


        if(global_infrasctructure.isPresent())
        {
            if(globalInfastructurefileExists(gitfolder))
                throw new NeoLoadJgitExeption("global instastructure file doesn't exists in "+ global_infrasctructure.get());

            if(!isInfastructurefile(gitfolder))
                throw new NeoLoadJgitExeption("global instastructure file not a yaml file "+ global_infrasctructure.get());


            List<NeoloadInfra> loadInfraFromGlobalFile=getNeoLoadInfraFromGlobalFile(gitfolder);
            if(loadInfraFromGlobalFile.size()<0)
                throw new NeoLoadJgitExeption("There is no Infrastructure define in " + global_infrasctructure.get());

            List<String> error = new ArrayList<>();
            loadInfraFromGlobalFile.stream().parallel().forEach(infra->{
                if(infra.getZones().size()<=0)
                    error.add("There is no zones define in " + infra.getName());
                else
                {
                    infra.getZones().stream().parallel().forEach(zone->{
                        if(zone.getMachines().size()<=0)
                            error.add("There is no machines define in the zone "+ infra.getName());
                    });
                }
            });
            if(error.size()>0)
                throw new NeoLoadJgitExeption(error.stream().collect(Collectors.joining("\n")));

            return true;
        }
        else
        {
            //---infra manage directly on population level
            if(!infrastructure.isPresent())
                throw new NeoLoadJgitExeption("infrastructure needs to be define if there is no global infrastructure file");

            if(infrastructure.get().getLocal_LG().size()<=0)
                throw new NeoLoadJgitExeption("You need to define at least one LoadGenerator");

            if(infrastructure.get().populations.size()<=0)
                throw new NeoLoadJgitExeption("You ned to configure at least one population");

            List<String> error = new ArrayList();
            infrastructure.get().populations.stream().forEach(population -> {
                if(population.lgs.size()<=0)
                    error.add("You need to define one Load Generator at least on population :"+ population.getName());

             });
            if (error.size()>0)
                throw new NeoLoadJgitExeption(error.stream().collect(Collectors.joining("\n")));

            return true;
        }

    }

    public List<String> getNameOfLGtoStart(Path gitFolder) throws NeoLoadSerialException {
        List<String> machinenameList=new ArrayList<>();
        if(global_infrasctructure.isPresent())
        {
            List<NeoloadInfra> loadInfraFromGlobalFile=getNeoLoadInfraFromGlobalFile(gitFolder);
            loadInfraFromGlobalFile.stream().filter( infras-> infras.getType().equalsIgnoreCase(ON_PREM_ZONE)).forEach(infra->{

                infra.getZones().stream().parallel().forEach(zone->{
                    machinenameList.addAll(zone.getMachines());
                });
            });
        }
        else
        {
            if(infrastructure.isPresent())
            {
                machinenameList.addAll(infrastructure.get().Local_LG.stream().map(lg ->
                {
                    return lg.getName();
                }).collect(Collectors.toList()));
            }
        }

        return machinenameList;
    }

    private boolean fileExists(Path gitrepo,Optional<String> file)
    {
        if(file.isPresent())
        {
            File neoloadprojectFile = new File(gitrepo.toAbsolutePath()+"/"+file.get());
            if(neoloadprojectFile.exists())
                return true;
            else
                return false;
        }
        else
            return false;
    }

    private boolean isANeoLoadInfastructureFile(Path pathinGit, Optional<String> file)
    {
        if(file.isPresent())
        {
            File neoloadprojectFile = new File(pathinGit.toAbsolutePath()+"/"+file.get());
            Path full=neoloadprojectFile.toPath();


        if(full.endsWith(YAML_EXTENSION)||full.endsWith(YML_EXTENSION))
            return true;
        else
            return false;
        }
        else
            return false;
    }

    public List<NeoloadInfra> getNeoLoadInfraFromGlobalFile(Path git) throws NeoLoadSerialException {
        if(global_infrasctructure.isPresent()) {
            Yaml yaml = new Yaml(new Constructor(NeoLoadInfrastructureFile.class));
            InputStream inputStream = this.getClass()
                    .getClassLoader()
                    .getResourceAsStream(git.toAbsolutePath() + "/" + NEOLOAD_FOLDER + "/" + this.global_infrasctructure.get());
            NeoLoadInfrastructureFile neoLoadInfrastructureFile = yaml.load(inputStream);

            if(neoLoadInfrastructureFile != null)
            {
                return neoLoadInfrastructureFile.getInfrastructures();
            }
            else
            {
                throw new NeoLoadSerialException("Unable to deserialize global infranstructure file  "+ global_infrasctructure.get());
            }
        }
        else
            return null;
    }

}
