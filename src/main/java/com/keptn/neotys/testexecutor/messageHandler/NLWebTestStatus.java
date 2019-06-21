package com.keptn.neotys.testexecutor.messageHandler;

import com.keptn.neotys.testexecutor.exception.NeoLoadJgitExeption;
import com.keptn.neotys.testexecutor.log.KeptnLogger;
import com.neotys.ascode.swagger.client.ApiClient;
import com.neotys.ascode.swagger.client.ApiException;
import com.neotys.ascode.swagger.client.api.ResultsApi;
import com.neotys.ascode.swagger.client.model.TestDefinition;

public class NLWebTestStatus {
    private ApiClient apiClient;
    private String apitoken;
    private String apiurl;
    private String testID;
    private static final String RUNNING="RUNNING";
    private static final String STARTING="STARTING";
    private static final String UNKNOWN="UNKNOWN";
    private static long MAXCHECKTIME=240000;
    KeptnLogger log;
    private boolean logingHasStarted=false;
    private String testStatus;

    public NLWebTestStatus(String url, String apitoken, String testID, KeptnLogger log) {
        this.apiClient = new ApiClient();
        this.apiurl=url;
        this.apitoken=apitoken;
        apiClient.setBasePath(apiurl);
        apiClient.setApiKey(this.apitoken);
        this.testID = testID;
        this.log=log;
    }


    public String getFinalTestStatus() {
        boolean testisrunning=true;
        ResultsApi resultsApi=new ResultsApi(apiClient);
        long timeoutExpiredMs = System.currentTimeMillis() + MAXCHECKTIME;
        try {
            Thread.sleep(2000);
            TestDefinition definition = resultsApi.getTest(testID);
            while(definition.getStatus()==null)
            {

                long waitMs = timeoutExpiredMs - System.currentTimeMillis();
                if (waitMs <= 0) {
                    // timeout expired
                   log.error("The test was not able to start after "+MAXCHECKTIME+" ms");
                    throw new NeoLoadJgitExeption("Error while starting the test ");

                }
                Thread.sleep(5000);
                definition = resultsApi.getTest(testID);

            }
            while(testisrunning)
            {
                definition=resultsApi.getTest(testID);
                if (!definition.getStatus().getValue().equalsIgnoreCase(RUNNING) && !definition.getStatus().getValue().equalsIgnoreCase(STARTING) && !definition.getStatus().getValue().equalsIgnoreCase(UNKNOWN))
                {
                    log.info("TEST " + definition.getName() + " is FINISHED");
                    testisrunning=false;

                } else
                {
                    if (!logingHasStarted)
                    {
                        log.info("..................................");
                        log.info("TEST " + definition.getName() + " has the following status : " + definition.getStatus().getValue());
                        testStatus = definition.getStatus().getValue();
                        logingHasStarted=true;
                    } else {
                        if (!testStatus.equalsIgnoreCase(definition.getStatus().getValue())) {
                            log.info("..................................");
                            log.info("TEST " + definition.getName() + " has the following status : " + definition.getStatus().getValue());
                        }
                    }
                }
                Thread.sleep(10000);

            }
            return definition.getQualityStatus().getValue();
        }catch (ApiException e)
        {
            log.error("API ERROR",e);
        }
        catch (Exception e)
        {
            log.error("ERROR :",e);
        }
        return null;
    }


}
