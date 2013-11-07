package org.cloudfoundry.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cloudfoundry.services.exception.NoServiceException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import redis.clients.jedis.Jedis;

import com.samsung.cloudpi.Fields;
import com.samsung.cloudpi.service.CloudpiService;
import com.samsung.cloudpi.service.RedisService;
import com.samsung.cloudpi.source.bean.Credential;
import com.samsung.cloudpi.source.helper.NoSqlHelper;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	private String key = "jredis::examples::Test::message";
	private static final Logger log = LoggerFactory
			.getLogger(HomeController.class);
	private Jedis client;

	public HomeController() {
		Configuration.init();
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) {
		RedisService.init();
		return "WEB-INF/views/set.html";
	}

	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public String get(Model model) {

		return "WEB-INF/views/get.html";
	}

	@RequestMapping(value = "/doGet", method = RequestMethod.POST)
	public ModelAndView doGet(Model model, @RequestParam String key,
			@RequestParam String type, HttpServletResponse response) {
		String value = "";
		initClient(type);
		response.setContentType("text/plain");
		PrintWriter out = null;

		try {
			value = client.get(key);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			NoSqlHelper.releaseJedis(client);
		}
		try {
			out = response.getWriter();
			out.println("value in " + type + ": " + value + "<br/>");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}

	/**
	 * init client by type
	 * 
	 * @param type
	 */
	public void initClient(String type) {
		RedisService.init();
		if (Boolean.valueOf(Configuration.getValue("use_cloudpi_source"))) {
			List<Jedis> list = RedisService.getRedisSourceUseJedis(type);
			if (list.size() > 0) {
				client = RedisService.getRedisSourceUseJedis(type).get(
						new Random().nextInt(list.size()));
			} else {
				throw new NoServiceException(type);
			}
		} else {
			// here you can write your own method to get connections
			JSONArray jsonArray = null;
			try {
				JSONObject rootObj = new JSONObject(
						System.getenv("VCAP_SERVICES"));
				JSONArray redisArray = rootObj.getJSONArray("redis-2.2.110");
				jsonArray = redisArray.getJSONObject(
						new Random().nextInt(redisArray.length()))
						.getJSONArray("credentials");
				List<Credential> list = new ArrayList<Credential>();
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject obj = jsonArray.getJSONObject(i);
					if (type.equals(obj.getString(Fields.TYPE.getValue()))) {
						// found the right type
						Credential connInfo = new Credential();
						try {
							connInfo.setHost(obj.getString(Fields.HOST
									.getValue()));
							connInfo.setPort(obj.getInt(Fields.PORT.getValue()));
							connInfo.setPassword(obj.getString(Fields.PASSWORD
									.getValue()));
							connInfo.setType(obj.getString(Fields.TYPE
									.getValue()));

							list.add(connInfo);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						continue;
					}

				}
				if (list.size() > 0) {
					Credential connInfo = list.get(new Random().nextInt(list
							.size()));
					client = new Jedis(connInfo.getHost(), connInfo.getPort());
					client.auth(connInfo.getPassword());
				} else {
					throw new NoServiceException(type);
				}

			} catch (JSONException e) {
				e.printStackTrace();
				throw new NoServiceException(type);
			}

		}
	}

	@RequestMapping(value = "/set", method = RequestMethod.POST)
	public void set(@RequestParam String key, @RequestParam String value,
			HttpServletResponse response) {
		PrintWriter out = null;
		initClient("master");
		try {

			if (client != null)
				client.set(key, value);

			NoSqlHelper.releaseJedis(client);
			// initClient("slave");
			// if (client != null)
			// client.set(key, value);
			// NoSqlHelper.releaseJedis(client);
			response.setContentType("text/plain");

			out = response.getWriter();
			out.println("success");
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	@RequestMapping("/env")
	public void env(HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println("System Properties:");
		for (Map.Entry<Object, Object> property : System.getProperties()
				.entrySet()) {
			out.println(property.getKey() + ": " + property.getValue());
		}
		out.println();
		out.println("System Environment:");
		for (Map.Entry<String, String> envvar : System.getenv().entrySet()) {
			out.println(envvar.getKey() + ": " + envvar.getValue());
		}
	}

	@ExceptionHandler(value = { NoServiceException.class, JSONException.class })
	public String handleException(Exception ex, HttpServletRequest request) {
		request.setAttribute("error", ex.getMessage());
		return "WEB-INF/views/exception.jsp";
	}
}
