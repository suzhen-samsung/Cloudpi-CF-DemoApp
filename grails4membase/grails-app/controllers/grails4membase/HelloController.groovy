package grails4membase



import com.samsung.cloudpi.service.MembaseService;
import net.spy.memcached.MemcachedClient

class HelloController {
	def   List<Host> hosts   = new ArrayList<Host>()
	def String msg = "";
	def String result="";
	def String env = "";
	def index() {
		MembaseService.init();
		hosts = MembaseService.getMembaseHostList();
	}
	def set(){
		MembaseService.init();
		def MemcachedClient client = MembaseService.getMemBaseSource(params.hostname);
		client.add(params.key, 0, params.value);
		msg = "success"
	}
	def get(){
		MembaseService.init();
		hosts = MembaseService.getMembaseHostList();
	}
	def doGet(){
		MembaseService.init();
		def MemcachedClient client = MembaseService.getMemBaseSource(params.hostname);
		result = "the value of key ["+params.key+"] is "+client.get(params.key)
		return result
	}
	def env(){
		MembaseService.init();
		def StringBuffer sb= new StringBuffer()
		for (Map.Entry<String, String> envvar : System.getenv().entrySet()) {
			sb.append(envvar.getKey() + ": " + envvar.getValue()).append("\r\n");
		}
		env = sb.toString()
	}
}
