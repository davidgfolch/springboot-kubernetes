#!/bin/bash

debug=false
if [[ "$*" == *debug* ]]; then
  debug=true
fi
filterByModules=false
declare -a microservices=(
  user
  swagger
)

function logCommands {
  if $debug; then
    if $1; then
        set -x #log commands executed
      else
        set +x #dont log commands executed
    fi
  fi
}

function println {
  logCommands false
  echo "$1"
  logCommands true
}

function buildImage() {
  println "- $1 -> Build mss docker image "
  println "-------------------------------"
  docker build -t $1:0.0.1-SNAPSHOT $1
  docker tag $1:0.0.1-SNAPSHOT davidgfolch/kubernetes-springboot-$1 &&
  #  docker tag kubikdata/$1:latest davidgfolch/kubernetes-springboot-$1 &&  with fabric8
  docker push davidgfolch/kubernetes-springboot-$1 ||
  exit
}

if [ $# -eq 0 ]; then
  println "Optional parameters: debug justDeploy justDelete"
fi

logCommands false
if [[ "$*" != *justDeploy* && "$*" != "" ]]; then
  for mss in "${microservices[@]}"; do
    for param in "$@"; do
      [[ $mss == "$param" ]] && println "$mss present in params" && filterByModules=true
    done
  done
fi

logCommands true
if [[ $filterByModules == false ]]; then
  kubectl delete -f deployment-ingress.yaml
  kubectl delete -f deployment-mysql.yaml
  if [[ "$*" != *justDelete* ]]; then
    kubectl apply -f deployment-ingress.yaml
    kubectl apply -f deployment-mysql.yaml
  fi
fi


for mss in "${microservices[@]}"; do
  if [[ $filterByModules == false || $filterByModules == true && "$*" == *$mss* ]]; then
    kubectl delete -f $mss/deployment.yaml
  fi
done

if [[ "$*" == *justDelete* ]]; then
  exit
fi

if [[ "$*" != *justDeploy* && "$*" != *justDelete* ]]; then
    if [[ $filterByModules == false ]]; then
      println "- BUILDING ALL IMAGES "
      println "----------------------"
      mvn clean install -Dlogging-level-test=off
    else
      println "- BUILDING ONLY IMAGES IN PARAMETERS "
      println "-------------------------------------"
    fi
    for mss in "${microservices[@]}"; do
      if [[ $filterByModules == false ]]; then
          buildImage $mss
        else
          for param in "$@"; do
            [[ $mss == "$param" ]] &&
              mvn clean spring-boot:build-image -pl $mss -Dlogging-level-test=warn &&
#              mvn package fabric8:build -pl $mss -DskipTests &&
              buildImage $mss
          done
      fi
    done
fi

for mss in "${microservices[@]}"; do
  if [[ $filterByModules == false || $filterByModules == true && "$*" == *$mss* ]]; then
    kubectl apply -f $mss/deployment.yaml
  fi
done

println "Wait for pods to be created & available"
