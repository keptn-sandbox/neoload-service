#!/usr/bin/env bash
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

kubectl delete secret neoload -n "$NAMESPACE" --ignore-not-found

# Create dynatrace-service
NL_SERVICE_RELEASE="0.7.0"

echo "Delete neoload-service $NL_SERVICE_RELEASE"
wget https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/neoloadexecutor/distributor.yaml -O distributor.yaml
wget https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/neoloadexecutor/role.yaml -O role.yaml

if [ -z "$CONTROLLER" ]
  then
      if [ -z "$LG" ]
      then
           wget https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/neoloadexecutor/service.yaml -O service.yaml
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
     wget https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/config/neoloadexecutor/service_customimage.yaml -O service.yaml
     sed -i "s/NLCTL_IMAGE_TOREPALCE/$CONTROLLER/" service.yaml
     sed -i "s/NLLG_IMAGE_TOREPALCE/$LG/" service.yaml
  fi
fi
#replace the namespace in the deployment


sed -i "s/NAMESPACE_TO_REPLACE/$NAMESPACE/" service.yaml
sed -i "s/NAMESPACE_TO_REPLACE/$NAMESPACE/" distributor.yaml
sed -i "s/NAMESPACE_TO_REPLACE/$NAMESPACE/" role.yaml
# to update the link
kubectl delete -f service.yaml --ignore-not-found
kubectl delete -f distributor.yaml --ignore-not-found
kubectl delete -f role.yaml --ignore-not-found