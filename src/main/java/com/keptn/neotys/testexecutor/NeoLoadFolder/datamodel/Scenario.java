package com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel;

import java.util.ArrayList;
import java.util.List;

public class Scenario {
    List<Object> populations;
    Apm apm_configuration;
    List<String> excluded_urls;
    List<Object> rendezvous_policies;
    Object monitoring;
    String name;

    public Scenario()
    {
        populations=new ArrayList<>();
        excluded_urls=new ArrayList<>();
        rendezvous_policies=new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Object> getPopulations() {
        return populations;
    }

    public void setPopulations(List<Object> populations) {
        this.populations = populations;
    }

    public Apm getApm_configuration() {
        return apm_configuration;
    }

    public void setApm_configuration(Apm apm_configuration) {
        this.apm_configuration = apm_configuration;
    }

    public List<String> getExcluded_urls() {
        return excluded_urls;
    }

    public void setExcluded_urls(List<String> excluded_urls) {
        this.excluded_urls = excluded_urls;
    }

    public List<Object> getRendezvous_policies() {
        return rendezvous_policies;
    }

    public void setRendezvous_policies(List<Object> rendezvous_policies) {
        this.rendezvous_policies = rendezvous_policies;
    }

    public Object getMonitoring() {
        return monitoring;
    }

    public void setMonitoring(Object monitoring) {
        this.monitoring = monitoring;
    }
}
