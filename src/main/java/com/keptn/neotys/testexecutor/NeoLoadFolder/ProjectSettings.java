package com.keptn.neotys.testexecutor.NeoLoadFolder;

import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

public class ProjectSettings {
    private static final String DYNATRACE_ENABLED="dynatrace_enabled";
    private static final String DYNATRACE_URL="dynatrace_url";
    private static final String DYNATRACE_TOKEN="dynatrace_token";
    String dynatrace_url ;
    String dynatrace_token ;
    Boolean dynatrace_enabled;

    public ProjectSettings(Boolean dynatrace_enabled, String dynatrace_url, String dynatrace_token) {
        this.dynatrace_enabled = dynatrace_enabled;
        this.dynatrace_url = "https://"+dynatrace_url+"/";
        this.dynatrace_token = dynatrace_token;
    }

    public Boolean getDynatrace_enabled() {
        return dynatrace_enabled;
    }

    public void setDynatrace_enabled(Boolean dynatrace_enabled) {
        this.dynatrace_enabled = dynatrace_enabled;
    }

    public String getDynatrace_url() {
        return dynatrace_url;
    }

    public void setDynatrace_url(String dynatrace_url) {
        this.dynatrace_url = dynatrace_url;
    }

    public String getDynatrace_token() {
        return dynatrace_token;
    }

    public void setDynatrace_token(String dynatrace_token) {
        this.dynatrace_token = dynatrace_token;
    }

    public Representer getRepresenter() {

        return  new Representer(){
            protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag
                    customTag) {
                NodeTuple defaultNode = super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);

                switch(property.getName())
                {
                    case DYNATRACE_ENABLED:
                        return new NodeTuple(representData("dynatrace.enabled"), defaultNode.getValueNode());
                    case DYNATRACE_TOKEN:
                        return new NodeTuple(representData("dynatrace.url"), defaultNode.getValueNode());
                    case DYNATRACE_URL:
                        return new NodeTuple(representData("dynatrace.token"), defaultNode.getValueNode());
                    default:
                        return defaultNode;
                }

            }
        };


    }

}
