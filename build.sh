#!/usr/bin/env bash


sbt assembly
docker build -t akka-cluster-poc .
