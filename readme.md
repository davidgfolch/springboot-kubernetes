## Pre-requisites

### Start containers
You need Docker, and a kubernetes cluster running (f.ex. minikube), see:

```shell
./start.sh
```

### Create kubernetes secrets

```shell
kubectl create secret generic springboot-mysql-secret --from-literal=mysql-root-password=kube1234 --from-literal=mysql-user=springboot-user --from-literal=mysql-password=kube1234
kubectl create configmap springboot-mysql-db --from-literal="mysql-database=springboot-db"
```
## Tech stack

- Bash
- Maven
- Docker
- Kubernetes
- Java
- Springboot2
- RestApi/Swagger/OpenApi
- Mysql

## Technical overview

Microservices archetype using a mysql database.

- Kubernetes mysql service/pod (deployment.mysql.yaml)
- Kubernetes fan-out ingress config for backend services (deployment-ingress.yaml)
- Kubernetes' secrets through dockerfile and springboot application.properties

## Compile deploy & undeploy

Compile all and deploy to kubernetes.

    ./build.sh

Compile & deploy only:
- **one** microservice:
  
        ./build.sh user

- **some** microservices:

        ./build.sh user swagger

Just deploy/undeploy:

    ./build.sh justDeploy
    ./build.sh justDeploy user swagger
    ./build.sh justDelete
    ./build.sh justDelete user

## Kubernetes guide

### Check kubernetes deployments, services & pods

```shell
kubectl get all
kubectl describe springboot-user-xxxxxx
kubectl logs -f springboot-user-xxxxxx
```


### Dev utilities

#### Check the endpoint with ingress

The ip is the springboot-ingress ip (`kubectl get ingress springboot-ingress`)

    curl -i 192.168.49.2/user/recover && echo
    curl 192.168.49.2/user/actuator | jq .

#### Check the endpoints without an ingress

Create a port forwarding to access service end-points when there is no ingress:

    kubectl port-forward svc/springboot-user 8080:8080

Check the end-points works

    curl -i localhost:8080/user/recover
    curl localhost:8080/v3/api-docs/ | jq .
    curl localhost:8080/user/actuator | jq .




### Connect to mysql service shell

```shell
kubectl exec -it springboot-mysql-5fdd4c8d5b-lq6nw -- bash
kubectl run -it --rm --image=mysql:5.6 --restart=Never mysql-client -- mysql -h springboot-mysql -ppassword
```
