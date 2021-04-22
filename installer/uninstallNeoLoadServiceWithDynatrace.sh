#!/usr/bin/env bash
################################
####  arguments :
####  -n  ->. namespace of keptn (otpional by default keptn)
####################################"
while getopts n: option
do
 case "${option}"
 in
 n) NAMESPACE=${OPTARG};;
 esac
done

if [  -z "$NAMESPACE" ]; then
   NAMESPACE="keptn"
fi


# Create dynatrace-service
NL_SERVICE_RELEASE="0.8.0"

echo "Delete neoload-service $NL_SERVICE_RELEASE"
wget https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/neoloadexecutor/distributor.yaml -O distributor.yaml
wget https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/neoloadexecutor/role.yaml -O role.yaml
wget https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/neoloadexecutor/service_withdynatrace.yaml -O service.yaml


#replace the namespace in the deployment
sed -i "s/NAMESPACE_TO_REPLACE/$NAMESPACE/" service.yaml
sed -i "s/NAMESPACE_TO_REPLACE/$NAMESPACE/" distributor.yaml
sed -i "s/NAMESPACE_TO_REPLACE/$NAMESPACE/" role.yaml
# to update the link
kubectl delete -f service.yaml --ignore-not-found
kubectl delete -f distributor.yaml --ignore-not-found
kubectl delete -f role.yaml --ignore-not-found
kubectl delete secret neoload -n "$NAMESPACE" --ignore-not-found
kubectl delete configmap neoload-config -n "$NAMESPACE" --ignore-not-found