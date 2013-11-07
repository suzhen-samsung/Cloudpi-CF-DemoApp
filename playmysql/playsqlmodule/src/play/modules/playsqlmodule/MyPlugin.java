package play.modules.playsqlmodule;
import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.db.DB;
import com.samsung.cloudpi.service.CloudpiService;
import com.samsung.cloudpi.service.MysqlService;
import com.samsung.cloudpi.source.bean.Credential;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.Random;
public class MyPlugin extends PlayPlugin 
{   
	public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	public static final String MYSQL_DIALECT = "org.hibernate.dialect.MySQL5Dialect";
	
	@Override
	public void onApplicationStart() 
	{     
		Logger.info("Yeeha, firstmodule started");   
		 Properties p = Play.configuration;

        if (!checkPlaySQLConfig(p)) {
            return;
        }
        //rdbmsServiceConfig(p);
				mysqlServiceConfig(p);
       
	}
	
	private void rdbmsServiceConfig(Properties p) {
           // p.put("db.driver", MYSQL_DRIVER);
            p.put("jpa.dialect", MYSQL_DIALECT);
        setCredential(p, "jdbc:mysql://127.0.0.1/test", "root", "168861");
    }

    private void setCredential(Properties p, String url, String username, String password) {
        p.put("db.url", "jdbc:mysql://"+url+"/cloudpidb");
        p.put("db.user", username);
        p.put("db.pass", password);
    }

    /**
     * Check that no SQL database is configured.
     *
     * @param p Play configuration.
     * @return <code>true</code> if no Play SQL configuration is defined, <code>false</code> otherwise.
     * @since 2011.09.07
     */
    private boolean checkPlaySQLConfig(Properties p) {
        // We configure the Cloud Foundry SQL database only if no other Play DB
        // is configured.
        if (p.containsKey("db") || p.containsKey("db.url")) {
            Logger.warn("[CloudFoundry] A SQL database configuration already exists. It will not be overriden.");
            return false;
        }

        return true;
    }
	
		public  void mysqlServiceConfig(Properties p)
		{
			System.out.println("start load config");
			MysqlService.init();
			List<Credential> list=MysqlService.getMysqlCredentialList("master");
			Credential credential=list.get(new Random().nextInt(list.size()));
			 p.put("db.driver", MYSQL_DRIVER);
			 p.put("jpa.dialect", MYSQL_DIALECT);
			String host=credential.getHost();
			String user=credential.getUsername();
			String password=credential.getPassword();
			setCredential(p,host,user,password);
		}
	
}