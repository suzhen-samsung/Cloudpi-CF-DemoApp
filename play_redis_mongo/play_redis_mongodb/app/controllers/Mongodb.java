package controllers;


import play.*;
import play.mvc.*;

import java.util.*;

import com.samsung.cloudpi.service.MongoDBService;
import com.mongodb.Mongo;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;


public class Mongodb extends Controller {

	public static void index() {
		MongoDBService.init();
		List mongoHostList = MongoDBService.getMongodbHostList();
		
		
        render(mongoHostList);
    }
	
	public static void set(String key, String value, String hostName) {
		String result=null;
        //System.out.println(key+"   "+value);
		
		Mongo conn = null;
		DB db = null;
		DBCollection coll = null;
		BasicDBObject doc = null;
		
		try{
			conn = MongoDBService.getMongoDBSource(hostName);
			db = conn.getDB( "cloudpidb" );
			coll = db.getCollection("testCollection");
			coll.drop();
			doc = new BasicDBObject();
			doc.put("key", key);
	        doc.put("value", value);
	        coll.insert(doc);
	        result = "success";
		}catch(Exception e){
			result = "failed";
			e.printStackTrace();
		}finally{
			conn.close();
		}		
		
		render(result);
    }
	
	public static void get(String key, String hostName) {
		//System.out.println(key);
		String result=null;
		
		Mongo conn = null;
		DB db = null;
		DBCollection coll = null;
		DBObject myDoc = null;
		BasicDBObject query = null;
		
		try{
			conn = MongoDBService.getMongoDBSource(hostName);
			db = conn.getDB( "cloudpidb" );
			coll = db.getCollection("testCollection");
			
			query = new BasicDBObject();
			query.put("key", key);			
			
			myDoc = coll.findOne(query);						
			
			result = myDoc.get("value").toString();		
	        
			//coll.remove(myDoc);
		}catch(Exception e){
			result = null;
			e.printStackTrace();
		}finally{
			conn.close();
		}
		
		render(key, result);
    }
}
