package code
package snippet

import net.liftweb._
import http._
import scala.xml.NodeSeq
import com.samsung.cloudpi.service.CloudpiService
import com.samsung.cloudpi.service.MembaseService
import java.util.{Date, List, LinkedList, Collections}


/**
 * A snippet that grabs the query parameters
 * from the form POST and processes them
 */
object MembaseEnvForm {
  //MembaseService.init()
  def render(in: NodeSeq): NodeSeq = {
    
    //val dd :Date = new Date()
    //val ms_client :MembaseService=null
    //try{
    //    CloudpiService.init()
    //}catch{
    //}
    //val ms_client :MembaseService= MembaseService.getMemBaseSource( MembaseService.getMembaseHostList().get(0) ) 
    
    // use a Scala for-comprehension to evaluate each parameter
    for {
      r <- S.request if r.post_? // make sure it's a post
      name <- S.param("name") // get the name field
      value <- S.param("value") // get the value field
      connection <- S.param("connection")
    } {
      // if everything goes as expected,
      // display a notice and send the user
      // back to the home page
      
      CloudpiService.init()
      val mclient = MembaseService.getMemBaseSource(connection)
      mclient.set(name, 60, value)

      S.notice("The ENV Name You input: "+name)
      S.notice("The ENV Value: "+ mclient.get(name) )
      S.redirectTo("/")
    }

    // pass through the HTML if we don't get a post and
    // all the parameters
    in
  }
}
