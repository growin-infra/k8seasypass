# K8S EasyPass Helm Chart (Helm 3)
A Helm chart that deploys K8S EasyPass to Kubernetes

![Version: 0.0.1](/src/main/resources/static/img/version.png) ![AppVersion: latest](/src/main/resources/static/img/appversion.png)


## Introduction

K8SEasyPass is a service for convenient website management.


## Prerequisites

- Kubernetes v1.18+
- Helm 3.+

## Kubernetes Prerequisites

- Dynamic Provisioning
- MetalLB
- Ingress Controller (If you need Ingress service)


## Installation

**Important:**
Required <span style="color:red">**ca.crt**</span> file in local path. The <span style="color:red">**ca.crt**</span> file can be downloaded from registry[harbor].

```console
$ helm repo add --ca-file ca.crt k8seasypass https://harbor.registry.svc.cluster.local/chartrepo/library
$ helm repo update
$ helm install [service name] k8seasypass/k8seasypass
```

Install after changing settings.
When you want to change the setting with Ingress.

```console
$ helm install [service name] \
  --set ingress.enabled=true \
  --set service.type=ClusterIP \
  k8seasypass/k8seasypass
```

Fetch the chart and install it after changing the settings.

```console
$ helm fetch k8seasypass/k8seasypass
$ cd k8seasypass
$ helm install [service name] -f values.yaml ./
```


## Configuration

The following table lists the configurable parameters of the K8S EasyPass chart and their default values.

| Parameter                        | Description                                                            | Default                     |
| ---------------------------------| -----------------------------------------------------------------------| ----------------------------|
| `replicaCount`                   | Number of replicas deployed                                            | `1`                         |
| `imagePullSecrets.name`          | Image Pull Secret                                                      | `imgpullsecret`             |
| `serviceAccount.name`            | ServiceAccount name                                                    | `k8seasypass`               |
| `service.type`                   | Choose one of NodePort, LoadBalancer, and ClusterIP                    | `LoadBalancer`              |
| `service.port`                   | External IP                                                            | `80`                        |
| `ingress.enabled`                | Select Use Ingress Service, true or false                              | `false`                     |
| `ingress.hosts.host`             | Ingress Service host name                                              | `k8seasypass.growin.co.kr`  |
| `ingress.apiVersion`             | Set API version for ingress                                            | `networking.k8s.io/v1`      |
| `persistence.enabled`            | Select Use PVC, true or false                                          | `true`                      |
| `persistence.name`               | PVC name                                                               | `k8seasypass-pvc`           |
| `persistence.storageClass`       | Dynamic Provisioning StorageClass name                                 | `nfs-storage`               |
| `persistence.existingClaim`      | Existing PVC name                                                      | `k8seasypass-pvc`           |
| `persistence.uninstalled`        | Select delete pvc when helm is deleted, true or false                  | `true`                      |


## Uninstalling the chart

To uninstall/delete the deployment:

```console
$ helm list
NAME       	REVISION	UPDATED                                	STATUS  	CHART            	APP VERSION
k8seasypass	1       	2021-06-19 13:34:33.171828951 +0900 KST	deployed	k8seasypass-0.1.1	latest
$ helm delete k8seasypass
```
