Application exposing a simple REST API to be used for demo's.

## Deploy on OpenShift

```
oc login --token=$TOKEN --server=$API_SERVER

mvn clean package -Dquarkus.kubernetes.deploy=true -Dquarkus.openshift.expose=true
```
