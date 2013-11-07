package code
package snippet

import net.liftweb._
import http._
import scala.xml.NodeSeq
import com.samsung.cloudpi.service.CloudpiService
import com.samsung.cloudpi.service.RabbitMQService
import java.util.{Date, List, LinkedList, Collections, Iterator}


/**
 * A snippet that grabs the query parameters
 * from the form POST and processes them
 */
object RabbitmqHostsForm {
  def render(in: NodeSeq): NodeSeq = {
    
    //val dd :Date = new Date()
    //val ms_client :MembaseService=null
    //try{
    //    CloudpiService.init()
    //}catch{
    //}

    //val hl = new LinkedList[String]
    //hl.add("connection1")
    //hl.add("connection2")   
    CloudpiService.init()
    val hl = RabbitMQService.getRabbitMQHostList()

    // use a Scala for-comprehension to evaluate each parameter
    //for {
    //  r <- S.request if r.post_? // make sure it's a post
    //  name <- S.param("name") // get the name field
    //  value <- S.param("value") // get the age field
    //} {
      // if everything goes as expected,
      // display a notice and send the user
      // back to the home page
    //  S.notice("ENV_Name: "+name)
    //  S.notice("ENV_Value: "+value)

      S.notice("Rabbitmq Hosts List: ")
      val hi = hl.iterator
      while( hi.hasNext ){
         S.notice("Connection : " + hi.next() )
      }
      S.redirectTo("/")
    //}

    // pass through the HTML if we don't get a post and
    // all the parameters
    in
  }
}
