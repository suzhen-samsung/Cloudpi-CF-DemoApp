package code
package snippet

import net.liftweb._
import http._
import scala.xml.NodeSeq
import com.samsung.cloudpi.service.CloudpiService 
import com.samsung.cloudpi.service.MongoDBService
import java.util.{ Date, List, LinkedList, Collections, Iterator }

/**
 * A snippet that grabs the query parameters
 * from the form POST and processes them
 */
object MongoHostsForm {
  def render(in: NodeSeq): NodeSeq = {

    CloudpiService.init()
    val hl = MongoDBService.getMongodbHostList()

    S.notice("Hosts List: ")
    val hi = hl.iterator
    while (hi.hasNext) {
      S.notice("Connection : " + hi.next())
    }
    S.redirectTo("/")

    in
  }
}
