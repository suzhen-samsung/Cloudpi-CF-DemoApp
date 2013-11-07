package org.cloudfoundry.services;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.spy.memcached.MemcachedClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.samsung.cloudpi.CredentialParser;
import com.samsung.cloudpi.Fields;
import com.samsung.cloudpi.parser.impl.MembaseJsonParser;
import com.samsung.cloudpi.service.CloudpiService;
import com.samsung.cloudpi.service.MembaseService;
import com.samsung.cloudpi.source.bean.Credential;
import com.samsung.cloudpi.source.helper.HostChecker;
import com.samsung.cloudpi.source.helper.NoSqlHelper;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	private static final Logger logger = LoggerFactory
			.getLogger(HomeController.class);
	private MemcachedClient client;

	public HomeController() {
		Configuration.init();
		MembaseService.init();
	}

	public void initClient(String hostName) {
		try {
			if (Boolean.valueOf(Configuration.getValue("use_cloudpi_source"))) {
				client = MembaseService.getMemBaseSource(hostName);
			} else {
				// here you can write you own method to get connection
				JSONArray jsonArray = null;
				try {
					JSONObject rootObj = new JSONObject(
							System.getenv("VCAP_SERVICES"));
					JSONArray redisArray = rootObj
							.getJSONArray("membase-1.7.1.10");
					jsonArray = redisArray.getJSONObject(0).getJSONArray(
							"credentials");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject obj = jsonArray.getJSONObject(i);
						if (hostName.equals(obj.getString("host_ip"))) {
							// found the right host name
							Credential connInfo = new Credential();
							try {
								connInfo.setHost(obj.getString("host_ip"));
								connInfo.setPort(obj.getInt("port"));
								connInfo.setBucketName(obj
										.getString("bucket_name"));
								connInfo.setPassword(obj.getString("passwd"));
								client = new MemcachedClient(
										new InetSocketAddress(
												connInfo.getHost(),
												connInfo.getPort()));

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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public String get(Model model) {
		try {

			Map<String, Credential> m = new MembaseJsonParser()
					.getServiceMapFromJsonArray(System.getenv("VCAP_SERVICES"),
							Fields.MEMBASESERVICENAME.getValue());
			List<String> names = new ArrayList<String>();
			for (String name : m.keySet()) {
				names.add(name);
			}
			model.addAttribute("hostname", names);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "WEB-INF/views/get.jsp";
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) {
		try {
			MembaseService.init();
			List<String> names = MembaseService.getMembaseHostList();
			model.addAttribute("hostname", names);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "WEB-INF/views/home.jsp";
	}

	@RequestMapping(value = "/set", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void set(@RequestParam String key, HttpServletResponse response,
			@RequestParam String value, String hostName) {
		try {
			Map<String, Credential> m = new MembaseJsonParser()
					.getServiceMapFromJsonArray(System.getenv("VCAP_SERVICES"),
							Fields.MEMBASESERVICENAME.getValue());
			initClient(hostName);
			client.add(key, 0, value);
			// logger.info("value:" + client.get(key));
			// NoSqlHelper.releaseMemBase(client);
			PrintWriter out = null;
			try {
				out = response.getWriter();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			out.println("success");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}

	@RequestMapping(value = "/doGet")
	@ResponseBody
	public void doGet(@RequestParam String key, @RequestParam String hostName,
			HttpServletResponse response) {
		response.setContentType("text/plain");
		PrintWriter out = null;
		try {
			initClient(hostName);
			out = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
		out.println("value:" + client.get(key));
		NoSqlHelper.releaseMemBase(client);

	}

	@RequestMapping("/check")
	public void check(HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println();
		out.println("membase is available:");
		Credential c = CredentialParser.parseMembase(System.getenv().get(
				"VCAP_SERVICES"));
		out.println(new HostChecker().isHostAccessible(c.getHost(), c.getPort()));

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

}
