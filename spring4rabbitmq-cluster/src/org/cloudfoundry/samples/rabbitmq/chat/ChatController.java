package org.cloudfoundry.samples.rabbitmq.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.samsung.cloudpi.Fields;
import com.samsung.cloudpi.parser.impl.RabbitMQParser;
import com.samsung.cloudpi.reader.impl.DefaultServiceReader;
import com.samsung.cloudpi.service.CloudpiService;
import com.samsung.cloudpi.service.RabbitMQService;
import com.samsung.cloudpi.source.bean.Credential;
import com.samsung.cloudpi.source.helper.NoSqlHelper;

@Controller
public class ChatController {
	private static final Logger logger = LoggerFactory
			.getLogger(ChatController.class);
	private String QUEUE_NAME = "hello";
	private ConnectionFactory connfactory;
	Connection connection;
	// private volatile RabbitTemplate amqpTemplate;
	// private SimpleMessageListenerContainer listenerContainer;
	private final Queue<String> messages = new LinkedBlockingQueue<String>();

	public void initRabbitMQ(String hostname) {

		System.out.println("======================="
				+ DefaultServiceReader.dedicatedServiceJsonDocument);
		if (Boolean.valueOf(Configuration.getValue("use_cloudpi_source"))) {
			try {
				connfactory = RabbitMQService
						.getRabbitConnectionFactory(hostname);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// here you can write your own method to get service. maybe a
			// test in a test environment
			JSONArray jsonArray = null;
			try {
				JSONObject rootObj = new JSONObject(
						System.getenv("VCAP_SERVICES"));
				JSONArray redisArray = rootObj.getJSONArray("rabbitmq-2.7.00");
				jsonArray = redisArray.getJSONObject(0).getJSONArray(
						"credentials");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject obj = jsonArray.getJSONObject(i);
					if (hostname.equals(obj.getString(Fields.HOST.getValue()))) {
						// found the right host name
						Credential connInfo = new Credential();
						try {
							connInfo.setHost(obj.getString(Fields.HOST
									.getValue()));
							connInfo.setPort(obj.getInt(Fields.PORT.getValue()));
							connInfo.setUsername(obj.getString(Fields.USERNAME
									.getValue()));
							connInfo.setPassword(obj.getString(Fields.PASSWORD
									.getValue()));
							connInfo.setVhost(obj.getString(Fields.VHOST
									.getValue()));
							connfactory = new ConnectionFactory();
							connfactory.setUsername(connInfo.getUsername());
							connfactory.setPassword(connInfo.getPassword());
							connfactory.setVirtualHost(connInfo.getVhost());
							connfactory.setHost(connInfo.getHost());
							connfactory.setPort(connInfo.getPort());

							break;
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						continue;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public ChatController() {
		Configuration.init();
		RabbitMQService.init();
	}

	public Model setHostOptions(Model model) {
		try {

			List<String> names = RabbitMQService.getRabbitMQHostList();

			model.addAttribute("hostname", names);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return model;
	}

	@RequestMapping(value = "/get")
	public String get(Model model) {
		model = setHostOptions(model);
		return "WEB-INF/views/get.jsp";
	}

	@RequestMapping(value = "/")
	public String home(Model model) {
		model = setHostOptions(model);
		return "WEB-INF/views/chat.jsp";
	}

	@RequestMapping(value = "/publish", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void publish(@RequestParam String username,
			@RequestParam String text, @RequestParam String hostname) {
		logger.info("!!!!!!!!!!!!!" + username + ": " + text);
		Channel channel = null;
		try {
			initRabbitMQ(hostname);

			connection = connfactory.newConnection();
			channel = connection.createChannel();
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			String message = username + ": " + text;
			channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
			System.out.println(" [x] Sent '" + message + "'");

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			NoSqlHelper.releaseRabbitMQ(connection, channel);
		}

	}

	@RequestMapping(value = "/chatlog")
	@ResponseBody
	public String chatlog(@RequestParam String hostname) {
		// ConnectionFactory factory;
		Channel channel = null;
		try {
			initRabbitMQ(hostname);
			connection = connfactory.newConnection();
			channel = connection.createChannel();

			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicConsume(QUEUE_NAME, true, consumer);

			while (true) {
				QueueingConsumer.Delivery delivery = consumer.nextDelivery();
				String message = new String(delivery.getBody());
				System.out.println(" [x] Received '" + message + "'");
				messages.add(message);
				NoSqlHelper.releaseRabbitMQ(connection, channel);
				return StringUtils.arrayToDelimitedString(messages.toArray(),
						"<br/>");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return QUEUE_NAME;

	}

}
