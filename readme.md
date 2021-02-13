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
- Docker
- Kubernetes
- Java
- Springboot2
- RestApi/Swagger/OpenApi
- Mysql

## Technical overview

- Microservices archetype using a mysql database.
- Kubernetes' secrets through dockerfile and springboot application.properties

## Compile and deploy

Compile all and deploy to kubernetes.

    ./build.sh

Compile & deploy only:
- **one** microservice:
  
        ./build.sh user

- **some** microservices:

        ./build.sh user swagger

## Check kubernetes deployments, services & pods

```shell
kubectl get all
kubectl describe springboot-user-xxxxxx
kubectl logs springboot-user-xxxxxx
```

### Connect to mysql service shell

```shell
kubectl exec -it springboot-mysql-5fdd4c8d5b-lq6nw -- bash
kubectl run -it --rm --image=mysql:5.6 --restart=Never mysql-client -- mysql -h springboot-mysql -ppassword
```
