package code
package snippet

import net.liftweb._
import http._
import scala.xml.NodeSeq
import com.samsung.cloudpi.service.CloudpiService
import com.samsung.cloudpi.service.MongoDBService
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import java.net.UnknownHostException; 
import com.mongodb.DB; 
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import java.util.{ Date, List, LinkedList, Collections }

/**
 * A snippet that grabs the query parameters
 * from the form POST and processes them
 */
object MongoEnvForm {
  def render(in: NodeSeq): NodeSeq = {

    // use a Scala for-comprehension to evaluate each parameter
    for {
      r <- S.request if r.post_? // make sure it's a post
      name <- S.param("name") // get the name field
      value <- S.param("value") // get the value field
      connection <- S.param("connection")
    } {

      CloudpiService.init()
      val mongo = MongoDBService.getMongoDBSource(connection)
      val db = mongo.getDB("cloudpidb");
      val collection = db.getCollection("collection");
      val document = new BasicDBObject();
      document.put("name", name);
      document.put("value", value);
      collection.insert(document);
      val searchQuery = new BasicDBObject();
      searchQuery.put("name", name);
      val cursor = collection.find(searchQuery);
      while (cursor.hasNext()) {
        S.notice("The ENV Name You input: " + name)
        S.notice("The ENV Value: " + cursor.next())
      }

      S.redirectTo("/")
    }

    in
  }
}
