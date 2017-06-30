FROM openjdk:latest

WORKDIR /

COPY "./target/scala-2.12/akka-cluster-poc-assembly-1.0.jar" /

CMD ["java", "-jar", "akka-cluster-poc-assembly-1.0.jar"]