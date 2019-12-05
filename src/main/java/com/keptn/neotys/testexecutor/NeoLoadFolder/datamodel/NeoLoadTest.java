package com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel;


import com.keptn.neotys.testexecutor.NeoLoadFolder.NeoloadInfrastructureModel.NeoLoadInfrastructureFile;
import com.keptn.neotys.testexecutor.NeoLoadFolder.NeoloadInfrastructureModel.NeoloadInfra;
import com.keptn.neotys.testexecutor.exception.NeoLoadJgitExeption;
import com.keptn.neotys.testexecutor.exception.NeoLoadSerialException;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.*;

public class NeoLoadTest {
	String repository;
	List<Project> project;
	String description;
	String scenario;
	Infrastructure infrastructure;
	String global_infrasctructure;
	List<Constants> constant_variables;

	public NeoLoadTest() {
		project = new ArrayList<>();
		constant_variables = new ArrayList<>();
	}

	public NeoLoadTest(String repository, List<Project> project, String description, String scenario, @Nullable Infrastructure infrastructure, @Nullable String global_infrasctructure, @Nullable List<Constants> constant_variables) {
		this.repository = repository;
		this.project = project;
		this.description = description;
		this.scenario = scenario;
		this.infrastructure = infrastructure;
		this.global_infrasctructure = global_infrasctructure;
		this.constant_variables = constant_variables;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

//	public Optional<List<Constants>> getConstant_variables() {
//		return Optional.ofNullable(constant_variables).flatMap(list -> list.isEmpty() ? Optional.empty() : Optional.of(list));
//	}

	public List<Constants> getConstant_variables() {
		return constant_variables;
	}


	public void setConstant_variables(List<Constants> constant_variables) {
		this.constant_variables = constant_variables;
	}

//	public Optional<Infrastructure> getInfrastructure() {
//		return Optional.ofNullable( infrastructure);
//	}

	public Infrastructure getInfrastructure() {
		return infrastructure;
	}
	public void setInfrastructure(Infrastructure infrastructure) {
		this.infrastructure = infrastructure;
	}

	public Optional<String> getGlobal_infrasctructure() {
		return Optional.ofNullable(global_infrasctructure);
	}

	public void setGlobal_infrasctructure(String global_infrasctructure) {
		this.global_infrasctructure = global_infrasctructure;
	}

	public List<Project> getProject() {
		return project;
	}

	public void setProject(List<Project> project) {
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


	public boolean globalInfastructurefileExists(Path gitrepo) {
		return fileExists(gitrepo, getGlobal_infrasctructure());
	}


	public boolean isInfastructurefile(Path gitrepo) {
		return isANeoLoadInfastructureFile(gitrepo, getGlobal_infrasctructure());
	}


	public boolean checkProject(Path gitfolder) throws NeoLoadJgitExeption, NeoLoadSerialException, IOException {
		if (project.size() <= 0)
			throw new NeoLoadJgitExeption("you need to refer to at least one project  ");
		else {
			List<String> error = new ArrayList<>();
			project.stream().forEach(project1 ->
			{
				if (!project1.isANeoLoadProjectFile(gitfolder))
					error.add("This project path needs to have nlp or yaml extension " + project1.getPath());
				if (!project1.projectExists(gitfolder))
					error.add("This project file doesn't not exists " + project1.getPath());
			});
			if (error.size() > 0)
				throw new NeoLoadJgitExeption(error.stream().collect(Collectors.joining("\n")));

		}

		if (scenario == null)
			throw new NeoLoadJgitExeption("Scenario cannot be null ");


		final Optional<String> optGlobalInfrasctructure = getGlobal_infrasctructure();
		if (optGlobalInfrasctructure.isPresent()) {
			if (globalInfastructurefileExists(gitfolder))
				throw new NeoLoadJgitExeption("global instastructure file doesn't exists in " + optGlobalInfrasctructure.get());

			if (!isInfastructurefile(gitfolder))
				throw new NeoLoadJgitExeption("global instastructure file not a yaml file " + optGlobalInfrasctructure.get());


			List<NeoloadInfra> loadInfraFromGlobalFile = getNeoLoadInfraFromGlobalFile(gitfolder);
			if (loadInfraFromGlobalFile.size() < 0)
				throw new NeoLoadJgitExeption("There is no Infrastructure define in " + optGlobalInfrasctructure.get());

			List<String> error = new ArrayList<>();
			loadInfraFromGlobalFile.stream().parallel().forEach(infra -> {
				if (infra.getZones().size() <= 0)
					error.add("There is no zones define in " + infra.getName());
				else {
					infra.getZones().stream().parallel().forEach(zone -> {
						if (zone.getMachines().size() <= 0)
							error.add("There is no machines define in the zone " + infra.getName());
					});
				}
			});
			if (error.size() > 0)
				throw new NeoLoadJgitExeption(error.stream().collect(Collectors.joining("\n")));

			return true;
		} else {
			//---infra manage directly on population level
			final Optional<Infrastructure> optInfrastructure = Optional.ofNullable(infrastructure);
			if (!optInfrastructure.isPresent())
				throw new NeoLoadJgitExeption("infrastructure needs to be define if there is no global infrastructure file");

			if (optInfrastructure.get().getLocal_LG().size() <= 0)
				throw new NeoLoadJgitExeption("You need to define at least one LoadGenerator");

			if (optInfrastructure.get().populations.size() <= 0)
				throw new NeoLoadJgitExeption("You ned to configure at least one population");

			List<String> error = new ArrayList();
			optInfrastructure.get().populations.stream().forEach(population -> {
				if (population.lgs.size() <= 0)
					error.add("You need to define one Load Generator at least on population :" + population.getName());

			});
			if (error.size() > 0)
				throw new NeoLoadJgitExeption(error.stream().collect(Collectors.joining("\n")));

			return true;
		}

	}

	public List<String> getNameOfLGtoStart(Path gitFolder) throws NeoLoadSerialException, IOException {
		List<String> machinenameList = new ArrayList<>();
		if (getGlobal_infrasctructure().isPresent()) {
			List<NeoloadInfra> loadInfraFromGlobalFile = getNeoLoadInfraFromGlobalFile(gitFolder);
			loadInfraFromGlobalFile.stream().filter(infras -> infras.getType().equalsIgnoreCase(ON_PREM_ZONE)).forEach(infra -> {

				infra.getZones().stream().parallel().forEach(zone -> {
					machinenameList.addAll(zone.getMachines());
				});
			});
		} else {
			final Optional<Infrastructure> optionalInfrastructure = Optional.ofNullable(this.infrastructure);
			if (optionalInfrastructure.isPresent()) {
				machinenameList.addAll(optionalInfrastructure.get().local_LG.stream().map(lg ->
				{
					return lg.getName();
				}).collect(Collectors.toList()));
			}
		}

		return machinenameList;
	}

	private boolean fileExists(Path gitrepo, Optional<String> file) {
		if (file.isPresent()) {
			File neoloadprojectFile = new File(gitrepo.toAbsolutePath() + "/" + file.get());
			if (neoloadprojectFile.exists())
				return true;
			else
				return false;
		} else
			return false;
	}

    private boolean isANeoLoadInfastructureFile(Path pathinGit, Optional<String> file)
    {
        if(file.isPresent())
        {
            File neoloadprojectFile = new File(pathinGit.toAbsolutePath()+"/"+file.get());
            Path full=neoloadprojectFile.toPath();


			if (full.endsWith(YAML_EXTENSION) || full.endsWith(YML_EXTENSION))
				return true;
			else
				return false;
		} else
			return false;
	}

	public List<NeoloadInfra> getNeoLoadInfraFromGlobalFile(Path git) throws NeoLoadSerialException, IOException {
		if (getGlobal_infrasctructure().isPresent()) {
			final String yamlFile = Files.readAllLines(Paths.get(git.toAbsolutePath().toString(), NEOLOAD_FOLDER, this.getGlobal_infrasctructure().get())).stream().collect(Collectors.joining("\n"));
			NeoLoadInfrastructureFile neoLoadInfrastructureFile	= new Yaml().loadAs(yamlFile, NeoLoadInfrastructureFile.class);

			if (neoLoadInfrastructureFile != null) {
				return neoLoadInfrastructureFile.getInfrastructures();
			} else {
				throw new NeoLoadSerialException("Unable to deserialize global infranstructure file  " + getGlobal_infrasctructure().get());
			}
		} else
			return null;
	}

	@Override
	public String toString() {
		return "NeoLoadTest{" +
				"repository='" + repository + '\'' +
				", project=" + project +
				", description='" + description + '\'' +
				", scenario='" + scenario + '\'' +
				", infrastructure=" + infrastructure +
				", global_infrasctructure='" + global_infrasctructure + '\'' +
				", constant_variables=" + constant_variables +
				'}';
	}
}
