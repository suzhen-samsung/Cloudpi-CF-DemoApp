package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import com.samsung.cloudpi.service.RedisService;
import redis.clients.jedis.Jedis;


public class Redis extends Controller {

	public static void index() {
		RedisService.init();
		
		List redisHostList = RedisService.getRedisHostList("master");
		
        render(redisHostList);
    }
	
	public static void set(String key, String value, String hostName) {
		Jedis client = null;
		String result = null;
		
		try{
			client = RedisService.getRedisSourceUseJedisByName(hostName);
			client.set(key, value);
		}catch(Exception e){
			result = "failed";
		}
		
		result = "success";
		
		render(result);
	}
	
	public static void get(String key, String hostName) {
		Jedis client = null;
		String result = null;
		
		try{
			client = RedisService.getRedisSourceUseJedisByName(hostName);
			result = client.get(key);
		}catch(Exception e){
			result = null;
		}
		
		render(key, result);
	}
	
}