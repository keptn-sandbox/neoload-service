package com.keptn.neotys.testexecutor.conf;

public class NeoLoadConfiguration {
    public static int PORT = 8080;
    public static final int KUBETIMEOUT=60;
    public static String HEALTH_PATH="/health";
    public static String NEOLOAD_SOURCE = "neoload-service";
    public static String KEPTN_EVENT_HOST = "api-service.";
    public static String KEPTN_END_URL=".svc.cluster.local";
    public static String KEPTN_EVENT_URL = "event";
    public static int KEPTN_PORT=8080;
    public static int KEPTN_PORT_EVENT=80;
    public static String GITHUB="https://github.com/";
    public static String NEOLOAD_GITFOLDER="Gitfolder";
    public static String LOGING_LEVEL_KEY="logging-level";
    public static String NEOLOAD_CONFIG_FILE="workload.yaml";
    public static String NEOLOAD_FOLDER="tests/neoload";
    public static String TMP_NEOLOAD_FOLDER="/tempneoload";
    public static String HEADER_KEPTNCONTEXT="Ce-X-Shkeptncontext";
    public static String HEADER_datacontentype="Ce-X-Datacontenttype";
    public static String HEADER_triggeredid="triggeredid";
    public static String HEADER_shkeptnspecversion="Ce-X-Shkeptnspecversion";

    public static final String DEFAULT_BRANCH="master";
    public static final String YML_EXTENSION=".yml";
    public static final String YAML_EXTENSION=".yaml";
    public static final String NLP_EXTENSION=".nlp";
    public static final String ON_PREM_ZONE="ON_PREMISE_LOAD_GENERATOR";
    public static final String NEOLOAD_Product="neoload";
    public static final String SECRET_API_TOKEN="NL_API_TOKEN";
    public static final String SECRET_NL_WEB_HOST="NL_WEB_HOST";
    public static final String SECRET_NL_API_HOST="NL_API_HOST";
    public static final String SECRET_NL_ZONEID="NL_WEB_ZONEID";
    public static final String SECRET_NL_UPLOAD_HOST="NL_UPLOAD_HOST";
    public static final String SECRET_DT_API_TOKEN="DT_API_TOKEN";
    public static final String SECRET_DT_TENANT="DT_TENANT";
    public static final String SECRET_KEPTN_NAMESPACE="KEPTN_NAMESPACE";
    public static final String NEOLOAD_CONFIG_FILE_OPTION2=NEOLOAD_Product+"%2F"+NEOLOAD_CONFIG_FILE;
    public static final String SECRET_SCM_USER="SECRET_SCM_USER";
    public static final String SECRET_SCM_PASSWORD="SECRET_SCM_PASSWORD";
    public static final String SECRET_KEPTN_API_TOKEN="KP_API_TOKEN";

    public static final String SECRET_NL_CONTROLLER_DCK_IMMAGE="NL_DOCKER_CTL_IMAGE";
    public static final String SECRET_NL_LG_DCK_IMMAGE="NL_DOCKER_LG_IMAGE";

    public static final String NLWEB_PROTOCOL="https://";
    public static final String NLWEB_APIVERSION="/v1";

    public static final String TEST_STATUS_FAIL="FAILED";
    public static final String CONFIGURATIONAPI_HOST="configuration-service.";
    public static final int CONFIGURAITON_PORT=8080;
    public static final String CONFIGURATION_PROTOCOL="http://";
    public static final String CONFIGURATION_VERSION="v1";
    public static final String CONFIGURATION_PROJECT="project";
    public static final String CONFIGURATION_STAGE="stage";
    public static final String CONFIGURATION_RESSOURCE="resource";
    public static final String CONFIGURATION_SERVICE="service";

    public static final String CONTENT_TYPE="Content-Type";

    public static final String HEADER_ACCEPT="accept";
    public static final String HEADER_KEPTN_TOKEN="x-token";
    public static final String HEADER_APPLICATIONJSON="application/json";

    public static final String KEPTN_TAG_PROJECT="keptn_project:";
    public static final String KEPTN_TAG_STAGE="keptn_stage:";
    public static final String KEPTN_TAG_SERVICE="keptn_service:";

}