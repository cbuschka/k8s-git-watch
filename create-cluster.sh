#!/bin/bash

cat - > /tmp/kind-cluster-config.yml <<EOB
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
- role: control-plane
#  extraPortMappings:
#  - containerPort: 80
#    hostPort: 80
#    listenAddress: "0.0.0.0" # Optional, defaults to "0.0.0.0"
#    protocol: udp # Optional, defaults to tcp
- role: worker
EOB


kind create cluster --name kind --config /tmp/kind-cluster-config.yml
#kind get clusters
#kubectl cluster-info --context kind-kind
#kind delete cluster --name kind-kind
#kind load docker-image my-custom-image:unique-tag
#ind load image-archive /my-image-archive.tar

