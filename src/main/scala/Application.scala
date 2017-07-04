import java.net.InetAddress

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

/**
  * Created on 29/06/17.
  */
object Application extends App {

  println(s"Local host IP: ${InetAddress.getLocalHost.getHostAddress}")
  println("akka-cluster-poc: " + AppConfiguration.config.getAnyRef("akka-cluster-poc"))
  println("akka.remote.netty.tcp: " + AppConfiguration.config.getAnyRef("akka.remote.netty.tcp"))
  println("akka.cluster.seed-nodes: " + AppConfiguration.config.getAnyRef("akka.cluster.seed-nodes"))

  implicit val system = ActorSystem(AppConfiguration.clusterName, AppConfiguration.config)
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  val clusterListener = system.actorOf(Props[ClusterListener], name = "clusterListener")

  val tickCounter = {
    val singletonProps = ClusterSingletonManager.props(
      singletonProps = Props[TickCounter],
      terminationMessage = PoisonPill,
      settings = ClusterSingletonManagerSettings(system)
    )
    system.actorOf(singletonProps, "tick-counter-singleton")
  }

  val route =
    path("hello") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", AppConfiguration.httpPort)

  //  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  //  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .foreach(binding => println(s"Server online at ${binding.localAddress}")) // trigger unbinding from the port

}
