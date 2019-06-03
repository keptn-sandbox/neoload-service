package com.keptn.neotys.testexecutor.log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.LOGING_LEVEL_KEY;
import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.NEOLOAD_SOURCE;

public class NeoLoadServiceLoggingHandler  extends Handler {





    @Override
    public void publish(LogRecord record) {


        StringBuilder sb = new StringBuilder();
        sb.append(record.getMillis())
                .append(" - ")
                .append(record.getSourceClassName())
                .append("#")
                .append(record.getSourceMethodName())
                .append(" - ")
                .append(record.getMessage());
        System.out.println(sb.toString());

    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
