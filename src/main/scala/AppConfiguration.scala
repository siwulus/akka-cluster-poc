import java.net.InetAddress

import com.typesafe.config.{ConfigFactory, ConfigValueFactory}
import scala.collection.JavaConverters._
import AWSInstanceConfiguration._

/**
  * Created on 30/06/17.
  */
object AppConfiguration {

  private lazy val originConfig = ConfigFactory.load()

  lazy val clusterName = originConfig.getString("akka-cluster-poc.clustering.cluster.name")
  lazy val httpPort = originConfig.getInt("akka-cluster-poc.http.port")
  private lazy val clusterSeedPort = originConfig.getString("akka-cluster-poc.clustering.seed-port")
  private lazy val awsClusterName = originConfig.getString("akka-cluster-poc.aws.cluster.name")

  lazy val config = {

    val myIp = AWSInstanceConfiguration.selfEc2InstanceIp.get
    val myLocalIp = InetAddress.getLocalHost.getHostAddress
    val natConfig = ConfigFactory.empty()
      .withValue("akka.remote.netty.tcp.hostname", ConfigValueFactory.fromAnyRef(myIp))
      .withValue("akka.remote.netty.tcp.bind-hostname", ConfigValueFactory.fromAnyRef(myLocalIp))

    AWSInstanceConfiguration.ec2InstanceIps(awsClusterName) match {
      case Left(_) => natConfig.withFallback(originConfig.withFallback(originConfig))
      case Right(ips) => { natConfig.withValue("akka.cluster.seed-nodes",
        ConfigValueFactory.fromIterable(
          (myIp::ips.filter(!_.equals(myIp))).map(ip => s"akka.tcp://$clusterName@$ip:$clusterSeedPort")
            .asJava))
        .withFallback(originConfig)
      }
    }


  }
}
