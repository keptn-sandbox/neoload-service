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
				"     repository: https://<surl>/neoload-perftest\n" +
				"     branch: master\n" +
				"     issecured: true\n"+
				"     project:\n" +
				"     - path: /tassitd/TAS_SM_CombinedFinal.nlp\n" +
				"  description: SitdTas\n" +
				"  properties:\n" +
				"    scenario: KeptnTest\n" +
				"  infrastructure:\n" +
				"    managedbyKeptn: false\n" +
				"    numberOfMachine: 2\n" +
				"    zoneId : 4KBFT\n";

		InputStream targetStream = new ByteArrayInputStream(yaml.getBytes());



		NeoLoadDataModel neoLoadDataModel = new Yaml().loadAs(targetStream, NeoLoadDataModel.class);
	}

}
