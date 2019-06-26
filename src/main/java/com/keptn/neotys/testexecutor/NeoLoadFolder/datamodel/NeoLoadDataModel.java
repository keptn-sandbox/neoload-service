package com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel;


import com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel.NeoLoadTest;
import com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel.NeoLoadTestStep;

import java.util.List;

//  :
//       - step :
//          - test :
//             - stage : dev
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
//                 - Local_LG :
//                     - name : lg1
//                     - name : lg2
//                 - populations :
//                     - population :
//                          - name : dede
//                            - lgs :
//                                - name : lg1
public class NeoLoadDataModel {
    List<NeoLoadTestStep> steps;

    public NeoLoadDataModel(List<NeoLoadTestStep> steps) {
        this.steps = steps;
    }

    public List<NeoLoadTestStep> getSteps() {
        return steps;
    }

    public void setSteps(List<NeoLoadTestStep> steps) {
        this.steps = steps;
    }
}
