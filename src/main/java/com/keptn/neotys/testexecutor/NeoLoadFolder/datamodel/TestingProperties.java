package com.keptn.neotys.testexecutor.NeoLoadFolder.datamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestingProperties {
    List<Constants> constant_variables;


    String scenario;


    public TestingProperties() {
        this.constant_variables = new ArrayList<>();


    }

    public TestingProperties(List<Constants> constant_variables, String scenario) {
        this.constant_variables = constant_variables;
        this.scenario = scenario;
    }

    public List<Constants> getConstant_variables() {
        return constant_variables;
    }

    public void setConstant_variables(List<Constants> constant_variables) {
        this.constant_variables = constant_variables;
    }

    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    @Override
    public String toString() {
        return "TestingProperties{" +
                " scenario='" + scenario + '\'' +
                ", constant_variables=" + "[" +constant_variables.stream().map(constants -> {return  constants.toString();}).collect(Collectors.joining(",")) +"]"+
                '}';
    }
}
