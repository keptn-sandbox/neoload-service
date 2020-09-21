package com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel;

import com.keptn.neotys.testexecutor.NeoLoadFolder.NeoloadInfrastructureModel.NeoLoadInfrastructureFile;
import com.keptn.neotys.testexecutor.NeoLoadFolder.NeoloadInfrastructureModel.NeoloadInfra;
import com.keptn.neotys.testexecutor.exception.NeoLoadJgitExeption;
import com.keptn.neotys.testexecutor.exception.NeoLoadSerialException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.*;
import static com.keptn.neotys.testexecutor.kubernetes.DockerConstants.LG;

public class NeoLoadTestStep {
	String description;
	String teststrategy;
	TestingProperties properties;
	TestingScript script;
	Infrastructure infrastructure;
	List<HashMap<String,Double>>  validations;
	TestStage teststages;


	public NeoLoadTestStep()
	{
		validations=new ArrayList<>();
	}
	public NeoLoadTestStep(String description, String teststrategy, TestingProperties properties, TestingScript script, Infrastructure infrastructure, List<HashMap<String, Double>> validations, TestStage teststages) {
		this.description = description;
		this.teststrategy = teststrategy;
		this.properties = properties;
		this.script = script;
		this.infrastructure = infrastructure;
		this.validations = validations;
		this.teststages = teststages;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTeststrategy() {
		return teststrategy;
	}

	public void setTeststrategy(String teststrategy) {
		this.teststrategy = teststrategy;
	}

	public TestingProperties getProperties() {
		return properties;
	}

	public void setProperties(TestingProperties properties) {
		this.properties = properties;
	}

	public TestingScript getScript() {
		return script;
	}

	public void setScript(TestingScript script) {
		this.script = script;
	}

	public Infrastructure getInfrastructure() {
		return infrastructure;
	}

	public void setInfrastructure(Infrastructure infrastructure) {
		this.infrastructure = infrastructure;
	}

	public List<HashMap<String,Double>>  getValidations() {
		return validations;
	}

	public void setValidations(List<HashMap<String,Double>>  validations) {
		this.validations = validations;
	}

	public TestStage getTeststages() {
		return teststages;
	}

	public void setTeststages(TestStage teststages) {
		this.teststages = teststages;
	}


	public boolean checkProject(Path gitfolder) throws NeoLoadJgitExeption, NeoLoadSerialException, IOException {
		if (script.getProject().size() <= 0)
			throw new NeoLoadJgitExeption("you need to refer to at least one project  ");
		else {
			List<String> error = new ArrayList<>();
			script.getProject().stream().forEach(project1 ->
			{
				if (!project1.isANeoLoadProjectFile(gitfolder))
					error.add("This project path needs to have nlp or yaml extension " + project1.getPath());
				if (!project1.projectExists(gitfolder))
					error.add("This project file doesn't not exists " + project1.getPath());
			});
			if (error.size() > 0)
				throw new NeoLoadJgitExeption(error.stream().collect(Collectors.joining("\n")));

		}

		if (properties.getScenario() == null)
			throw new NeoLoadJgitExeption("Scenario cannot be null ");




		//---infra
		final Optional<Infrastructure> optInfrastructure = Optional.ofNullable(infrastructure);
		if (!optInfrastructure.isPresent())
			throw new NeoLoadJgitExeption("infrastructure needs to be defined ");

		if (!(optInfrastructure.get().getNumberOfMachine().intValue() > 0))
			throw new NeoLoadJgitExeption("You need to define at least one LoadGenerator");

		if(optInfrastructure.isPresent())
		{
			if(optInfrastructure.get().isManagedbyKeptn())
			{
				if(optInfrastructure.get().getZoneId() ==null)
					throw new NeoLoadJgitExeption("If the load testing infrastructure is not manage by keptn, the Neoload zone id needs to be precised");
			}
		}

		return true;


	}

	public List<String> getNameOfLGtoStart() throws NeoLoadSerialException, IOException {
		List<String> machinenameList = new ArrayList<>();
		final Optional<Infrastructure> optionalInfrastructure = Optional.ofNullable(this.infrastructure);

			if(optionalInfrastructure.isPresent())
			{
				if(optionalInfrastructure.get().isManagedbyKeptn())
				{
					for (int i=0;i<optionalInfrastructure.get().getNumberOfMachine().intValue();i++)
					{
						machinenameList.add(properties.getScenario().toLowerCase()+"-"+String.valueOf(i));
					}

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

	/*public List<NeoloadInfra> getNeoLoadInfraFromGlobalFile(Path git) throws NeoLoadSerialException, IOException {
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
	}*/

	@Override
	public String toString() {
		return "NeoLoadTestStep{" +
				"description='" + description + '\'' +
				", teststrategy='" + teststrategy + '\'' +
				", properties=" + properties +
				", script=" + script +
				", infrastructure=" + infrastructure +
				", validations=" + validations +
				", teststages=" + teststages +
				'}';
	}
}
