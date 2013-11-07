package grails4membase



import com.samsung.cloudpi.service.RabbitMQService;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;


class HelloController {
	def   List<Host> hosts   = new ArrayList<Host>()
	def String msg = "";
	def String result="";
	def String env = "";
	def index() {
		RabbitMQService.init();
		hosts = RabbitMQService.getRabbitMQHostList(); 
	}
	def set(){
		//def MemcachedClient client = MembaseService.getMemBaseSource(params.hostname);
		//client.add(params.key, 0, params.value);
		//msg = "success"
		RabbitMQService.init();
                def String s_host = params.hostname;
                def String s_queue = params.key;
                def String s_value = params.value;
		com.rabbitmq.client.Connection rconnection = RabbitMQService.getRabbitSource(s_host);
		com.rabbitmq.client.Channel channel = rconnection.createChannel();
		channel.queueDeclare(s_queue, false, false, true, null);
	        channel.basicPublish("", s_queue, null, s_value.getBytes() );
		
		//channel.close();
	        //rconnection.close();

		msg = "success";
	}
	def get(){
                RabbitMQService.init();
		hosts = RabbitMQService.getRabbitMQHostList();

	}
	def doGet(){
		//def MemcachedClient client = MembaseService.getMemBaseSource(params.hostname);
		//result = "the value of key ["+params.key+"] is "+client.get(params.key)
		//return result
                RabbitMQService.init();
                def String s_host = params.hostname;
                def String s_queue = params.key;
		com.rabbitmq.client.Connection rconnection = RabbitMQService.getRabbitSource(s_host);
                com.rabbitmq.client.Channel channel = rconnection.createChannel();
		com.rabbitmq.client.QueueingConsumer consumer = new QueueingConsumer(channel);
		
		channel.basicConsume(s_queue, true, consumer);
		result =  new String( consumer.nextDelivery(10000).getBody() );
                result = "the value you input into queue ["+ s_queue +"] is " + result;
                channel.queueDelete(s_queue);
		channel.close();
	        rconnection.close();

		return result;
	}
	def env(){
		def StringBuffer sb= new StringBuffer()
		for (Map.Entry<String, String> envvar : System.getenv().entrySet()) {
			sb.append(envvar.getKey() + ": " + envvar.getValue()).append("\r\n");
		}
		env = sb.toString()
	}
}
