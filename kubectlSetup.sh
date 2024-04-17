#!/bin/bash
kubectl create secret generic springboot-mysql-secret \
  --from-literal=mysql-root-password=kube1234 \
   --from-literal=mysql-user=springboot-user \
   --from-literal=mysql-password=kube1234
kubectl create configmap springboot-mysql-db --from-literal="mysql-database=springboot-db"
