#!/bin/bash

PROJECT_DIR=$(cd `dirname $0` && pwd)

kubectl --context kind-kind delete -f ${PROJECT_DIR}/controller/src/main/resources/gitwatch-crd.yml || true
kubectl --context kind-kind apply -f ${PROJECT_DIR}/controller/src/main/resources/gitwatch-crd.yml
