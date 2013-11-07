package play.modules.playrabbitmodule;
import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.db.DB;
import com.samsung.cloudpi.service.CloudpiService;
import com.samsung.cloudpi.service.RabbitMQService;
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

	@Override
	public void onApplicationStart() 
	{     
		Logger.info("Yeeha, firstmodule started");   
		 Properties p = Play.configuration;
				rabbitServiceConfig(p);
       
	}


    private void setCredential(Properties p, String url, String username, String password) {
        p.put("rabbitmq.host", url);
        p.put("rabbitmq.username", username);
        p.put("rabbitmq.password", password);
    }

	
		public  void rabbitServiceConfig(Properties p)
		{
			System.out.println("start load config");
			CloudpiService.init();
			List<Credential> list=RabbitMQService.getRabbitMQCredentialList();
			Credential credential=list.get(new Random().nextInt(list.size()));
			String host=credential.getHost();
					System.out.println("host="+host);
			String user=credential.getUsername();
				System.out.println("user="+user);
			String password=credential.getPassword();
				System.out.println("password="+password);
			setCredential(p,host,user,password);
		}
	
}