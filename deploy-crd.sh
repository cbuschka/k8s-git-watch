#!/bin/bash

kubectl --context kind-kind delete -f gitwatch-crd.yml || true
kubectl --context kind-kind apply -f gitwatch-crd.yml
