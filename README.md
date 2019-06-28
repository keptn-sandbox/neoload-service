# Keptn NeoLoad Service

This service is designed to use NeoLoad for executing various Load testing tasks. 

To trigger a NeoLoad test, the service has subscriptions to event channels. In more details, the current implementation of the service listens to CloudEvents from type:
* `sh.keptn.events.deployment-finished`: When receiving this event, the service executes a test for a deployed application. This event would be replace by the start test event

## Secret for credentials
During the setup of NeoLaod, a secret is created that contains key-value pairs for the NeoLoad  URL, NeoLoad apiKey:
* neoloadWebURL="$NEOLOAD_URL" 
* neoloadAPIKEY="$NEOLOAD_APIKEY" 



## Install service <a id="install"></a>

1. To install the service, specify values for the following parameters:
    * REGISTRY_URL - URL of the container registry
    * GITHUB_USER_EMAIL - Email of GitHub user
    * GITHUB_ORGANIZATION - GitHub organization used by keptn
    * GITHUB_PERSONAL_ACCESS_TOKEN - Personal access token from GitHub user



## The NeoLoad Service requires to have the following folders in the github service repository

1. Neoload Folder containing all the neoload tests. The NeoLaod folder will have :

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
       NeoLoad
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

