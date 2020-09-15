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
				"  - teststrategy: performance\n" +
				"    script:\n" +
				"      repository: https://github.com/keptn-example/cart.git\n" +
				"      project:\n" +
				"      - path: /test/cart_basic.yaml\n" +
				"      - path: /test/load_template/load_template.nlp\n" +
				"    description: CartLoad\n" +
				"    properties:\n" +
				"      scenario: CartLoad\n" +
				"      constant_variables:\n" +
				"      - name: server_host\n" +
				"        value: carts.sockshop-dev.svc\n" +
				"    infrastructure:\n" +
				"      managedbyKeptn: false\n" +
				"      numberOfMachine: 4 \n" +
				"      zoneId : rest\n" +
				"    ";

		InputStream targetStream = new ByteArrayInputStream(yaml.getBytes());



		NeoLoadDataModel neoLoadDataModel = new Yaml().loadAs(targetStream, NeoLoadDataModel.class);
	}

}
