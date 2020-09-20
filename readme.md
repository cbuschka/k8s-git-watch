# k8s controller written in java for watching git repos

### just an experiment
curl -Lo ./kind https://kind.sigs.k8s.io/dl/v0.9.0/kind-linux-amd64
chmod +x ./kind
mv ./kind /some-dir-in-your-PATH/kind
./kind cluster create --name kind
kind get clusters
kubectl cluster-info --context kind-kind
kind delete cluster --name kind-kind
kind load docker-image my-custom-image:unique-tag
ind load image-archive /my-image-archive.tar

kind create cluster --config kind-example-config.yaml
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
- role: control-plane
  extraPortMappings:
  - containerPort: 80
    hostPort: 80
    listenAddress: "0.0.0.0" # Optional, defaults to "0.0.0.0"
    protocol: udp # Optional, defaults to tcp