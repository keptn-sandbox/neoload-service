package com.keptn.neotys.testexecutor.NeoLoadFolder.NeoloadInfrastructureModel;

import java.util.List;

public class NeoLoadInfrastructureFile {

    //infrastructures:
    //  - name: On-Prem
    //    type: ON_PREMISE_LOAD_GENERATOR
    //    zones:
    //    - name: Docker
    //      machines:
    //      - nl-lg.cicd.svc

    List<NeoloadInfra> infrastructures;

    public NeoLoadInfrastructureFile(List<NeoloadInfra> infrastructures) {
        this.infrastructures = infrastructures;
    }

    public List<NeoloadInfra> getInfrastructures() {
        return infrastructures;
    }

    public void setInfrastructures(List<NeoloadInfra> infrastructures) {
        this.infrastructures = infrastructures;
    }
}
