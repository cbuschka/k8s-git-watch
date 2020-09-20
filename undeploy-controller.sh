#!/bin/bash

kubectl --context kind-kind delete -f gitwatch-controller-deployment.yml || true
