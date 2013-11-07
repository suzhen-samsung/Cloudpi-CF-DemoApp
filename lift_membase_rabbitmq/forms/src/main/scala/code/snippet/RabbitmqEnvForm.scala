package code
package snippet

import net.liftweb._
import http._
import scala.xml.NodeSeq
import com.samsung.cloudpi.service.CloudpiService
import com.samsung.cloudpi.service.RabbitMQService
import java.util.{Date, List, LinkedList, Collections}
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.QueueingConsumer

/**
 * A snippet that grabs the query parameters
 * from the form POST and processes them
 */
object RabbitmqEnvForm {
  def render(in: NodeSeq): NodeSeq = {
    
    // use a Scala for-comprehension to evaluate each parameter
    for {
      r <- S.request if r.post_? // make sure it's a post
      name <- S.param("name") // get the name field
      value <- S.param("value") // get the value field
      connection <- S.param("connection")
    } {
      
      val QUEUE_NAME = name
      CloudpiService.init()
      val rconnection = RabbitMQService.getRabbitSource(connection)
      val channel = rconnection.createChannel()
      channel.queueDeclare(QUEUE_NAME, false, false, false, null)
      channel.basicPublish("", QUEUE_NAME, null, value.getBytes() )

      
      val consumer = new QueueingConsumer(channel);
      channel.basicConsume(QUEUE_NAME, true, consumer);
      val delivery = consumer.nextDelivery()
      val message = new String(delivery.getBody() )
      

      S.notice("The Queue Name You input: "+name)
      S.notice("The ENV Value: "+  message)
      channel.close()
      rconnection.close()
      S.redirectTo("/")
    }

    // pass through the HTML if we don't get a post and
    // all the parameters
    in
  }
}
