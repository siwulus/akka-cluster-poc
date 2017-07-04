import java.io.{BufferedReader, InputStreamReader}
import java.net.URL

import com.amazonaws.services.ec2.model.DescribeInstancesRequest
import com.amazonaws.services.ec2.{AmazonEC2, AmazonEC2ClientBuilder}
import com.amazonaws.services.ecs.model.{DescribeContainerInstancesRequest, Failure, ListContainerInstancesRequest}
import com.amazonaws.services.ecs.{AmazonECS, AmazonECSClientBuilder}

import scala.collection.JavaConverters._

/**
  * Created on 03/07/17.
  */
object AWSInstanceConfiguration {

  implicit val escClient = AmazonECSClientBuilder.defaultClient()
  implicit val ec2Client = AmazonEC2ClientBuilder.defaultClient()

  private def containerInstanceArns(clusterName: String)(implicit client: AmazonECS): List[String] = client.listContainerInstances(
    new ListContainerInstancesRequest()
      .withCluster(clusterName)
      .withMaxResults(100))
    .getContainerInstanceArns.asScala.toList

  lazy private val selfEc2fInstanceId: String = {
    val conn = new URL("http://169.254.169.254/latest/meta-data/instance-id").openConnection
    val in = new BufferedReader(new InputStreamReader(conn.getInputStream))
    try in.readLine() finally in.close()
  }

  private def ec2InstanceIds(clusterName: String)(implicit client: AmazonECS): Either[List[Failure], List[String]] = {
    val containerInstances = client.describeContainerInstances(new DescribeContainerInstancesRequest()
      .withCluster(clusterName)
      .withContainerInstances(containerInstanceArns(clusterName)(client).asJava))
    if (containerInstances.getFailures.size() > 0)
      Left(containerInstances
        .getFailures
        .asScala
        .toList)
    else
      Right(containerInstances.getContainerInstances
        .asScala
        .toList
        .map(_.getEc2InstanceId))
  }


  def selfEc2InstanceIp(implicit ec2Client: AmazonEC2): Option[String] = ec2Client.describeInstances(
    new DescribeInstancesRequest().withInstanceIds(selfEc2fInstanceId))
    .getReservations
    .asScala
    .flatMap(_.getInstances.asScala)
    .map(_.getPrivateIpAddress)
    .toList
    .headOption


  def ec2InstanceIps(clusterName: String)(implicit ec2Client: AmazonEC2, ecsClient: AmazonECS): Either[List[Failure], List[String]] = {
    ec2InstanceIds(clusterName)(ecsClient) match {
      case Left(failures) => Left(failures)
      case Right(ec2InstanceIds) => Right(ec2Client.describeInstances(new DescribeInstancesRequest()
        .withInstanceIds(ec2InstanceIds.asJava))
        .getReservations
        .asScala
        .flatMap(_.getInstances.asScala)
        .map(_.getPrivateIpAddress)
        .toList)

    }
  }

}
