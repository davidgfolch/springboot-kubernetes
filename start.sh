#!/bin/bash
sudo systemctl start docker &&
minikube start &&
minikube dashboard
