#!/bin/bash
################################
####  arguments :
####  -n  ->. namespace of keptn (otpional by default keptn)
####  -c ->. controller immage (optional by default will use the official images of neotys)
####  -l -> load generator image (optional by default will use the offical image of neotys)
####  -u -> login user to connect to the Source control containing the NeoLoad test scripts
####  -p -> password to connect to the source control containing the NeoLoad test scripts
####################################"
while getopts n:c:l:u:p: option
do
 case "${option}"
 in
 n) NAMESPACE=${OPTARG};;
 c) CONTROLLER=${OPTARG};;
 l) LG=${OPTARG};;
 u) USER=${OPTARG};;
 p) PASS=${OPTARG};;
 esac
done

if [  -z "$NAMESPACE" ]; then
   NAMESPACE="keptn"
fi

NL_WEB_HOST=$(cat creds_nl.json | jq -r '.nlwebWebHost')
NL_API_HOST=$(cat creds_nl.json | jq -r '.nlwebAPIHost')
NL_API_TOKEN=$(cat creds_nl.json | jq -r '.nlwebAPIToken')
NL_WEB_ZONEID=$(cat creds_nl.json | jq -r '.nlwebZoneId')
NL_UPLOAD_HOST=$(cat creds_nl.json | jq -r '.nlwebUploadHost')
# Create secrets to be used by neoload-service
echo "Create secrets to be used by neoload-service"
kubectl -n "$NAMESPACE" create secret generic neoload --from-literal="NL_WEB_HOST=$NL_WEB_HOST" --from-literal="NL_API_HOST=$NL_API_HOST"  --from-literal="NL_API_TOKEN=$NL_API_TOKEN" --from-literal="NL_WEB_ZONEID=$NL_WEB_ZONEID" --from-literal="NL_UPLOAD_HOST=$NL_UPLOAD_HOST"

if  [ ! -z "$USER" ]; then
   if  [ ! -z "$PASS" ]; then
   echo "Adding the SCM user to the neoload seccret"
   kubectl -n "$NAMESPACE" create secret generic neoload --from-literal="SECRET_SCM_USER=$USER" --from-literal="SECRET_SCM_PASSWORD=$PASS" --from-literal="NL_API_TOKEN=$NL_API_TOKEN"
  fi
else
  kubectl -n "$NAMESPACE" create secret generic neoload --from-literal="NL_API_TOKEN=$NL_API_TOKEN"
fi
# Create dynatrace-service
NL_SERVICE_RELEASE="0.8.0"

echo "Deploying neoload-service $NL_SERVICE_RELEASE"
# download the deployment files
wget https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/neoloadexecutor/distributor.yaml -O distributor.yaml
wget https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/neoloadexecutor/role.yaml -O role.yaml
wget https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/neoloadexecutor/service_withdynatrace.yaml -O service.yaml

if [ -z "$CONTROLLER" ]; then
      if [ -z "$LG" ]; then
        kubectl -n "$NAMESPACE" create configmap neoload-config --from-literal="NL_WEB_HOST=$NL_WEB_HOST" --from-literal="NL_API_HOST=$NL_API_HOST"  --from-literal="NL_WEB_ZONEID=$NL_WEB_ZONEID" --from-literal="NL_UPLOAD_HOST=$NL_UPLOAD_HOST" --from-literal="KEPTN_NAMESPACE=$NAMESPACE"
      else
         echo "Controller image needs to be defined if the Loadgenerator image is specified"
         exit 0
      fi
else
  if [ -z "$LG" ]; then
     echo "You need to precize a Loadgenerator image if you are specifyin a custom Controller image"
     exit 0
  else
     echo "Creating neoload configmap with cusotm neoload images"
     kubectl -n "$NAMESPACE" create configmap neoload-config --from-literal="NL_WEB_HOST=$NL_WEB_HOST" --from-literal="NL_API_HOST=$NL_API_HOST"  --from-literal="NL_WEB_ZONEID=$NL_WEB_ZONEID" --from-literal="NL_UPLOAD_HOST=$NL_UPLOAD_HOST" --from-literal="NL_DOCKER_CTL_IMAGE=$CONTROLLER" --from-literal="NL_DOCKER_LG_IMAGE=$LG" --from-literal="KEPTN_NAMESPACE=$NAMESPACE"
  fi
fi

#replace the namespace in the deployment


sed -i "s/NAMESPACE_TO_REPLACE/$NAMESPACE/" service.yaml
sed -i "s/NAMESPACE_TO_REPLACE/$NAMESPACE/" role.yaml
sed -i "s/NAMESPACE_TO_REPLACE/$NAMESPACE/" distributor.yaml
kubectl apply -f service.yaml
kubectl apply -f distributor.yaml
kubectl apply -f role.yaml