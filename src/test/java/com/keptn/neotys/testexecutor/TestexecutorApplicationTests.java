package com.keptn.neotys.testexecutor;

import com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel.NeoLoadDataModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.yaml.snakeyaml.Yaml;

import java.io.*;


public class TestexecutorApplicationTests {

	@Test
	public void contextLoads() throws IOException {

		String yaml ="steps:\n" +
				"- step:\n" +
				"    repository: https://github.com/keptn-example/cart.git\n" +
				"    project:\n" +
				"    - path: /test/cart_basic.yaml\n" +
				"    - path: /test/load_template/load_template.nlp\n" +
				"    description: CartLoad\n" +
				"    scenario: CartLoad\n" +
				"    constant_variables:\n" +
				"    - name: server_host\n" +
				"      value: carts.sockshop-dev.svc\n" +
				"    infrastructure:\n" +
				"      local_LG:\n" +
				"      - name: lg1\n" +
				"      populations :\n" +
				"      - name: CartLoad\n" +
				"        lgs:\n" +
				"        - name: lg1";

		InputStream targetStream = new ByteArrayInputStream(yaml.getBytes());



		NeoLoadDataModel neoLoadDataModel = new Yaml().loadAs(targetStream, NeoLoadDataModel.class);
	}

}
