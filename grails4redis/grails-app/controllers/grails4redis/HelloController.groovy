package grails4redis

import java.util.List;
import com.samsung.cloudpi.service.RedisService;
import redis.clients.jedis.Jedis;

class HelloController {
	def   List<Host> hosts   = new ArrayList<Host>()
	def String msg = "";
	def String result="";
	def String env = "";

	def index() {
		RedisService.init();
		hosts = RedisService.getRedisHostList("master");
	}
	def set(){
		RedisService.init();
		def List<Jedis> clients = RedisService.getRedisSourceUseJedis("master");
		clients.get(0).set(params.key, params.value);
		msg = "success"
	}
	def get(){
		RedisService.init();
		hosts = RedisService.getRedisHostList("master");
	}
	def doGet(){
		RedisService.init();
		def List<Jedis> clients= RedisService.getRedisSourceUseJedis(params.type);
		result = "the value of key ["+params.key+"] is "+clients.get(0).get(params.key)
		return result
	}
	def env(){
		def StringBuffer sb = new StringBuffer()
		for (Map.Entry<String, String> envvar : System.getenv().entrySet()) {
			sb.append(envvar.getKey() + ": " + envvar.getValue()).append("\r\n");
		}
		env = sb.toString()
	}
}
