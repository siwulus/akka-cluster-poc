import java.net.InetAddress

import com.typesafe.config.{ConfigFactory, ConfigValueFactory}

/**
  * Created on 30/06/17.
  */
object AppConfiguration {

  lazy val config = ConfigFactory.empty()
    .withValue("akka.remote.netty.tcp.bind-hostname",
      ConfigValueFactory.fromAnyRef(InetAddress.getLocalHost.getHostAddress))
    .withFallback(ConfigFactory.load())

  lazy val clusterName = config.getString("akka-cluster-poc.clustering.cluster.name")
  lazy val httpPort = config.getInt("akka-cluster-poc.http.port")
  private lazy val clusterSeedPort = config.getString("akka-cluster-poc.clustering.seed-port")
  private lazy val awsClusterName = config.getString("akka-cluster-poc.aws.cluster.name")

}
