package com.keptn.neotys.testexecutor.KeptnEvents;

import io.vertx.core.json.JsonObject;

public class TestFinished extends KeptnEventFinished {


    public TestFinished(String project, JsonObject teststrategy, JsonObject deploymentstrategy, String stage, String service ) {
        super( project, teststrategy, deploymentstrategy, stage, service );
    }
}
