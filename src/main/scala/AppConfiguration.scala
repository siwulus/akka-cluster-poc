import java.net.InetAddress

import com.typesafe.config.{ConfigFactory, ConfigValueFactory}
import scala.collection.JavaConverters._
import AWSInstances._

/**
  * Created on 30/06/17.
  */
object ClusteringConfig {

  private lazy val originConfig = ConfigFactory.load()

  lazy val clusterName = originConfig.getString("clustering.cluster.name")
  lazy val httpPort = originConfig.getInt("clustering.http-port")
  private lazy val clusterSeedPort = originConfig.getString("clustering.seed-port")
  private lazy val awsClusterName = originConfig.getString("aws.cluster.name")

  lazy val config = {

    val natConfig = ConfigFactory.empty()
      .withValue("akka.remote.netty.tcp.bind-hostname", ConfigValueFactory.fromAnyRef(InetAddress.getLocalHost.getHostAddress))
      .withValue("clustering.ip", ConfigValueFactory.fromAnyRef(InetAddress.getLocalHost.getHostAddress))
      .withValue("clustering.seed-ip", ConfigValueFactory.fromAnyRef(InetAddress.getLocalHost.getHostAddress))

    AWSInstances.ec2InstanceIps(awsClusterName) match {
      case Left(_) => natConfig.withFallback(originConfig.withFallback(originConfig))
      case Right(ips) => { natConfig.withValue("akka.cluster.seed-nodes",
        ConfigValueFactory.fromIterable(ips.map(ip => s"akka.tcp://$clusterName@$ip:$clusterSeedPort").asJava))
        .withFallback(originConfig)
      }
    }


  }
}
