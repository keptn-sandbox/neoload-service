package com.keptn.neotys.testexecutor.log;




import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.LOGING_LEVEL_KEY;
import static com.keptn.neotys.testexecutor.conf.NeoLoadConfiguration.NEOLOAD_SOURCE;

public  class KeptnLogger  {

    private String logginglevel;
    private String kepncontext;
    private Logger loger;
    private ConsoleHandler handler;
    private static final String INFO="INFO";
    private static final String WARN="WARN";
    private static final String ERROR="ERROR";
    private static final String TRACE="TRACE";
    private static final String DEBUG="DEBUG";

    String messageFormat="{\n" +
            "  \"keptnContext\": \"%s\",\n" +
            "  \"logLevel\": \"%s\",\n" +
            "  \"keptnService\": \""+NEOLOAD_SOURCE+"\",\n" +
            "  \"message\": \"%s\"\n" +
            "}";

    public String getKepncontext() {
        return kepncontext;
    }

    public void setKepncontext(String kepncontext) {
        this.kepncontext = kepncontext;
        handler.setFormatter(new KeptnLoggerFormater(kepncontext));
    }

    public KeptnLogger(String className) {
        handler = new ConsoleHandler();
        LogManager.getLogManager().reset();
        loger = LogManager.getLogManager().getLogger(className);
        loger.setLevel(getLevel());

        handler.setLevel(getLevel());
    }



    private Level getLevel()
    {
        String level=System.getenv(LOGING_LEVEL_KEY);
        switch (level.toUpperCase())
        {
            case INFO:
                return Level.INFO;
            case ERROR:
                return Level.SEVERE;
            case DEBUG:
                return Level.FINE;
            case TRACE:
                return Level.FINEST;
            case WARN:
                return Level.WARNING;
            default: return Level.SEVERE;
        }
    }

    public void error(String s) {
      loger.log(Level.SEVERE,s);
    }
    public void error(String s,Throwable throwable) {
        loger.log(Level.SEVERE,s,throwable);
    }

    public void info(String s) {
        loger.log(Level.INFO,s);
    }

    public void warn(String s) {
        loger.log(Level.WARNING,s);
    }




    public void debug(String s) {
        loger.log(Level.FINE,s);
    }


    public void trace(String s) {
        loger.log(Level.FINEST,s);
    }



}
