package grails4membase



import com.samsung.cloudpi.service.MysqlService;
import java.sql.*;
import javax.sql.*;


class HelloController {
	def   List<Host> hosts   = new ArrayList<Host>()
	def String msg = "";
	def String result="";
	def String env = "";
	def index() {
		MysqlService.init();
		hosts = MysqlService.getMysqlHostList("master");
	}
	def set(){
		//def MemcachedClient client = MembaseService.getMemBaseSource(params.hostname);
		//client.add(params.key, 0, params.value);
		//msg = "success"
		//MysqlService.init();
                def String host = params.hostname;
                def String s_key = params.key;
                def String s_value = params.value;
                def java.sql.Connection conn = MysqlService.getMysqlSourcesByName(host).getConnection();
                System.out.println("host:"+host+"  connection:"+conn);
                conn.setAutoCommit(true);
		def java.sql.Statement stmt = conn.createStatement();	
                stmt.executeUpdate("drop database if exists testing");
                stmt.executeUpdate("create database testing");
                stmt.executeUpdate("use testing");
                stmt.executeUpdate("create table t1(k varchar(100), v varchar(100))");

		def PreparedStatement pstmt = conn.prepareStatement("insert into t1 values (?, ?)");
                pstmt.setString(1, s_key);
                pstmt.setString(2, s_value);
                pstmt.executeUpdate();
//                stmt.executeUpdate("insert into t1 values (" + s_key+ ", " + s_value + ")");

                stmt.close();
                pstmt.close();
                conn.close();

                msg = "success";

	}
	def get(){
		hosts = MysqlService.getMysqlHostList("master");
	}
	def doGet(){
		//def MemcachedClient client = MembaseService.getMemBaseSource(params.hostname);
		//result = "the value of key ["+params.key+"] is "+client.get(params.key)
		//return result
                def String host = params.hostname;
                def String s_key = params.key;
                def java.sql.Connection conn = MysqlService.getMysqlSourcesByName(host).getConnection();
                def java.sql.Statement stmt = conn.createStatement();
		def PreparedStatement pstmt = conn.prepareStatement("select v from t1 where k = ?");

		stmt.executeUpdate("use testing");
                pstmt.setString(1, s_key);
                def java.sql.ResultSet rs = pstmt.executeQuery();
                rs.next();
                result = rs.getString(1);
                
		rs.close();
                stmt.close();
		pstmt.close();
	        return result;
	}
	def env(){
		def StringBuffer sb= new StringBuffer()
		for (Map.Entry<String, String> envvar : System.getenv().entrySet()) {
			sb.append(envvar.getKey() + ": " + envvar.getValue()).append("\r\n");
		}
		env = sb.toString()
	}
}
