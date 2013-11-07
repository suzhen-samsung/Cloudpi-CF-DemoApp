package grails.plugins.mongodb;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.DatastoreImpl;
import com.google.code.morphia.Morphia;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.ServerAddress;
import groovy.util.ConfigObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.grails.commons.GrailsApplication;
import org.springframework.context.ApplicationContext;
import com.samsung.cloudpi.service.CloudpiService;
import com.samsung.cloudpi.service.MongoDBService;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.samsung.cloudpi.source.bean.Credential;
import java.util.List;
import java.util.Random;
/**
 * should be registered with spring. provides access to
 * all relevant instances for working with mongodb
 *
 * @author: Juri Kuehn
 */
public class MongoHolderBean {
  private static final Log log = LogFactory.getLog(MongoDomainClass.class);

  private Morphia morphia;
  private DatastoreImpl datastore;

  /**
   * Parses config options, creates database connection and morphia & datastore instances
   *
   * @param application
   * @throws UnknownHostException
   */
  public MongoHolderBean(GrailsApplication application) throws UnknownHostException {
    Mongo mongo;

    Map flatConfig = application.getConfig().flatten();

    //String database = getConfigVar(flatConfig, "mongodb.databaseName", null);
		String database="cloudpidb";
    if (database == null) database = getConfigVar(flatConfig, "mongodb.database", "test");

    List<String> replicaSets = null;
    try {
      replicaSets = (List<String>)((ConfigObject)application.getConfig().get("mongodb")).get("replicaSet");
    } catch (Exception ignore) {}
			System.out.println("start init mongodb");
	   CloudpiService.init();
			List<Credential> list=MongoDBService.getMongodbCredentialList();
			Credential credential=list.get(new Random().nextInt(list.size()));
			
			
    if (replicaSets != null) { // user replica sets
      log.info("Creating MongoDB connection with replica sets " + replicaSets + " and database " + database);
      List<ServerAddress> addressList = new ArrayList<ServerAddress>();
      for (String addr : replicaSets) {
        addressList.add(new ServerAddress(addr));
      }
      mongo = new Mongo(addressList);
    } else { // use host port
      //String host = getConfigVar(flatConfig, "mongodb.host", "localhost");
      //int port = parsePortFromConfig(getConfigVar(flatConfig, "mongodb.port", "27017"), 27017);
		
			String host = credential.getHost();
			int port=credential.getPort();
			System.out.println("Creating MongoDB connection to host " + host + ":" + port + " and database " + database);
      log.info("Creating MongoDB connection to host " + host + ":" + port + " and database " + database);
      mongo = new Mongo(host, port);
    }
		String host = credential.getHost();
			int port=credential.getPort();
mongo = new Mongo(host, port);
    morphia = new Morphia();
    //String username = getConfigVar(flatConfig,"mongodb.username",null);
		String username=credential.getUsername();
		String pswd=credential.getPassword();
    char[] password = getCharArray(pswd);
		System.out.println("User "+":" +  username + " password " + pswd);
    datastore = (DatastoreImpl)morphia.createDatastore(mongo, database,username,password);
			System.out.println("User "+":" +  username + " password " + pswd);
		log.info("User "+":" +  username + " password " + pswd);
    // init ObjectFactory
		 //application.getConfig().merge(new ConfigSlurper().parse())
     morphia.getMapper().getOptions().objectFactory = new MongoDomainObjectFactory(application);
  }

  private char[] getCharArray(String configVar) {
      return configVar == null ? null : configVar.toCharArray();
  }

  private String getConfigVar(Map config, String key, String defaultValue) {
    if (!config.containsKey(key)) {
      log.info("MongoDB configuration option missing: '" + key + "'. Using default value '" + defaultValue + "'.");
      return defaultValue;
    }
    return config.get(key).toString();
  }

  private int parsePortFromConfig(String configVal, int defaultValue) {
    int port;
    try { // in case port is not a valid number
      port = Integer.parseInt(configVal);
    } catch (Exception e) {
      log.info("MongoDB port invalid: '" + configVal + "' is not a number. Using default value " + defaultValue);
      return defaultValue;
    }

    return port;
  }

  public Morphia getMorphia() {
    return morphia;
  }

  public Datastore getDatastore() {
    return datastore;
  }

  public Mongo getMongo() {
    return datastore.getMongo();
  }

  public DB getDb() {
    return datastore.getDB();
  }

  /**
   * returns the collection that stores instances of the given class
   * @param clazz
   * @return
   */
  public DBCollection getCollection(Class clazz) {
    return datastore.getCollection(clazz);
  }

  public DBCollection getCollection(Object obj) {
    return datastore.getCollection(obj);
  }
}
