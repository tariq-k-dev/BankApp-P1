package bankapp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
	public static Connection dbConnection() {
		final Credentials cred = new Credentials();
		final String dbUrl = cred.getDbUrl();
		final String userName = cred.getUserName();
		final String pwd = cred.getPwd();
		
		try {
			Connection conn = DriverManager.getConnection(dbUrl, userName, pwd);
			// System.out.println("Database connection established...");
			
			return conn;
		} catch (SQLException e) {
	        System.out.println("Connection failure.");
	        e.printStackTrace();
	    }
		
		return null;
	}
}

