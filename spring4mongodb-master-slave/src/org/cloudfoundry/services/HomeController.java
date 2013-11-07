package org.cloudfoundry.services;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray; 
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.CollectionCallback;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException; 
import com.samsung.cloudpi.reader.impl.DefaultServiceReader; 
import com.samsung.cloudpi.service.MongoDBService;
import com.samsung.cloudpi.source.bean.Credential;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	MongoDbFactory mongoDbFactory;

	MongoTemplate mongoTemplate;

	@Autowired(required = false)
	@Qualifier(value = "serviceProperties")
	Properties serviceProperties;

	public HomeController() {
		MongoDBService.init();
		System.out.println("---------------------------"
				+ DefaultServiceReader.dedicatedServiceJsonDocument);
		Configuration.init();
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Model model) {
		List<String> names = MongoDBService.getMongodbHostList();

		try {

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		model.addAttribute("hostname", names);
		return "home";
	}

	@RequestMapping(value = "/result", method = RequestMethod.POST)
	public String result(Model model, @RequestParam String hostname) {
		try {
			if (Boolean.valueOf(Configuration.getValue("use_cloudpi_source"))) {
				mongoDbFactory = new SimpleMongoDbFactory(
						MongoDBService.getMongoDBSource(hostname), "cloudpidb");
			} else {
				// here you can write your own method to get connection
				JSONArray jsonArray = null;
				try {
					JSONObject rootObj = new JSONObject(
							System.getenv("VCAP_SERVICES"));
					JSONArray redisArray = rootObj
							.getJSONArray("mongodb-2.0.20");
					jsonArray = redisArray.getJSONObject(0).getJSONArray(
							"credentials");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject obj = jsonArray.getJSONObject(i);
						if (hostname.equals(obj.getString("host"))) {
							// found the right host name
							Credential connInfo = new Credential();
							try {
								connInfo.setHost(obj.getString("host"));
								connInfo.setPort(obj.getInt("port"));
								connInfo.setUsername(obj.getString("username"));
								connInfo.setPassword(obj.getString("password"));
								connInfo.setDb(obj.getString("db"));
								Mongo m = null;

								m = new Mongo(connInfo.getHost(),
										connInfo.getPort());
								DB db = m.getDB(connInfo.getDb());
								boolean auth = db.authenticate(connInfo
										.getUsername(), connInfo.getPassword()
										.toCharArray());
								if (!auth)
									throw new Exception("authentication failed");
								mongoDbFactory = new SimpleMongoDbFactory(m,
										"cloudpidb");
								break;

							} catch (Exception e) {
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
			e.printStackTrace();
		}
		mongoTemplate = new MongoTemplate(mongoDbFactory);
		List<String> services = new ArrayList<String>();
		if (mongoDbFactory != null) {
			services.add("MongoDB: "
					+ mongoDbFactory.getDb().getMongo().getAddress());
		}
		Random generator = new Random();
		Person p = new Person("Joe Cloud-" + generator.nextInt(100),
				generator.nextInt(100));
		mongoTemplate.save(p);
		List<Person> people = mongoTemplate.find(
				new Query(where("age").lt(100)), Person.class);

		model.addAttribute("people", people);
		model.addAttribute("services", services);
		model.addAttribute("serviceProperties", getServicePropertiesAsList());
		return "result";
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

	@RequestMapping("/service-properties")
	public void services(HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		if (serviceProperties != null) {
			out.println("Cloud Service Properties:");
			// Map envMap = System.getenv();
			for (Object key : serviceProperties.keySet()) {
				out.println(key + ": " + serviceProperties.get(key));
			}
		} else {
			out.println("No Cloud Service Properties found.  Check configuration file for <cloud:service-properties/> element");
		}
		out.println(")<a href=\"/\">Return to previous page.</a>");
		out.println();
	}

	private List<String> getServicePropertiesAsList() {
		List<String> propList = new ArrayList<String>();
		if (serviceProperties != null) {
			for (Object key : serviceProperties.keySet()) {
				propList.add(key + ": " + serviceProperties.get(key));
			}
		}
		return propList;
	}

	@RequestMapping("/deleteAll")
	public void deleteAll(HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		long count = mongoTemplate.execute(Person.class,
				new CollectionCallback<Long>() {
					@Override
					public Long doInCollection(DBCollection collection)
							throws MongoException, DataAccessException {
						return collection.count();
					}
				});
		out.println("Deleted " + count + " entries");
		mongoTemplate.dropCollection(Person.class);
	}

}
