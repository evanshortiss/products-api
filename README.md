Application exposing a simple REST API to be used for demo's.

## Deploy on OpenShift

```
oc login --token=$TOKEN --server=$API_SERVER

mvn clean package -Dquarkus.kubernetes.deploy=true -Dquarkus.openshift.expose=true
```

## Enforce 3scale Proxy Token Security

Set the `THREESCALE_SECRET_HEADER_VALUE` environment variable to the secret
value you configured in the 3scale API Manager/Admin portal.
