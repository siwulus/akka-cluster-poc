import com.typesafe.config.ConfigFactory

/**
  * Created on 30/06/17.
  */
object ClusteringConfig {

  private val config = ConfigFactory.load()

  lazy val clusterName = config.getString("clustering.cluster.name")
  lazy val httpPort = config.getInt("clustering.http-port")
}
