package com.keptn.neotys.testexecutor.NeoLoadFolder.NeoloadInfrastructureModel;

import java.util.List;

public class NeoloadInfra {
    String name;
    String type;
    List<Zone> zones;

    public NeoloadInfra(String name, String type, List<Zone> zones) {
        this.name = name;
        this.type = type;
        this.zones = zones;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }
}
