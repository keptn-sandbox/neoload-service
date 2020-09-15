package com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel;


import java.util.ArrayList;
import java.util.List;

//  :
//workloads:
//  - teststrategy: performance
//    description: PerformanceTest
//    script:
//		- repository : path
//
//      - project :
//			- path :
//			- path :
//    properties:
//		- constant_varaibles:
//      	- name : $SERVERURL
//			  value :
//        	- name : server_port
//			  value:
//      - scenario :
//    infrastructure:
//		- loadgeneraor:
//			 - managedbyKeptn: false
//             - numberOfMachine : 2
//    validations:
//      - acceptederrorrate: 1.0
//    teststages:
//      - ramp_up: 60s
//        cool_down: 60s
//
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
//                 - loadgeneraor :
//                     - name : lg1
//                     - name : lg2
//                 - populations :
//                     - population :
//                          - name : dede
//                            - lgs :
//                                - name : lg1
public class NeoLoadDataModel {
	List<NeoLoadTestStep> workloads;

	public NeoLoadDataModel() {
		workloads = new ArrayList<>();

	}

	public NeoLoadDataModel(List<NeoLoadTestStep> workloads) {
		this.workloads = workloads;
	}

	public List<NeoLoadTestStep> getWorkloads() {
		return workloads;
	}

	public void setWorkloads(List<NeoLoadTestStep> workloads) {
		this.workloads = workloads;
	}

	@Override
	public String toString() {
		return "NeoLoadDataModel{" +
				"workloads=" + workloads.toString() +
				'}';
	}
}
