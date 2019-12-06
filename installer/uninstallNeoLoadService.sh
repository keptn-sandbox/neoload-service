#!/usr/bin/env bash
kubectl delete secret neoload -n keptn --ignore-not-found

# Create dynatrace-service
NL_SERVICE_RELEASE="0.6.0"

print_info "Delete neoload-service $NL_SERVICE_RELEASE"
# to update the link
kubectl delete -f https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/neoloadexecutor/service.yaml --ignore-not-found
kubectl delete -f https://raw.githubusercontent.com/keptn-contrib/neoload-service/$NL_SERVICE_RELEASE/config/neoloadexecutor/distributor.yaml --ignore-not-found
