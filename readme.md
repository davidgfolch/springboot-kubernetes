## Pre-requisites

### Start containers
You need Docker, and a kubernetes cluster running (f.ex. minikube), see:

```shell
./start.sh
```

If you have problems with `minikube start`:
```shell
minikube --driver=docker
minikube delete
sudo rm -rf ~/.minikube
```
- Certificate problems arise randomly (applying the above clean recreates certificates)

### Create kubernetes secrets

```shell
./kubectlSetup.sh
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

- spring-boot-maven-plugin optimizes jar size & startup time.
- Kubernetes mysql service/pod (deployment.mysql.yaml)
- Kubernetes fan-out ingress config for backend services (deployment-ingress.yaml)
- Kubernetes' secrets through dockerfile and springboot application.properties

## Backend tech overview

- encrypted pass in db user table

## Compile deploy & undeploy (build.sh)

Compile all and deploy to kubernetes.
> NOTE: you must `docker login` & change your $docker-io-user in `./build.sh`


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

#### How it works (build.sh)

For all, one or several microservices, it:
- Kubernetes undeploy
- compiles optimized
- docker tag & push to [my repo](https://hub.docker.com/repository/docker/davidgfolch/kubernetes-springboot-db)
- Kubernetes deploy


#### Known issues & warnings

- Maven: ignoring WARNING for **spring-boot-maven-plugin** otherwise `spring-boot:build-image` doesn't work

### TODO

- need of user/../messages.properties duplication of translations for unit testing UserControllerTest, see how to solve this (maven vs resourceBundle)
- swagger service auto-discover endpoints
- security improvements
  - https
  - spring-security
- front-end?
- maven kubernetes plugin  https://maven.fabric8.io/ ??

## Kubernetes guide

### Check kubernetes deployments, services & pods

```shell
kubectl get all
kubectl describe springboot-user-xxxxxx
kubectl logs -f springboot-user-xxxxxx
```


### Dev utilities

#### Mysql container ssh

    kubectl get pods --namespace=default -o=jsonpath="{.items[*].metadata.name}" -l app=springboot-kubernetes,tier=db
    kubectl exec -it springboot-mysql-7f6cfcf6b6-vlcxx -- mysql -h 127.0.0.1 -P 3306 -uroot -p'kube1234' -e 'use springboot-db; select verify_token from user where user_name="mrBean";'

#### Check business logic with ingress

You need to enable ingress plugin for minikube:

    minikube addons enable ingress
    kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.10.0/deploy/static/provider/cloud/deploy.yaml
    kubectl apply -f deployment-ingress.yaml
    
Build all if not done previously:

    ./build.sh

The ip is the `minikube ip` (same as springboot-ingress ip `kubectl get ingress springboot-ingress`).

    curl "$(minikube ip)"/user/actuator | jq .
    curl "$(minikube ip)"/user/actuator/health | jq .
    # curl 192.168.49.2/swagger/v2/api-docs/ | jq .

##### User business logic (manual test)

Signup MrBean:

    curl -X PUT -H "Content-Type: application/json" "$(minikube ip)"/user/signup \
      -d '{"userName":"mrBean", "email":"misterbean@gmail.com", "name":"mister", "surname":"bean", "pass":"MrBeanIsNumber1!"}' | jq .

Get token from database:

    kubectl exec -it springboot-mysql-7f6cfcf6b6-vlcxx -- mysql -h 127.0.0.1 -P 3306 -uroot -p'kube1234' -e 'use springboot-db; select verify_token from user where user_name="mrBean";'

Verify user (replace the XXXX with token in next command):

    curl -X POST -H "Content-Type: application/json" "$(minikube ip)"/user/verify -d '{"userName":"mrBean", "token":"XXXX"}' | jq .

Login:

    curl -X POST -H "Content-Type: application/json" "$(minikube ip)"/user/login \
      -d '{"userName":"mrBean", "pass":"MrBeanIsNumber1!"}' | jq .


#### Check the endpoints without an ingress

Uncomment port & nodePort in `user/deployment.yaml` and:

    kubectl apply -f user/deployment.yaml
    kubectl describe svc springboot-user
    curl -H "Content-Type: application/json" $(minikube ip):32000/user

**TODO NOT WORKING PORT-FORWARD!!!**

Create a port forwarding to access service end-points when there is no ingress:

    kubectl port-forward svc/springboot-user 8080:8080

Check the end-points works

    curl -i localhost:8080/user/recover
    curl localhost:8080/swagger/v2/api-docs/ | jq .
    curl localhost:8080/user/actuator | jq .




### Connect to mysql service shell

```shell
kubectl exec -it springboot-mysql-5fdd4c8d5b-lq6nw -- bash
kubectl run -it --rm --image=mysql:5.6 --restart=Never mysql-client -- mysql -h springboot-mysql -ppassword
```
