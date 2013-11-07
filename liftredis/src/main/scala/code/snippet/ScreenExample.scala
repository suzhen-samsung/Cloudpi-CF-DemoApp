package code
package snippet

import net.liftweb._
import http._
import _root_.redis.clients.jedis._
import com.samsung.cloudpi.service.CloudpiService;
import com.samsung.cloudpi.service.RedisService;
import java.util.{List,Random}
/**
 * Declare the fields on the screen
 */
object ScreenExample extends LiftScreen {
  // here are the fields and default values
  val name = field("Name", "")
val pool = new JedisPool(new JedisPoolConfig(), "109.105.20.169");
  // the age has validation rules
  val age = field("Age", 0, minVal(13, "Too Young"))

  def finish() {
    S.notice("Name: "+name)
    S.notice("Age: "+age)
    //CloudpiService.init()
    createUsers(name,age)
  }
	
	def createUser(username: String, age: Int) = {
		val jedis = pool.getResource
			try {
				jedis.set("username", username)
				jedis.set("age", "age")
			} catch {
				case e => e.printStackTrace
			} finally {
				pool.returnResource(jedis)
			}
		
	}
      def createUsers(username: String,age: Int)={
CloudpiService.init()  
val jedis=RedisService.getRedisSourceUseJedis("master").get(
						new Random().nextInt(RedisService.getRedisSourceUseJedis("master").size()))
				jedis.set("username", username)
                                jedis.set("age", "age")
}
}
