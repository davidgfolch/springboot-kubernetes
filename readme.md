## Pre-requisites

You need a kubernetes cluster running, f.ex. minikube:

    minikube start

## Create kubernetes secrets

    kubectl create secret generic springboot-mysql-secret --from-literal=mysql-root-password=kube1234 --from-literal=mysql-user=springboot-user --from-literal=mysql-password=kube1234
    kubectl create configmap springboot-mysql-db --from-literal="mysql-database=springboot-db"

## Compile and publish

Compile all and deploy to kubernetes.

    ./build.sh

To only one microservice:

    ./build.sh user swagger

To only some microservices:

    ./build.sh user

### Connect to mysql service shell

    kubectl run -it --rm --image=mysql:5.6 --restart=Never mysql-client -- mysql -h springboot-mysql -ppassword
    kubectl exec -it springboot-mysql-5fdd4c8d5b-lq6nw -- bash
