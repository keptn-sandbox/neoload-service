#!/bin/bash

NL_WEB_HOST=$(cat creds_nl.json | jq -r '.nlwebWebHost')
NL_API_HOST=$(cat creds_nl.json | jq -r '.nlwebAPIHost')
NL_API_TOKEN=$(cat creds_nl.json | jq -r '.nlwebAPIToken')
NL_WEB_ZONEID=$(cat creds_nl.json | jq -r '.nlwebZoneId')
NL_UPLOAD_HOST=$(cat creds_nl.json | jq -r '.nlwebUploadHost')
# Create secrets to be used by neoload-service
echo "Create secrets to be used by neoload-service"
kubectl -n keptn create secret generic neoload --from-literal="NL_WEB_HOST=$NL_WEB_HOST" --from-literal="NL_API_HOST=$NL_API_HOST"  --from-literal="NL_API_TOKEN=$NL_API_TOKEN" --from-literal="NL_WEB_ZONEID=$NL_WEB_ZONEID" --from-literal="NL_UPLOAD_HOST=$NL_UPLOAD_HOST"


# Create dynatrace-service
NL_SERVICE_RELEASE="0.6.0"

echo "Deploying neoload-service $NL_SERVICE_RELEASE"
# to update the link
kubectl apply -f https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/neoloadexecutor/service.yaml
kubectl apply -f https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/neoloadexecutor/distributor.yaml
