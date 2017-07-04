/**
  * Created on 29/06/17.
  */
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor.ActorLogging
import akka.actor.Actor

class ClusterListener extends Actor with ActorLogging {
  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    log.info("starting up cluster listener...")
    Cluster(context.system).subscribe(self, classOf[ClusterDomainEvent])
  }

  def receive = {
    case state: CurrentClusterState ⇒
      log.info("Current members: {}", state.members.mkString(", "))
    case MemberUp(member) =>
      log.info("Member is Up: {}", member.address)
    case UnreachableMember(member) =>
      log.info("Member detected as unreachable: {}", member)
    case MemberRemoved(member, previousStatus) =>
      log.info("Member is Removed: {} after {}",
        member.address, previousStatus)
    case LeaderChanged(member) => log.info("Leader changed: " + member)
    case any: MemberEvent => log.info("Member Event: " + any.toString) // ignore
  }
}
