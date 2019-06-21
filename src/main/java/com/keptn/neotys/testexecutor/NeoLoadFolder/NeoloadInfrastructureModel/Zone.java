package com.keptn.neotys.testexecutor.NeoLoadFolder.NeoloadInfrastructureModel;

import java.util.List;

public class Zone {
    String name;
    List<String> machines;


    public Zone(String name, List<String> machines) {
        this.name = name;
        this.machines = machines;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getMachines() {
        return machines;
    }

    public void setMachines(List<String> machines) {
        this.machines = machines;
    }
}

