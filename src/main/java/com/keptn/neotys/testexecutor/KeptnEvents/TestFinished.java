package com.keptn.neotys.testexecutor.KeptnEvents;

public class TestFinished extends KeptnEventFinished {


    public TestFinished(String githuborg, String project, String teststrategy, String deploymentstrategy, String stage, String service, String image, String tag) {
        super(githuborg, project, teststrategy, deploymentstrategy, stage, service, image, tag);
    }
}
