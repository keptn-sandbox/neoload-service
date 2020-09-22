#!/bin/bash
################################
####  arguments :
####  -n  ->. namespace of keptn (otpional by default keptn)
####  -c ->. controller immage (optional by default will use the official images of neotys)
####  -l -> load generator image (optional by default will use the offical image of neotys)
####################################"
while getopts n:c:l: option
do
 case "${option}"
 in
 n) NAMESPACE=${OPTARG};;
 c) CONTROLLER=${OPTARG};;
 l) LG=${OPTARG};;
 esac
done

if [  -z "$NAMESPACE" ]
 then
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


# Create dynatrace-service
NL_SERVICE_RELEASE="0.7.0"

echo "Deploying neoload-service $NL_SERVICE_RELEASE"
# download the deployment files
wget https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/neoloadexecutor/distributor.yaml -O distributor.yaml
wget https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/neoloadexecutor/role.yaml -O role.yaml

if [ -z "$CONTROLLER" ]
  then
      if [ -z "$LG" ]
      then
          wget https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/neoloadexecutor/service_withdynatrace.yaml -O service.yaml
      else
         echo "Controller image needs to be defined if the Loadgenerator image is specified"
         exit 0
      fi
else
  if [ -z "$LG" ]
  then
     echo "You need to precize a Loadgenerator image if you are specifyin a custom Controller image"
     exit 0
  else
     wget https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/neoloadexecutor/service_withdynatrace_customimage.yaml -O service.yaml
     sed -i "s/NLCTL_IMAGE_TOREPALCE/$CONTROLLER/" service.yaml
     sed -i "s/NLLG_IMAGE_TOREPALCE/$LG/" service.yaml
  fi
fi

#replace the namespace in the deployment


sed -i "s/NAMESPACE_TO_REPLACE/$NAMESPACE/" service.yaml
sed -i "s/NAMESPACE_TO_REPLACE/$NAMESPACE/" role.yaml
sed -i "s/NAMESPACE_TO_REPLACE/$NAMESPACE/" distributor.yaml
kubectl apply -f service.yaml
kubectl apply -f distributor.yaml
kubectl apply -f role.yaml