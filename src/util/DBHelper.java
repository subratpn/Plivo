package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Singleton Class
public class DBHelper {
	
	
	private static DBHelper dbHelper;
	private static Connection connection;
	
	private static final String DB_URL = "localhost:3306";
	private static final String DB_USERNAME = "root";
	private static final String DB_PASSWORD = "";
	private static final String DB_NAME = "plivo_db";
	
	
	
	
	private DBHelper() {
		
	}
	
	public static DBHelper getInstance() {
		
		if(dbHelper == null) {
			dbHelper = new DBHelper();
		}
		
		return dbHelper;
	}
	
	
	public Connection getConnection() throws ClassNotFoundException, SQLException {
		
		if(connection==null) {
			
			System.out.println("Created Connection");
			
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(  
					"jdbc:mysql://"+DB_URL+"/"+DB_NAME,DB_USERNAME,DB_PASSWORD);
			
		}
		
		
		return connection;
	}
	
	
	public void closeDBConnection() throws SQLException {
		
		if(connection!=null) {
			connection.close();
		}
	}
	
	
	

}
