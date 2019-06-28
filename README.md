# Keptn NeoLoad Service

This service is designed to use NeoLoad for executing various Load testing tasks. 

To trigger a NeoLoad test, the service has subscriptions to event channels. In more details, the current implementation of the service listens to CloudEvents from type:
* `sh.keptn.events.deployment-finished`: When receiving this event, the service executes a test for a deployed application. This event would be replace by the start test event

## Secret for credentials
During the setup of NeoLaod, a secret is created that contains key-value pairs for the NeoLoad  URL, NeoLoad apiKey:
   * NL_WEB_HOST 
   * NL_API_HOST 
   * NL_UPLOAD_HOST
   * NL_API_TOKEN
   * NL_WEB_ZONEID 




## Install service <a id="install"></a>

1. To install the service, you need to run :
 * installer/defineNeoLoadWebCredentials.sh to configure the required parameters :
    1. NL_WEB_HOST : host of the web ui of NeoLoad web
    1. NL_API_HOST : host of the api of NeoLoad web
    1. NL_UPLOAD_HOST : host of upload api of NeoLoad Web
    1. NL_API_TOKEN: api token of your NeoLoad account
    1. NL_WEB_ZONEID : NeoLoad Web Zone id that would be used by Keptn


## The NeoLoad Service requires to have the following folders in the github service repository

1. "neoload" Folder containing all the neoload tests. The NeoLaod folder will have :

1. One folder for each NeoLoad project  (nlp or yaml file) containing the NeoLaod infrastructure files ( deployments of LG pods)

1. One testing file describing the test to execute named `keptn.neoload.engine.yaml`

```yaml
steps:
- step:
    stage: dev
    project:
    - path: /tests/neoload/catalogue_neoload.yaml
    description: BasicCheck
    scenario: BasicCheck
    constant_variables:
    - name: server_host
      value: catalog-service.orders-project-dev.svc
    - name: server_port
      value: 8080
    infrastructure:
      local_LG:
      - name: lg1
      populations:
      - name: BasicCheck
        lgs:
        - name: lg1
 ```

  All github repository will then need to have the following folders :
  ```
     tests
       neoload
         - keptn.neoload.engine.yml
           project A
             test.yml
             infrastructure
                infrastructure.yml
           project X
              project.nlp
              config.zip
              ....
           infrastructure
              default.yml
   ``` 

