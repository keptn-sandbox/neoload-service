#!/usr/bin/env bash
[  -z "$1" ] && NAMESPACE="keptn" || NAMESPACE=$1

kubectl delete secret neoload -n "$NAMESPACE" --ignore-not-found


# Create dynatrace-service
NL_SERVICE_RELEASE="0.7.0"

echo "Delete neoload-service $NL_SERVICE_RELEASE"
wget https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/neoloadexecutor/service_withdynatrace.yaml -O service.yaml
wget https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/neoloadexecutor/distributor.yaml -O distributor.yaml
wget https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/neoloadexecutor/role.yaml -O role.yaml

#replace the namespace in the deployment
[  -z "$1" ] && NAMESPACE="keptn" || NAMESPACE="$1"

sed -i "s/NAMESPACE_TO_REPLACE/$NAMESPACE/" service.yaml
sed -i "s/NAMESPACE_TO_REPLACE/$NAMESPACE/" distributor.yaml
sed -i "s/NAMESPACE_TO_REPLACE/$NAMESPACE/" role.yaml
# to update the link
kubectl delete -f service.yaml --ignore-not-found
kubectl delete -f distributor.yaml --ignore-not-found
kubectl delete -f role.yaml --ignore-not-found