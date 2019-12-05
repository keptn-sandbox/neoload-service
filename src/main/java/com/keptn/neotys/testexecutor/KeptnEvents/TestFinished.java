package com.keptn.neotys.testexecutor.KeptnEvents;

public class TestFinished extends KeptnEventFinished {


    public TestFinished( String project, String teststrategy, String deploymentstrategy, String stage, String service ) {
        super( project, teststrategy, deploymentstrategy, stage, service );
    }
}
