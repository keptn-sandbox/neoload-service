package com.keptn.neotys.testexecutor.log;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.NEOLOAD_SOURCE;
import static com.keptn.neotys.testexecutor.log.KeptnLogger.*;


public class KeptnLoggerFormater extends Formatter {

    String keptnContext;
    private static final String SEVERE="SEVERE";
    private static final String FINE="FINE";

    private static final String FINEST="FINEST";
    private static final String WARNING="WARNING";

    String messageFormat="{" +
            "  \"keptnContext\": \"%s\"," +
            "  \"logLevel\": \"%s\"," +
            "  \"keptnService\": \""+NEOLOAD_SOURCE+"\"," +
            "  \"message\": \"%s\"" +
            "}\n";

    public KeptnLoggerFormater(String keptnContext) {
        this.keptnContext = keptnContext;
    }

    @Override
    public String format(LogRecord record) {
        String level;
        switch (record.getLevel().getName())
        {
            case INFO:
                level=INFO;
                break;
            case SEVERE:
                level=ERROR;
                break;
            case FINE:
                level= DEBUG;
                break;
            case FINEST:
                level= TRACE;
                break;
            case WARNING:
                level= WARNING;
                break;
            default: level= ERROR;
                break;
        }
        return String.format(messageFormat,keptnContext,level, record.getMillis() +" :" + record.getSourceClassName() +" - " +record.getSourceMethodName() + " - "+  record.getMessage());
    }
}
