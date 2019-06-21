package com.keptn.neotys.testexecutor.log;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.NEOLOAD_SOURCE;

public class KeptnLoggerFormater extends Formatter {

    String keptnContext;

    String messageFormat="{\n" +
            "  \"keptnContext\": \"%s\",\n" +
            "  \"logLevel\": \"%s\",\n" +
            "  \"keptnService\": \""+NEOLOAD_SOURCE+"\",\n" +
            "  \"message\": \"%s\"\n" +
            "}";

    public KeptnLoggerFormater(String keptnContext) {
        this.keptnContext = keptnContext;
    }

    @Override
    public String format(LogRecord record) {
        return String.format(messageFormat,keptnContext,record.getLevel(), record.getMillis() +" :" + record.getSourceClassName() +" - " +record.getSourceMethodName() + " - "+  record.getMessage());
    }
}
