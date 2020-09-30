package com.kolovanja.Spigot2VKPlugin;
import java.sql.*;

public class MysqlCon {
	static String dbHost;
	static int dbPort;
	static String dbName;
	static String dbLogin;
	static String dbPass;
	
	public static void MysqlConSet(String host, int port, String database, String login, String password) {
		dbHost = host;
		dbPort = port;
		dbName = database;
		dbLogin = login; 
		dbPass = password;
	}
	public static int getUserCount(){  
		int count = 0;
		try{  
			Class.forName("com.mysql.jdbc.Driver");  
			String connString = "jdbc:mysql://"+dbHost+":"+dbPort+"/"+dbName+"";

			Connection con=DriverManager.getConnection(  
					connString,dbLogin,dbPass);  
			
			Statement stmt=con.createStatement();  
			ResultSet rs=stmt.executeQuery("select count(*) FROM `authme`.`authme`");   	
			while(rs.next())
			count = rs.getInt(1);

			con.close();  
		}catch(Exception e){ System.out.println(e);}  
		return count;
	}  

}
