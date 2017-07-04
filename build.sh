#!/usr/bin/env bash

AWS_OWNER="921100211113"

sbt assembly
docker build -t akka-cluster-poc .

docker tag akka-cluster-poc:latest ${AWS_OWNER}.dkr.ecr.eu-west-2.amazonaws.com/akka-cluster-poc:latest
$(aws ecr get-login --region eu-west-2)
docker push ${AWS_OWNER}.dkr.ecr.eu-west-2.amazonaws.com/akka-cluster-poc:latest