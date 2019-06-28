package com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel;

import com.keptn.neotys.testexecutor.NeoLoadFolder.NeoloadInfrastructureModel.NeoloadInfra;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.*;

public class Project {
    String path;

	public Project() {
	}

	public Project(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isANeoLoadProjectFile(Path pathinGit)
    {
        File neoloadprojectFile = new File(pathinGit.toAbsolutePath()+"/"+path);
        Path full=neoloadprojectFile.toPath();
        if(full.endsWith(YAML_EXTENSION)||full.endsWith(YML_EXTENSION)||full.endsWith(NLP_EXTENSION))
            return true;
        else
            return false;
    }

    public boolean projectExists(Path pathinGit)
    {

        File neoloadprojectFile = new File(pathinGit.toAbsolutePath()+"/"+path);
        if(neoloadprojectFile.exists())
            return true;
        else
            return false;
    }

	@Override
	public String toString() {
		return "Project{" +
				"path='" + path + '\'' +
				'}';
	}
}
