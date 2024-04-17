./mvnw install
java -jar target/*.jar
curl localhost:8080/actuator | jq

#build image
docker login
./mvnw spring-boot:build-image
docker run -p 8080:8080 dgf-user:0.0.1-SNAPSHOT
docker tag dgf-user:0.0.1-SNAPSHOT davidgfolch/dgf-user
docker push davidgfolch/dgf-user

#deploy to kubernetes
#just first time to create deployment.yaml
#kubectl create deployment dgf-user --image=davidgfolch/-user --dry-run=client -o=yaml > deployment.yaml
#kubectl create service clusterip dgf-user --tcp=8080:8080 --dry-run=client -o=yaml >> deployment.yaml
#update deployment.yaml
kubectl apply -f deployment.yaml
kubectl port-forward svc/springboot-user 8080:8080
