package com.keptn.neotys.testexecutor.log;

import jdk.internal.instrumentation.Logger;

import java.util.logging.Level;

import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.NEOLOAD_SOURCE;

public abstract class KeptnLogger implements Logger {

    String logginglevel;

    String messageFormat="{\n" +
            "  \"keptnContext\": \"%s\",\n" +
            "  \"logLevel\": \"%s\",\n" +
            "  \"keptnService\": \""+NEOLOAD_SOURCE+"\",\n" +
            "  \"message\": \"%s\"\n" +
            "}";



    public void errorMessage(String keptncontext,String s) {
      this.error(String.format(messageFormat,keptncontext, "ERROR",s));
    }
    public void errorMessage(String keptncontext,String s,Throwable throwable) {
        this.error(String.format(messageFormat,keptncontext, "ERROR",s),throwable);
    }

    public void warnMessage(String keptncontext,String s) {
        this.error(String.format(messageFormat,keptncontext, "WARN",s));
    }


    public void infoMessage(String keptncontext,String s) {
        this.error(String.format(messageFormat,keptncontext, "INFO",s));
    }


    public void debugMessage(String keptncontext,String s) {
        this.error(String.format(messageFormat,keptncontext, "DEBUG",s));
    }


    public void traceMessage(String keptncontext,String s) {
        this.error(String.format(messageFormat,keptncontext, "TRACE",s));
    }



}
