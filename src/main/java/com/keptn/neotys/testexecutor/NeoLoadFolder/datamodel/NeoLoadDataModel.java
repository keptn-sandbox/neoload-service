package com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel;


import java.util.ArrayList;
import java.util.List;

//  :
//       - step :
//          - test :
//             - repository : path
//             - project :
//                 - path  : absolute path to the project folder
//                 - path :
//             - description : Load test A
//             - scenario : load_test
//             - global_infrasctructure : dede/§dede.yaml
//             - constant_variables:
//                    - name = host
//                      value = catalogueèservice
//             - infrastructure:
//                 - local_LG :
//                     - name : lg1
//                     - name : lg2
//                 - populations :
//                     - population :
//                          - name : dede
//                            - lgs :
//                                - name : lg1
public class NeoLoadDataModel {
	List<NeoLoadTestStep> steps;

	public NeoLoadDataModel() {
		steps = new ArrayList<>();
	}

	public NeoLoadDataModel(List<NeoLoadTestStep> steps) {
		this.steps = steps;
	}

	public List<NeoLoadTestStep> getSteps() {
		return steps;
	}

	public void setSteps(List<NeoLoadTestStep> steps) {
		this.steps = steps;
	}

	@Override
	public String toString() {
		return "NeoLoadDataModel{" +
				"steps=" + steps +
				'}';
	}
}
