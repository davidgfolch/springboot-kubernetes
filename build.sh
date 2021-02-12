#!/bin/bash
if [ $# -eq 0 ]; then
  echo "Optional parameters: justDeploy"
fi

declare -a microservices=(
  user
  swagger
)

function buildImage() {
  set +x #dont log commands executed
  echo "- $1 -> Build mss docker image "
  echo "-------------------------------"
  set -x #log commands executed
  docker build -t $1:0.0.1-SNAPSHOT $1
  docker tag $1:0.0.1-SNAPSHOT davidgfolch/kubernetes-springboot-$1 &&
  #  docker tag kubikdata/$1:latest davidgfolch/kubernetes-springboot-$1 &&  with fabric8
  docker push davidgfolch/kubernetes-springboot-$1 ||
  exit
}

filterByModules=false
if [[ "$*" != *justDeploy* && "$*" != "" ]]; then
  for mss in "${microservices[@]}"; do
    for param in "$@"; do
      [[ $mss == "$param" ]] && echo "$mss present in params" && filterByModules=true
    done
  done
fi

set -x #log commands executed
if [[ $filterByModules == false ]]; then
  kubectl delete -f deployment-ingress.yaml
  kubectl delete -f deployment-mysql.yaml
fi

for mss in "${microservices[@]}"; do
  if [[ $filterByModules == false || $filterByModules == true && "$*" == *$mss* ]]; then
    kubectl delete -f $mss/deployment.yaml
  fi
done

if [[ "$*" != *justDeploy* ]]; then
    if [[ $filterByModules == false ]]; then
      set +x #dont log commands executed
      echo "# BUILDING ALL IMAGES "
      echo "######################"
      set -x #log commands executed
      mvn clean install
    fi
    for mss in "${microservices[@]}"; do
      set -x
      if [[ $filterByModules == false ]]; then
          buildImage $mss
        else
          set +x #dont log commands executed
          echo "# BUILDING ONLY IMAGES IN PARAMETERS "
          echo "#####################################"
          set -x #log commands executed
          for param in "$@"; do
            [[ $mss == "$param" ]] &&
              mvn clean spring-boot:build-image -pl $mss &&
#              mvn package fabric8:build -pl $mss -DskipTests &&
              buildImage $mss
          done
      fi
    done
fi

if [[ $filterByModules == false ]]; then
  kubectl apply -f deployment-ingress.yaml
  kubectl apply -f deployment-mysql.yaml
fi
for mss in "${microservices[@]}"; do
  if [[ $filterByModules == false || $filterByModules == true && "$*" == *$mss* ]]; then
    kubectl apply -f $mss/deployment.yaml
  fi
done

set +x #dont log commands executed
echo "Wait for pods to be created & available"
echo "Create a port forwarding to access user end-points: kubectl port-forward svc/springboot-user 8080:8080 &"
echo "Check the end-point works: curl localhost:8080/actuator | jq ."
echo "Check the end-point works: curl -i localhost:8080/user/recover"
echo "Check the end-point works: curl localhost:8080/v3/api-docs/ | jq ."
echo "Kill the port-forward process: ps -ef | grep port-forward"
echo "Check user-end point (where the ip is the springboot-ingress ip, see dashboard): curl -i 192.168.49.2/user/recover"
