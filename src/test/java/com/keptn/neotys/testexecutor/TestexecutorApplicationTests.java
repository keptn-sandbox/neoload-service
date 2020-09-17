package com.keptn.neotys.testexecutor;

import com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel.NeoLoadDataModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yaml.snakeyaml.Yaml;

import java.io.*;


public class TestexecutorApplicationTests {

	@Test
	public void contextLoads() throws IOException {

		String yaml ="workloads:\n" +
				"- teststrategy: performance\n" +
				"  script:\n" +
				"    repository: https://github.com/keptn-orders/keptn-onboarding.git\n" +
				"    branch: neoload\n" +
				"    project:\n" +
				"    - path: /frontend/neoload/frontend_basic.yaml\n" +
				"    - path: /frontend/neoload/production/frontend_scenario.yaml\n" +
				"    - path: /frontend/neoload/load_template/load_template.nlp\n" +
				"  properties:\n" +
				"    scenario: FrontLoad\n" +
				"    constant_variables:\n" +
				"    - name: server_host\n" +
				"      value: frontend.keptnorder-production.svc.cluster.local\n" +
				"    - name: server_port\n" +
				"      value: 80\n" +
				"  description: FrontLoad\n" +
				"  infrastructure:\n" +
				"    managedbyKeptn: false\n" +
				"    numberOfMachine: 1\n" +
				"    zoneId: cz104";

		InputStream targetStream = new ByteArrayInputStream(yaml.getBytes());



		NeoLoadDataModel neoLoadDataModel = new Yaml().loadAs(targetStream, NeoLoadDataModel.class);
	}

}
