# akka-cluster-poc

docker run -it --rm -p 2551:2551 -e CLUSTER_IP=192.168.3.106 -e CLUSTER_PORT=2551 akka-cluster-poc:latest
docker run -it --rm -p 2552:2552 -e CLUSTER_IP=192.168.3.106 -e CLUSTER_PORT=2552 -e SEED_PORT=2551 akka-cluster-poc:latest
docker run -it --rm -p 2552:2552 -e CLUSTER_IP=192.168.3.106 -e CLUSTER_PORT=2552 -e SEED_IP=192.168.3.106 -e SEED_PORT=2551 akka-cluster-poc:latest



java -jar -DHTTP_PORT=8080 -DCLUSTER_PORT=2551 -DSEED_PORT=2553 akka-cluster-poc-assembly-1.0.jar
java -jar -DHTTP_PORT=8081 -DCLUSTER_PORT=2552 -DSEED_PORT=2553 akka-cluster-poc-assembly-1.0.jar
java -jar -DHTTP_PORT=8082 -DCLUSTER_PORT=2553 -DSEED_PORT=2551 akka-cluster-poc-assembly-1.0.jar
