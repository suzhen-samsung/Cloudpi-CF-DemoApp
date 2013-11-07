import com.samsung.cloudpi.service.CloudpiService
import com.samsung.cloudpi.service.MongoDBService
import com.samsung.cloudpi.source.bean.Credential
import java.util.List
import java.util.Random
import org.codehaus.groovy.grails.commons.*
import org.springframework.core.io.support.PropertiesLoaderUtils   
import org.springframework.core.io.ClassPathResource   
dataSource {
    pooled = true
    driverClassName = "org.h2.Driver"
    username = "sa"
    password = ""
}
//MongoDBService.init()
mongodb {
//host= '127.0.0.1'
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "create-drop" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:h2:mem:devDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
        }
				
				//mongodb {
		//	MongoDBService.init()
			//List<Credential> list=MongoDBService.getMongodbCredentialList()
		//	if (list)
		//	{
		//	Credential credential=list.get(new Random().nextInt(list.size()))
 // host = credential.getHost() // adjust this according to your settings
  //port = 27017
  //databaseName = 'test'
  //username = credential.getUsername() // database user and password, if server requires authentication
  //password = credential.getPassword() 
	//println('kkk')
	//}
	//else
	//{
	//host="127.0.0.1"
	//port=27017
	//databaseName='test'
	//username='guest'
	//password='guest'
	//}
//}
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
        }
			//	mongodb {
			//MongoDBService.init()
			//List<Credential> list=MongoDBService.getMongodbCredentialList()
		//	if (list)
		//	{
		//	Credential credential=list.get(new Random().nextInt(list.size()))
 // host = credential.getHost() // adjust this according to your settings
  //port = 27017
  //databaseName = 'test'
  //username = credential.getUsername() // database user and password, if server requires authentication
  //password = credential.getPassword() 
	//println('kkk')
	//}
	//else
	//{
	//host="127.0.0.1"
	//port=27017
	//databaseName='test'
	//username='guest'
	//password='guest'
	//}
//}
    }
    production {
        dataSource {
            dbCreate = "update"
            url = "jdbc:h2:prodDb;MVCC=TRUE;LOCK_TIMEOUT=10000"
            pooled = true
            properties {
               maxActive = -1
               minEvictableIdleTimeMillis=1800000
               timeBetweenEvictionRunsMillis=1800000
               numTestsPerEvictionRun=3
               testOnBorrow=true
               testWhileIdle=true
               testOnReturn=true
               validationQuery="SELECT 1"
            }
        }
    }
}
