package org.cloudfoundry.samples;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Handles requests
 */
@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory
			.getLogger(HomeController.class);
	
	Logger metricLogger = LoggerFactory.getLogger("org.cloudpi.metrics");
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		logger.info("Welcome home!");
		return "home";
	}
	
	@RequestMapping("/sample")
	public void sample(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//send {"users" : 1}
		metricLogger.debug("{\"users\": 1}");

		//send {"users" : 10,000}
		int userCnt = 10000;
		metricLogger.debug("{\"users\":" + userCnt + "}");

		//send {"users" : [1,5,2,2,2]}
		metricLogger.debug("{\"users\": [1,5,2,2,2]}");
		int[] usersArr = {1, 5, 2, 2, 2};
		try {
			metricLogger.debug("{\"users\":" + new JSONArray(usersArr).toString() + "}");		
		} catch (JSONException e) {
			e.printStackTrace();
		}

		//send {"users" : 1, "rate" : 55.5}
		float rateVal = 55.5f;
		metricLogger.debug("{\"users\":1, " +"\"rate\":" + rateVal + "}");		

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println("send sample");
	}
	
	@RequestMapping("/count")
	public void count(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//int value = 1;
		String name = request.getParameter("name");
		String value = request.getParameter("value");
		metricLogger.debug("{\"" + name + "\":" + value + "}");
		
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println("send metrics : " + name + ", " + value);
	}

	@RequestMapping(value = "/send", method = RequestMethod.POST)
	public void home(HttpServletResponse response, @RequestParam String name, @RequestParam String value) throws IOException {
		logger.info("Welcome " + name + ", " + value);
		metricLogger.debug("{\"" + name + "\":" + value + "}");
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println("send metrics : " + name + ", " + value);
	}

	@RequestMapping(value = "/getSession")
	public void getSession(HttpServletResponse response, HttpSession session) {
		try {
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();

			out.println(System.getenv("VCAP_APPLICATION"));
			out.println(session.getAttribute("user") + session.getId());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping("/env")
	public void env(HttpServletResponse response) throws IOException {
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.println("System Environment:");
		out.println("System Environment:");
		for (Map.Entry<String, String> envvar : System.getenv().entrySet()) {
			out.println(envvar.getKey() + ": " + envvar.getValue());
		}
	}
}